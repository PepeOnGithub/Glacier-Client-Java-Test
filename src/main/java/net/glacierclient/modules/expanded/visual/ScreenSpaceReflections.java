package net.glacierclient.modules.expanded.visual;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class ScreenSpaceReflections extends GlacierMod {

    private final BooleanSetting onWater = new BooleanSetting("On Water", "Apply SSR on water surfaces", true);
    private final BooleanSetting onGlass = new BooleanSetting("On Glass", "Apply SSR on glass surfaces", false);
    private final NumberSetting quality = new NumberSetting("Quality", "SSR quality level (1=low, 4=ultra)", 2, 1, 4);
    private final NumberSetting intensity = new NumberSetting("Intensity", "Reflection blend intensity", 0.5, 0.1, 1.0);
    private final NumberSetting maxSteps = new NumberSetting("Max Steps", "Maximum ray march steps per pixel", 48, 16, 128);

    public ScreenSpaceReflections() {
        super("SSR", "Screen space reflections on water and glass", Category.RENDER);
        addSettings(onWater, onGlass, quality, intensity, maxSteps);
    }

    @Override
    public void onEnable() {
        // Register SSR post-process pass
    }

    @Override
    public void onDisable() {
        // Remove SSR post-process pass
    }

    public boolean isOnWater() { return onWater.getValue(); }
    public boolean isOnGlass() { return onGlass.getValue(); }
    public int getQuality() { return (int) quality.getValue(); }
    public double getIntensity() { return intensity.getValue(); }
    public int getMaxSteps() { return (int) maxSteps.getValue(); }
}
