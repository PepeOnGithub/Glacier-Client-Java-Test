package net.glacierclient.web;

import com.labymedia.ultralight.plugin.filesystem.UltralightFileSystem;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Serves the web UI directly from the mod jar ({@code assets/glacierclient/web/}) — no extraction to
 * disk. Ultralight requests {@code file:///title.html} etc.; we map those onto classpath resources.
 *
 * <p>Ultralight also loads its <em>own</em> engine resources (ICU data {@code resources/icudt67l.dat},
 * {@code resources/cacert.pem}) through this same file system. Those ship in the native SDK and are
 * unpacked to {@code <run>/glacier/ultralight/resources/}; without them ICU is missing and the text /
 * layout engine produces a blank view. So any {@code resources/*} request is served from that on-disk
 * SDK directory, while everything else is served from the classpath web bundle.</p>
 */
public class GlacierFileSystem implements UltralightFileSystem {

    private static final String BASE = "/assets/glacierclient/web";

    /** On-disk SDK directory ({@code <run>/glacier/ultralight}) holding the {@code resources/} folder. */
    private final Path sdkDir;

    private long nextHandle = 1;
    private final Map<Long, byte[]> data = new HashMap<>();
    private final Map<Long, Integer> pos = new HashMap<>();

    public GlacierFileSystem(Path sdkDir) {
        this.sdkDir = sdkDir;
    }

    private static String clean(String path) {
        if (path == null) return null;
        if (path.startsWith("file:///")) path = path.substring(8);
        else if (path.startsWith("/")) path = path.substring(1);
        int q = path.indexOf('?'); if (q >= 0) path = path.substring(0, q);
        int h = path.indexOf('#'); if (h >= 0) path = path.substring(0, h);
        return path;
    }

    private static String resource(String path) {
        return BASE + "/" + path;
    }

    /** SDK engine resources (icudt67l.dat, cacert.pem) live on disk, not on the classpath. */
    private Path diskPath(String path) {
        return (sdkDir != null && path.startsWith("resources/")) ? sdkDir.resolve(path) : null;
    }

    @Override
    public boolean fileExists(String path) {
        String p = clean(path);
        if (p == null) return false;
        Path disk = diskPath(p);
        if (disk != null && Files.exists(disk)) return true;
        return GlacierFileSystem.class.getResource(resource(p)) != null;
    }

    @Override
    public long getFileSize(long handle) {
        byte[] b = data.get(handle);
        return b == null ? -1 : b.length;
    }

    @Override
    public String getFileMimeType(String path) {
        String p = path.toLowerCase();
        if (p.endsWith(".html")) return "text/html";
        if (p.endsWith(".css")) return "text/css";
        if (p.endsWith(".js")) return "application/javascript";
        if (p.endsWith(".json")) return "application/json";
        if (p.endsWith(".png")) return "image/png";
        if (p.endsWith(".ttf")) return "font/ttf";
        if (p.endsWith(".woff2")) return "font/woff2";
        return "application/octet-stream";
    }

    @Override
    public long openFile(String path, boolean openForWriting) {
        String p = clean(path);
        if (p == null) return INVALID_FILE_HANDLE;
        try {
            byte[] bytes = null;
            Path disk = diskPath(p);
            if (disk != null && Files.exists(disk)) {
                bytes = Files.readAllBytes(disk);
            } else {
                try (InputStream in = GlacierFileSystem.class.getResourceAsStream(resource(p))) {
                    if (in != null) bytes = in.readAllBytes();
                }
            }
            if (bytes == null) return INVALID_FILE_HANDLE;
            long handle = nextHandle++;
            data.put(handle, bytes);
            pos.put(handle, 0);
            return handle;
        } catch (Exception e) {
            return INVALID_FILE_HANDLE;
        }
    }

    @Override
    public void closeFile(long handle) {
        data.remove(handle);
        pos.remove(handle);
    }

    @Override
    public long readFromFile(long handle, ByteBuffer out, long length) {
        byte[] b = data.get(handle);
        if (b == null) return -1;
        int p = pos.getOrDefault(handle, 0);
        int n = (int) Math.min(Math.min(length, b.length - p), out.remaining());
        if (n <= 0) return 0;
        out.put(b, p, n);
        pos.put(handle, p + n);
        return n;
    }
}
