package net.glacierclient.modules.expanded.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class AutoRefillFromBundle extends GlacierMod {

    private final BooleanSetting showIndicator = new BooleanSetting("Show Indicator", "Display visual indicator for refill state", false);
    private final BooleanSetting playSound = new BooleanSetting("Play Sound", "Play sound when refill occurs", false);
    private final NumberSetting threshold = new NumberSetting("Threshold", "Stack count below which to trigger refill", 4, 1, 8);

    public AutoRefillFromBundle() {
        super("Bundle Refill", "Visual indicator for bundle auto-refill of hotbar slots", Category.QOL);
        addSettings(showIndicator, playSound, threshold);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public boolean isShowIndicator() { return showIndicator.getValue(); }
    public boolean isPlaySound() { return playSound.getValue(); }
    public int getThreshold() { return (int)(double) threshold.getValue(); }
}
