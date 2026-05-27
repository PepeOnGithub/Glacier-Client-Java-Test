package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class FastChestOpenFix extends GlacierMod {

    private final BooleanSetting skipAnimation = new BooleanSetting("Skip Animation", "Skip chest open animation", true);
    private final BooleanSetting instantOpen = new BooleanSetting("Instant Open", "Instantly open chests", false);
    private final NumberSetting animationSpeed = new NumberSetting("Animation Speed", "Chest open animation speed", 0.5, 5.0, 3.0);

    public FastChestOpenFix() {
        super("Fast Chest Open Fix", "Optimize chest opening animations", Category.PERFORMANCE);
        addSettings(skipAnimation, instantOpen, animationSpeed);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isSkipAnimation() { return skipAnimation.getValue(); }
    public boolean isInstantOpen() { return instantOpen.getValue(); }
    public float getAnimationSpeed() { return (float) animationSpeed.getValue(); }
}
