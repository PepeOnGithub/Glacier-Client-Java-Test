package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class FireIntensitySlider extends GlacierMod {

    private final NumberSetting intensity = new NumberSetting("Intensity", "On-screen fire overlay size", 0.0, 1.0, 0.2);
    private final BooleanSetting removeFire = new BooleanSetting("Remove Fire", "Completely remove fire overlay", false);

    public FireIntensitySlider() {
        super("Fire Intensity", "Reduce on-screen fire overlay size", Category.PVP);
        addSettings(intensity, removeFire);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public float getFireScale() {
        if (removeFire.getValue()) return 0f;
        return (float)(double) intensity.getValue();
    }
}
