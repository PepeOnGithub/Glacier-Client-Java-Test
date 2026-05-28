package net.glacierclient.launcher.install;

import net.glacierclient.launcher.cdn.CDNClient;
import net.glacierclient.launcher.cdn.VersionEntry;
import java.io.IOException;
import java.nio.file.*;
import java.util.function.Consumer;

public final class ClientInstaller {
    private static final Path GLACIER_DIR = Path.of(System.getProperty("user.home"), ".glacier");
    private static final Path VERSIONS_DIR = GLACIER_DIR.resolve("versions");

    private final CDNClient cdn;

    public ClientInstaller(CDNClient cdn) {
        this.cdn = cdn;
    }

    public Path getVersionsDir() { return VERSIONS_DIR; }

    public boolean isInstalled(VersionEntry entry) {
        return cdn.isInstalled(entry, VERSIONS_DIR);
    }

    public Path getJarPath(VersionEntry entry) {
        return VERSIONS_DIR.resolve(entry.getId()).resolve(entry.getLocalJarName());
    }

    public void install(VersionEntry entry, Consumer<String> statusUpdate, Consumer<Double> progressUpdate) throws Exception {
        Path versionDir = VERSIONS_DIR.resolve(entry.getId());
        Files.createDirectories(versionDir);

        statusUpdate.accept("Downloading Glacier " + entry.getId() + "...");

        cdn.downloadJar(entry, versionDir, bytesReceived -> {
            if (progressUpdate != null) {
                progressUpdate.accept(Math.min(1.0, bytesReceived / (10.0 * 1024 * 1024)));
            }
        });

        statusUpdate.accept("Installed Glacier " + entry.getId());
    }

    public void uninstall(VersionEntry entry) throws IOException {
        Path jar = getJarPath(entry);
        Files.deleteIfExists(jar);
    }
}
