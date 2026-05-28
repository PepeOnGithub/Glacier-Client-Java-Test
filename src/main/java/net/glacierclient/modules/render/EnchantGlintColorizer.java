package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;

public class EnchantGlintColorizer extends GlacierMod {

    private final ColorSetting glintColor = new ColorSetting("Glint Color", "Color of the enchantment glint", 0xFF7289DA);
    private final NumberSetting glintSpeed = new NumberSetting("Glint Speed", "Speed of the glint animation", 0.1, 5.0, 1.0);
    private final BooleanSetting rainbowGlint = new BooleanSetting("Rainbow Glint", "Enable rainbow cycling glint", false);
    private final NumberSetting glintAlpha = new NumberSetting("Glint Alpha", "Opacity of the glint", 0, 255, 160);

    private float hue = 0f;

    public EnchantGlintColorizer() {
        super("Glint Colorizer", "Change the enchantment glint color on items", Category.RENDER);
        addSettings(glintColor, glintSpeed, rainbowGlint, glintAlpha);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        if (rainbowGlint.getValue()) {
            hue = (hue + 0.01f * (float)(double) glintSpeed.getValue()) % 1f;
        }
    }

    public int getCurrentGlintColor() {
        if (rainbowGlint.getValue()) {
            int rgb = java.awt.Color.HSBtoRGB(hue, 1f, 1f);
            int alpha = (int)(double) glintAlpha.getValue();
            return (alpha << 24) | (rgb & 0x00FFFFFF);
        }
        int base = glintColor.getValue() & 0x00FFFFFF;
        return ((int)(double) glintAlpha.getValue() << 24) | base;
    }
}
