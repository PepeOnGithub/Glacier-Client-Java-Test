package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class ControllerSupportHook extends GlacierMod {

    private final BooleanSetting enabled = new BooleanSetting("Enabled", "Enable controller support", false);
    private final NumberSetting deadzone = new NumberSetting("Deadzone", "Analog stick deadzone", 0.05, 0.5, 0.15);
    private final NumberSetting sensitivity = new NumberSetting("Sensitivity", "Controller sensitivity", 0.1, 3.0, 1.5);
    private final BooleanSetting vibration = new BooleanSetting("Vibration", "Enable controller vibration", true);

    public ControllerSupportHook() {
        super("Controller Support", "Enable gamepad/controller support", Category.QOL);
        addSettings(enabled, deadzone, sensitivity, vibration);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        if (!enabled.getValue()) return;
        // Controller polling via LWJGL GLFW joystick API
    }

    public double applyDeadzone(double value) {
        double dz = deadzone.getValue();
        if (Math.abs(value) < dz) return 0.0;
        return (value - Math.signum(value) * dz) / (1.0 - dz);
    }

    public boolean isEnabled() { return enabled.getValue(); }
    public float getSensitivity() { return (float)(double) sensitivity.getValue(); }
    public boolean isVibration() { return vibration.getValue(); }
}
