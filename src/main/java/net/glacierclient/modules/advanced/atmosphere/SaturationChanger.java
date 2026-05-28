package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class SaturationChanger extends GlacierMod {

    private final NumberSetting saturation = new NumberSetting("Saturation", "Color saturation multiplier", 0.0, 3.0, 1.0);
    private final NumberSetting contrast = new NumberSetting("Contrast", "Contrast multiplier", 0.5, 2.0, 1.0);
    private final NumberSetting brightness = new NumberSetting("Brightness", "Brightness multiplier", 0.5, 2.0, 1.0);
    private final BooleanSetting nightVision = new BooleanSetting("Night Vision", "Enable night vision effect", false);

    public SaturationChanger() {
        super("Saturation", "Adjust world color saturation/vibrance", Category.RENDER);
        addSettings(saturation, contrast, brightness, nightVision);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
    @Override public void onTick() {}

    public float getSaturation() { return (float)(double) saturation.getValue(); }
    public float getContrast() { return (float)(double) contrast.getValue(); }
    public float getBrightness() { return (float)(double) brightness.getValue(); }
    public boolean isNightVisionEnabled() { return nightVision.getValue(); }
}
