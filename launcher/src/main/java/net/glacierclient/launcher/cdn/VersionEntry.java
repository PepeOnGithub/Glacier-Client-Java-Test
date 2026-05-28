package net.glacierclient.launcher.cdn;

public final class VersionEntry {
    private String id;
    private String name;
    private String releaseDate;
    private String tag;
    private String url;
    private String sha256;
    private boolean fabric;
    private boolean forge;
    private int javaVersion;
    private String changelog;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getReleaseDate() { return releaseDate; }
    public String getTag() { return tag; }
    public String getUrl() { return url; }
    public String getSha256() { return sha256; }
    public boolean isFabric() { return fabric; }
    public boolean isForge() { return forge; }
    public int getJavaVersion() { return javaVersion; }
    public String getChangelog() { return changelog; }

    public String getLoaderLabel() {
        if (fabric) return "Fabric";
        if (forge) return "Forge";
        return "Unknown";
    }

    public String getLocalJarName() {
        return "Glacier-" + id + ".jar";
    }
}
