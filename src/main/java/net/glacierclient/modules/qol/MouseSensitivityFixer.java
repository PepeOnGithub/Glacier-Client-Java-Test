package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class MouseSensitivityFixer extends GlacierMod {

    private final NumberSetting sensitivityMultiplier = new NumberSetting("Sensitivity Multiplier", "Mouse sensitivity multiplier", 0.1, 5.0, 1.0);
    private final BooleanSetting rawInput = new BooleanSetting("Raw Input", "Use raw mouse input", true);
    private final BooleanSetting subpixelSmoothing = new BooleanSetting("Subpixel Smoothing", "Apply subpixel smoothing", false);
    private final NumberSetting acceleration = new NumberSetting("Acceleration", "Mouse acceleration factor", 0.0, 2.0, 0.0);

    public MouseSensitivityFixer() {
        super("Mouse Sensitivity Fixer", "Fine-tune mouse sensitivity and input", Category.QOL);
        addSettings(sensitivityMultiplier, rawInput, subpixelSmoothing, acceleration);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public double applyToInput(double rawDelta) {
        double result = rawDelta * sensitivityMultiplier.getValue();
        if (acceleration.getValue() > 0.0) {
            result += Math.signum(result) * Math.abs(result) * acceleration.getValue();
        }
        return result;
    }

    public boolean isRawInput() { return rawInput.getValue(); }
    public boolean isSubpixelSmoothing() { return subpixelSmoothing.getValue(); }
}
