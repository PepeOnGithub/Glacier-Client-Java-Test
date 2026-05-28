package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class HurtCamIntensitySlider extends GlacierMod {

    private final NumberSetting intensity = new NumberSetting("Intensity", "Camera shake intensity when hurt", 0.0, 2.0, 0.5);
    private final BooleanSetting disableOnCrit = new BooleanSetting("Disable On Crit", "Disable shake on critical hits", false);

    public HurtCamIntensitySlider() {
        super("Hurt Cam", "Control the screen shake intensity when taking damage", Category.RENDER);
        addSettings(intensity, disableOnCrit);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public float getIntensity(boolean isCrit) {
        if (isCrit && disableOnCrit.getValue()) return 0f;
        return (float)(double) intensity.getValue();
    }
}
