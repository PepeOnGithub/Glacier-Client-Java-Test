package net.glacierclient.core.settings;

public class ColorSetting extends Setting<Integer> {

    private boolean allowAlpha;

    public ColorSetting(String name, String description, int defaultColor) {
        this(name, description, defaultColor, true);
    }

    public ColorSetting(String name, String description, int defaultColor, boolean allowAlpha) {
        super(name, description, defaultColor);
        this.allowAlpha = allowAlpha;
    }

    public int getRed() { return (value >> 16) & 0xFF; }
    public int getGreen() { return (value >> 8) & 0xFF; }
    public int getBlue() { return value & 0xFF; }
    public int getAlpha() { return (value >> 24) & 0xFF; }

    public String toHex() {
        if (allowAlpha) return String.format("#%08X", value);
        return String.format("#%06X", value & 0xFFFFFF);
    }

    public boolean isAllowAlpha() { return allowAlpha; }

    @Override
    public String getTypeName() { return "color"; }
}
