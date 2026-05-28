package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class RainbowGUIMode extends GlacierMod {

    private final NumberSetting speed = new NumberSetting("Speed", "Rainbow animation speed", 0.1, 5.0, 1.0);
    private final NumberSetting saturation = new NumberSetting("Saturation", "Rainbow saturation", 0.5, 1.0, 0.8);
    private final BooleanSetting affectText = new BooleanSetting("Affect Text", "Apply rainbow to text", true);
    private final BooleanSetting affectBorders = new BooleanSetting("Affect Borders", "Apply rainbow to borders", true);

    private float hue = 0;

    public RainbowGUIMode() {
        super("Rainbow GUI", "Animate GUI elements with rainbow colors", Category.RENDER);
        addSettings(speed, saturation, affectText, affectBorders);
    }

    @Override public void onEnable() { hue = 0; }
    @Override public void onDisable() {}

    @Override
    public void onTick() {
        hue += (float) (speed.getValue() * 0.005);
        if (hue > 1f) hue -= 1f;
    }

    public int getRainbowColor(float offset) {
        float h = (hue + offset) % 1f;
        float s = (float) saturation.getValue();
        return java.awt.Color.HSBtoRGB(h, s, 1.0f) | 0xFF000000;
    }

    public boolean shouldAffectText() { return affectText.getValue(); }
    public boolean shouldAffectBorders() { return affectBorders.getValue(); }
}
