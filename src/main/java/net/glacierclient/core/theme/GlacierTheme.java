package net.glacierclient.core.theme;

public final class GlacierTheme {

    private GlacierTheme() {}

    public static final int ACCENT        = 0xFF7289DA;
    public static final int ACCENT_HOVER  = 0xFF8EA0E0;
    public static final int ACCENT_GLOW   = 0x6B7289DA;
    public static final int ACCENT_BG     = 0x1A7289DA;
    public static final int BG            = 0xFF23272A;
    public static final int BG_PANEL      = 0xFF2C2F33;
    public static final int BG_ITEM       = 0x0AFFFFFF;
    public static final int BG_ITEM_HOVER = 0x13FFFFFF;
    public static final int TEXT          = 0xFFFFFFFF;
    public static final int TEXT_DIM      = 0xFF99AAB5;
    public static final int RED           = 0xFFF04747;
    public static final int GREEN         = 0xFF43B581;
    public static final int ORANGE        = 0xFFFAA61A;

    public static final int RADIUS_SM     = 8;
    public static final int RADIUS_MD     = 12;
    public static final long TRANSITION   = 150L;
    public static final int BLUR          = 14;

    public static int withAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    public static int lerp(int colorA, int colorB, float t) {
        int ar = (colorA >> 16) & 0xFF, ag = (colorA >> 8) & 0xFF, ab = colorA & 0xFF;
        int br = (colorB >> 16) & 0xFF, bg = (colorB >> 8) & 0xFF, bb = colorB & 0xFF;
        int r = (int)(ar + (br - ar) * t);
        int g = (int)(ag + (bg - ag) * t);
        int b = (int)(ab + (bb - ab) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
