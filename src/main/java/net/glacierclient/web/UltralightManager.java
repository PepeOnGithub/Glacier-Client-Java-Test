package net.glacierclient.web;

import com.labymedia.ultralight.UltralightJava;
import com.labymedia.ultralight.UltralightPlatform;
import com.labymedia.ultralight.UltralightRenderer;
import com.labymedia.ultralight.UltralightView;
import com.labymedia.ultralight.config.FontHinting;
import com.labymedia.ultralight.config.UltralightConfig;
import com.labymedia.ultralight.config.UltralightViewConfig;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Lazy, guarded bootstrap for the Ultralight HTML engine. The Ultralight SDK native binaries are
 * bundled in the jar ({@code assets/glacierclient/natives/ultralight-<platform>.zip}) and unpacked
 * once into {@code <run>/glacier/ultralight/} on a background thread; the engine is then initialised
 * on the render thread (sharing Minecraft's GLFW/GL context). Everything is guarded — if the platform
 * is unsupported or anything fails, the web UI stays unavailable and the native screens are used.
 */
public final class UltralightManager {

    // Windows SDK DLLs in dependency order (must be System.load-ed before the JNI wrapper so the OS
    // resolves WebCore/UltralightCore/etc. to already-loaded modules rather than searching the cwd).
    private static final String[] WIN_LOAD_ORDER = {
            "UltralightCore.dll", "glib-2.0-0.dll", "gthread-2.0-0.dll", "gmodule-2.0-0.dll",
            "gobject-2.0-0.dll", "gio-2.0-0.dll", "gstreamer-full-1.0.dll",
            "WebCore.dll", "Ultralight.dll", "AppCore.dll"
    };

    private static UltralightManager instance;

    private boolean ready = false;
    private boolean failed = false;
    private volatile boolean extracting = false;
    private volatile boolean extractFailed = false;

    private UltralightRenderer renderer;

    public static UltralightManager get() {
        if (instance == null) instance = new UltralightManager();
        return instance;
    }

    public boolean isReady() { return ready; }
    public boolean isExtracting() { return extracting; }

    /** True while the engine is still being provisioned (extraction or not-yet-failed). */
    public boolean isPending() { return !ready && !failed && !extractFailed; }

    /** Whether the web UI can run on this platform at all (bundled natives exist). */
    public static boolean isSupported() { return bundleResource() != null; }

    /**
     * Web UI is ON by default. Overrides: JVM flag {@code -Dglacier.webui=false} or a kill-switch file
     * {@code glacier/webui.disable} in the run directory force the native screens (escape hatch if the
     * Ultralight engine fails to init on a given machine).
     */
    public static boolean isEnabled() {
        String prop = System.getProperty("glacier.webui");
        if (prop != null) return Boolean.parseBoolean(prop);
        try {
            if (Files.exists(MinecraftClient.getInstance().runDirectory.toPath()
                    .resolve("glacier").resolve("webui.disable"))) return false;
        } catch (Throwable ignored) {}
        return true;
    }

    /**
     * Kick off native extraction as early as possible (mod init), off the render thread, so the engine
     * can initialise the instant the first title screen appears. Does not touch GL.
     */
    public void prefetch() {
        if (ready || failed || extractFailed || extracting || !isSupported() || !isEnabled()) return;
        try {
            Path dir = MinecraftClient.getInstance().runDirectory.toPath().resolve("glacier").resolve("ultralight");
            Files.createDirectories(dir);
            if (!sdkPresent(dir)) startExtract(dir);
        } catch (Throwable t) {
            System.err.println("[Glacier] Ultralight prefetch error: " + t);
        }
    }

    /** Called from the render thread. Returns true only once the engine is fully ready. */
    public boolean ensureInit() {
        if (ready) return true;
        if (failed || extractFailed || !isEnabled()) return false;
        try {
            Path dir = MinecraftClient.getInstance().runDirectory.toPath().resolve("glacier").resolve("ultralight");
            Files.createDirectories(dir);
            if (!sdkPresent(dir)) {
                if (!extracting) startExtract(dir);
                return false; // unpacking natives → native UI for now
            }
            return initEngine(dir);
        } catch (Throwable t) {
            failed = true;
            System.err.println("[Glacier] Ultralight init error: " + t);
            return false;
        }
    }

    // ---- bundled native provisioning ----
    private static boolean isWindows() { return System.getProperty("os.name", "").toLowerCase().contains("win"); }

    private static String bundleResource() {
        if (isWindows()) return "/assets/glacierclient/natives/ultralight-win-x64.zip";
        return null; // only Windows is bundled for now
    }

    private boolean sdkPresent(Path dir) {
        return isWindows() && Files.exists(dir.resolve("UltralightCore.dll"));
    }

    private void startExtract(Path dir) {
        String res = bundleResource();
        if (res == null) { extractFailed = true; System.err.println("[Glacier] No bundled Ultralight natives for this platform."); return; }
        extracting = true;
        Thread t = new Thread(() -> {
            try (InputStream in = UltralightManager.class.getResourceAsStream(res)) {
                if (in == null) throw new IllegalStateException("missing bundled natives: " + res);
                try (ZipInputStream zip = new ZipInputStream(in)) {
                    ZipEntry e;
                    byte[] buf = new byte[1 << 16];
                    while ((e = zip.getNextEntry()) != null) {
                        if (e.isDirectory()) continue;
                        Path target = dir.resolve(e.getName());
                        Files.createDirectories(target.getParent());
                        try (OutputStream out = Files.newOutputStream(target)) {
                            int n;
                            while ((n = zip.read(buf)) > 0) out.write(buf, 0, n);
                        }
                    }
                }
                System.out.println("[Glacier] Ultralight natives unpacked to " + dir);
            } catch (Throwable e) {
                extractFailed = true;
                System.err.println("[Glacier] Ultralight native unpack failed: " + e);
            } finally {
                extracting = false;
            }
        }, "Glacier-Ultralight-Unpack");
        t.setDaemon(true);
        t.start();
    }

    // ---- engine init (render thread) ----
    private boolean initEngine(Path dir) {
        try {
            String libPath = System.getProperty("java.library.path", "");
            System.setProperty("java.library.path",
                    libPath.isEmpty() ? dir.toAbsolutePath().toString()
                                      : libPath + File.pathSeparator + dir.toAbsolutePath());

            // Pre-load the SDK engine DLLs in dependency order so the JNI wrapper resolves them.
            if (isWindows()) {
                for (String name : WIN_LOAD_ORDER) {
                    Path p = dir.resolve(name);
                    if (Files.exists(p)) {
                        try { System.load(p.toAbsolutePath().toString()); }
                        catch (Throwable t) { System.err.println("[Glacier] preload " + name + " failed: " + t); }
                    }
                }
            }

            UltralightJava.extractNativeLibrary(dir);
            UltralightJava.load(dir);

            UltralightPlatform platform = UltralightPlatform.instance();
            platform.setConfig(new UltralightConfig()
                    .forceRepaint(true)   // repaint the whole view each frame — stops the CPU-surface jitter
                    .fontHinting(FontHinting.SMOOTH));
            platform.usePlatformFontLoader();
            platform.setFileSystem(new GlacierFileSystem(dir));
            platform.setLogger((level, message) -> {});

            // CPU/bitmap rendering — no GPU driver, so no conflict with Minecraft's GL context.
            this.renderer = UltralightRenderer.create();
            this.ready = true;
            System.out.println("[Glacier] Ultralight initialised.");
            return true;
        } catch (Throwable t) {
            failed = true;
            System.err.println("[Glacier] Ultralight engine init failed: " + t);
            return false;
        }
    }

    public UltralightView createView(int width, int height) {
        return renderer.createView(Math.max(1, width), Math.max(1, height),
                new UltralightViewConfig()
                        .isAccelerated(false)   // CPU bitmap surface
                        .initialDeviceScale(1.0)
                        .isTransparent(true));
    }

    public void update() {
        if (renderer != null) {
            renderer.update();
            renderer.render();
        }
    }
}
