package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.settings.ModeSetting;

public class Fullbright extends GlacierMod {

    // Brightness is fed straight into the lightmap by MixinLightmapTextureManager (which bypasses the
    // vanilla 0..1 gamma clamp). We must NOT write this into the real options.gamma — that value only
    // accepts 0..1, so writing 100 spams "Illegal option value 100.0" and corrupts options.txt.
    private final NumberSetting gamma = new NumberSetting("Brightness", "Fullbright intensity (higher = brighter)", 1, 16, 10);
    private final ModeSetting mode = new ModeSetting("Mode", "Fullbright implementation", "Gamma", "Gamma", "Night Vision");

    public Fullbright() {
        super("Fullbright", "Forces full map brightness so caves and night are fully lit", Category.RENDER);
        addSettings(gamma, mode);
    }

    /** True when the gamma-based fullbright path is active (read by MixinLightmapTextureManager). */
    public boolean isGammaMode() { return "Gamma".equals(mode.getValue()); }

    /** The brightness multiplier fed into the lightmap (bypasses the vanilla 0..1 gamma clamp). */
    public double getBrightness() { return gamma.getValue(); }
}
