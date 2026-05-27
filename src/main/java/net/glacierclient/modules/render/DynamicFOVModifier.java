package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class DynamicFOVModifier extends GlacierMod {

    private final NumberSetting multiplier = new NumberSetting("Multiplier", "FOV change multiplier", 0.0, 2.0, 1.0);
    private final BooleanSetting disableSpeedFOV = new BooleanSetting("Disable Speed FOV", "Disable speed effect FOV", false);
    private final BooleanSetting disableSlownessFOV = new BooleanSetting("Disable Slowness FOV", "Disable slowness effect FOV", false);
    private final BooleanSetting disableSprintFOV = new BooleanSetting("Disable Sprint FOV", "Disable sprint FOV boost", false);

    public DynamicFOVModifier() {
        super("Dynamic FOV", "Controls FOV changes from speed/slowness effects", Category.RENDER);
        addSettings(multiplier, disableSpeedFOV, disableSlownessFOV, disableSprintFOV);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public float getMultiplier() { return (float) multiplier.getValue(); }
    public boolean isSpeedFOVDisabled() { return disableSpeedFOV.getValue(); }
    public boolean isSlownessFOVDisabled() { return disableSlownessFOV.getValue(); }
    public boolean isSprintFOVDisabled() { return disableSprintFOV.getValue(); }
}
