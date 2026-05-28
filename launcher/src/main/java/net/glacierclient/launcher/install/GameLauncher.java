package net.glacierclient.launcher.install;

import net.glacierclient.launcher.cdn.VersionEntry;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public final class GameLauncher {
    private static final Path MINECRAFT_DIR = resolveMinecraftDir();
    private static final Path GLACIER_DIR = Path.of(System.getProperty("user.home"), ".glacier");

    private final ClientInstaller installer;

    public GameLauncher(ClientInstaller installer) {
        this.installer = installer;
    }

    public void launch(VersionEntry entry, String javaPath, int ramMb) throws IOException {
        Path clientJar = installer.getJarPath(entry);
        if (!Files.exists(clientJar)) {
            throw new IOException("Client JAR not found: " + clientJar);
        }

        String cp = buildClasspath(entry, clientJar);

        List<String> cmd = new ArrayList<>();
        cmd.add(javaPath.isBlank() ? resolveJavaBin() : javaPath);
        cmd.add("-Xmx" + ramMb + "M");
        cmd.add("-Xms" + Math.min(512, ramMb) + "M");
        cmd.add("-XX:+UseG1GC");
        cmd.add("-XX:+ParallelRefProcEnabled");
        cmd.add("-XX:MaxGCPauseMillis=200");
        cmd.add("-XX:+UnlockExperimentalVMOptions");
        cmd.add("-cp");
        cmd.add(cp);

        if (entry.isFabric()) {
            cmd.add("net.fabricmc.loader.launch.knot.KnotClient");
        } else {
            cmd.add("net.minecraft.launchwrapper.Launch");
            cmd.add("--tweakClass");
            cmd.add("net.glacierclient.launch.GlacierTweaker");
        }

        cmd.add("--gameDir");
        cmd.add(MINECRAFT_DIR.toString());
        cmd.add("--version");
        cmd.add(entry.getId());

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(MINECRAFT_DIR.toFile());
        pb.redirectErrorStream(true);
        pb.start();
    }

    private String buildClasspath(VersionEntry entry, Path clientJar) {
        StringBuilder cp = new StringBuilder(clientJar.toAbsolutePath().toString());
        Path librariesDir = MINECRAFT_DIR.resolve("libraries");
        Path versionLibs = MINECRAFT_DIR.resolve("versions").resolve(entry.getId());
        if (Files.exists(versionLibs)) {
            try {
                Files.walk(versionLibs)
                        .filter(p -> p.toString().endsWith(".jar"))
                        .forEach(p -> cp.append(File.pathSeparator).append(p.toAbsolutePath()));
            } catch (IOException ignored) {}
        }
        return cp.toString();
    }

    private static Path resolveMinecraftDir() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return Path.of(System.getenv().getOrDefault("APPDATA", System.getProperty("user.home")), ".minecraft");
        } else if (os.contains("mac")) {
            return Path.of(System.getProperty("user.home"), "Library", "Application Support", "minecraft");
        } else {
            return Path.of(System.getProperty("user.home"), ".minecraft");
        }
    }

    private static String resolveJavaBin() {
        String javaHome = System.getProperty("java.home");
        if (javaHome != null && !javaHome.isBlank()) {
            Path bin = Path.of(javaHome, "bin", "java");
            if (Files.exists(bin)) return bin.toString();
        }
        return "java";
    }
}
