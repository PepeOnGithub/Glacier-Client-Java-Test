package net.glacierclient.launcher.cdn;

import java.util.List;

public final class VersionManifest {
    private int schemaVersion;
    private String latestRelease;
    private String releaseDate;
    private List<VersionEntry> versions;
    private LauncherEntry launcher;

    public int getSchemaVersion() { return schemaVersion; }
    public String getLatestRelease() { return latestRelease; }
    public String getReleaseDate() { return releaseDate; }
    public List<VersionEntry> getVersions() { return versions; }
    public LauncherEntry getLauncher() { return launcher; }

    public static final class LauncherEntry {
        private String version;
        private String url;
        private String sha256;

        public String getVersion() { return version; }
        public String getUrl() { return url; }
        public String getSha256() { return sha256; }
    }
}
