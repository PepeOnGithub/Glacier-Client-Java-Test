package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class ScrollSpeedMultiplier extends GlacierMod {

    private final NumberSetting multiplier = new NumberSetting("Multiplier", "Scroll speed multiplier", 0.1, 10.0, 3.0);
    private final BooleanSetting invertScroll = new BooleanSetting("Invert Scroll", "Invert scroll direction", false);
    private final BooleanSetting smoothScroll = new BooleanSetting("Smooth Scroll", "Enable smooth scrolling", true);

    public ScrollSpeedMultiplier() {
        super("Scroll Speed Multiplier", "Adjust scroll speed in menus and game", Category.QOL);
        addSettings(multiplier, invertScroll, smoothScroll);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public double applyToScroll(double amount) {
        double result = amount * multiplier.getValue();
        if (invertScroll.getValue()) result = -result;
        return result;
    }

    public boolean isSmoothScroll() { return smoothScroll.getValue(); }
}
