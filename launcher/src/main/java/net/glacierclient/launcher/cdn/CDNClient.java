package net.glacierclient.launcher.cdn;

import com.google.gson.Gson;
import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;
import java.util.function.LongConsumer;

public final class CDNClient {
    private static final String MANIFEST_URL = "https://cdn.glacierclient.xyz/versions.json";
    private static final Gson GSON = new Gson();
    private final HttpClient http;

    public CDNClient() {
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public VersionManifest fetchManifest() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(MANIFEST_URL))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new IOException("Manifest fetch failed: HTTP " + resp.statusCode());
        }
        return GSON.fromJson(resp.body(), VersionManifest.class);
    }

    public void downloadJar(VersionEntry entry, Path destDir, LongConsumer progressBytes) throws Exception {
        Path destFile = destDir.resolve(entry.getLocalJarName());
        Files.createDirectories(destDir);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(entry.getUrl()))
                .timeout(Duration.ofMinutes(5))
                .GET()
                .build();

        HttpResponse<InputStream> resp = http.send(req, HttpResponse.BodyHandlers.ofInputStream());
        if (resp.statusCode() != 200) {
            throw new IOException("Download failed for " + entry.getId() + ": HTTP " + resp.statusCode());
        }

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        try (InputStream in = resp.body();
             OutputStream out = Files.newOutputStream(destFile)) {
            byte[] buf = new byte[8192];
            long total = 0;
            int read;
            while ((read = in.read(buf)) != -1) {
                out.write(buf, 0, read);
                sha256.update(buf, 0, read);
                total += read;
                if (progressBytes != null) progressBytes.accept(total);
            }
        }

        String actual = HexFormat.of().formatHex(sha256.digest());
        if (entry.getSha256() != null && !entry.getSha256().startsWith("0000") && !actual.equals(entry.getSha256())) {
            Files.deleteIfExists(destFile);
            throw new IOException("SHA256 mismatch for " + entry.getId() + ": expected " + entry.getSha256() + " got " + actual);
        }
    }

    public boolean isInstalled(VersionEntry entry, Path versionsDir) {
        return Files.exists(versionsDir.resolve(entry.getId()).resolve(entry.getLocalJarName()));
    }
}
