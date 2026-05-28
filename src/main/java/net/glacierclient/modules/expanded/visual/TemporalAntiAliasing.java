package net.glacierclient.modules.expanded.visual;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class TemporalAntiAliasing extends GlacierMod {

    private final BooleanSetting enabled = new BooleanSetting("Enabled", "Enable temporal anti-aliasing shader", false);
    private final NumberSetting strength = new NumberSetting("Strength", "TAA blend factor", 0.5, 0.1, 1.0);
    private final BooleanSetting jitter = new BooleanSetting("Jitter", "Apply sub-pixel jitter pattern", false);
    private final NumberSetting sharpness = new NumberSetting("Sharpness", "Post-TAA sharpening amount", 0.3, 0.0, 1.0);

    public TemporalAntiAliasing() {
        super("Temporal AA", "Lightweight TAA toggle via shader injection", Category.RENDER);
        addSettings(enabled, strength, jitter, sharpness);
    }

    @Override
    public void onEnable() {
        // Inject TAA post-process shader
    }

    @Override
    public void onDisable() {
        // Remove TAA shader
    }

    public boolean isEnabled() { return enabled.getValue(); }
    public double getStrength() { return strength.getValue(); }
    public boolean isJitter() { return jitter.getValue(); }
    public double getSharpness() { return sharpness.getValue(); }
}
