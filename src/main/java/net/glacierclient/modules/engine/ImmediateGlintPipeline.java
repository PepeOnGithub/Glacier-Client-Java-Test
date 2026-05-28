package net.glacierclient.modules.engine;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.theme.GlacierTheme;

public class ImmediateGlintPipeline extends GlacierMod {

    private final BooleanSetting singlePass = new BooleanSetting("Single Pass", "Use single-pass glint shader instead of multi-pass blending", true);
    private final NumberSetting glintScale = new NumberSetting("Glint Scale", "Scale factor for the glint pattern", 1.0, 0.1, 5.0);
    private final ColorSetting defaultGlintColor = new ColorSetting("Glint Color", "Default enchantment glint color", 0xFF7289DA);
    private final NumberSetting glintAlpha = new NumberSetting("Glint Alpha", "Transparency of the glint effect", 160, 0, 255);
    private final BooleanSetting animatedGlint = new BooleanSetting("Animated Glint", "Enable glint scroll animation", false);

    public ImmediateGlintPipeline() {
        super("Immediate Glint Pipeline", "Single-pass enchantment shader replacing multi-pass blending", Category.ENGINE);
        addSettings(singlePass, glintScale, defaultGlintColor, glintAlpha, animatedGlint);
    }

    @Override
    public void onEnable() {
        // Inject single-pass glint shader
    }

    @Override
    public void onDisable() {
        // Restore vanilla multi-pass glint
    }

    public boolean isSinglePass() { return singlePass.getValue(); }
    public double getGlintScale() { return glintScale.getValue(); }
    public int getDefaultGlintColor() { return defaultGlintColor.getValue(); }
    public int getGlintAlpha() { return (int) glintAlpha.getValue(); }
    public boolean isAnimatedGlint() { return animatedGlint.getValue(); }
}
