package net.glacierclient.common;

public final class GlacierVersion {

    public static final String CLIENT_VERSION = "1.0.0";
    public static final String CDN_BASE       = "https://cdn.glacierclient.xyz/builds";
    public static final String MANIFEST_URL   = "https://cdn.glacierclient.xyz/versions.json";

    public enum MinecraftVersion {
        V1_8_9("1.8.9",   "Glacier-1.8.9.jar",   false, false, false),
        V1_12_2("1.12.2", "Glacier-1.12.2.jar",  false, false, false),
        V1_16_5("1.16.5", "Glacier-1.16.5.jar",  true,  false, false),
        V1_20_4("1.20.4", "Glacier-1.20.4.jar",  true,  true,  false),
        V1_21_4("1.21.4", "Glacier-1.21.4.jar",  true,  true,  false),
        V1_21_11("1.21.11","Glacier-1.21.11.jar",true,  true,  true);

        public final String id;
        public final String jarName;
        public final boolean fabric;
        public final boolean drawContext;
        public final boolean mojmapDeobf;

        MinecraftVersion(String id, String jarName, boolean fabric, boolean drawContext, boolean mojmapDeobf) {
            this.id = id;
            this.jarName = jarName;
            this.fabric = fabric;
            this.drawContext = drawContext;
            this.mojmapDeobf = mojmapDeobf;
        }

        public String getCdnUrl() {
            return CDN_BASE + "/" + id + "/" + jarName;
        }
    }

    private GlacierVersion() {}
}
