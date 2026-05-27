package net.glacierclient.core.util;

public final class ColorUtil {

    private ColorUtil() {}

    public static int fromRGB(int r, int g, int b) { return 0xFF000000 | (r << 16) | (g << 8) | b; }
    public static int fromARGB(int a, int r, int g, int b) { return (a << 24) | (r << 16) | (g << 8) | b; }
    public static int withAlpha(int color, int alpha) { return (alpha << 24) | (color & 0x00FFFFFF); }
    public static int getAlpha(int color) { return (color >> 24) & 0xFF; }
    public static int getRed(int color) { return (color >> 16) & 0xFF; }
    public static int getGreen(int color) { return (color >> 8) & 0xFF; }
    public static int getBlue(int color) { return color & 0xFF; }

    public static int rainbow(long offset) {
        float hue = ((System.currentTimeMillis() + offset) % 2000) / 2000.0f;
        return 0xFF000000 | (java.awt.Color.HSBtoRGB(hue, 0.8f, 1.0f));
    }

    public static int fromHex(String hex) {
        hex = hex.replace("#", "");
        if (hex.length() == 6) hex = "FF" + hex;
        return (int) Long.parseLong(hex, 16);
    }

    public static String toHex(int color) {
        return String.format("#%08X", color);
    }
}
