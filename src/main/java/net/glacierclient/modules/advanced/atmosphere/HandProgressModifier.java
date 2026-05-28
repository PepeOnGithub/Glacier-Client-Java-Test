package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class HandProgressModifier extends GlacierMod {

    private final NumberSetting swayIntensity = new NumberSetting("Sway Intensity", "Hand sway multiplier", 0.0, 2.0, 1.0);
    private final NumberSetting bobbingIntensity = new NumberSetting("Bobbing Intensity", "Hand bobbing multiplier", 0.0, 2.0, 1.0);
    private final BooleanSetting removeSquish = new BooleanSetting("Remove Squish", "Remove hand squish animation", false);
    private final NumberSetting zoomLevel = new NumberSetting("Zoom Level", "Item zoom in view", 0.5, 1.5, 1.0);

    public HandProgressModifier() {
        super("Hand Progress", "Modify hand sway and bobbing animations", Category.RENDER);
        addSettings(swayIntensity, bobbingIntensity, removeSquish, zoomLevel);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
    @Override public void onTick() {}

    public float getSwayIntensity() { return (float) swayIntensity.getValue(); }
    public float getBobbingIntensity() { return (float) bobbingIntensity.getValue(); }
    public boolean isSquishRemoved() { return removeSquish.getValue(); }
    public float getZoomLevel() { return (float) zoomLevel.getValue(); }
}
