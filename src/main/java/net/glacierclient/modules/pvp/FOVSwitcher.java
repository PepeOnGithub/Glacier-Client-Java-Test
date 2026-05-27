package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;

public class FOVSwitcher extends GlacierMod {

    private final BooleanSetting disableSprintFOV = new BooleanSetting("Disable Sprint FOV", "Disable sprint FOV boost", true);
    private final BooleanSetting disableEffectFOV = new BooleanSetting("Disable Effect FOV", "Disable effect FOV changes", true);
    private final BooleanSetting disableBoostFOV = new BooleanSetting("Disable Boost FOV", "Disable elytra/speed boost FOV", true);
    private final BooleanSetting disableBowFOV = new BooleanSetting("Disable Bow FOV", "Disable bow zoom FOV", false);

    public FOVSwitcher() {
        super("FOV Switcher", "Disable FOV changes from effects and sprinting", Category.PVP);
        addSettings(disableSprintFOV, disableEffectFOV, disableBoostFOV, disableBowFOV);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isSprintFOVDisabled() { return disableSprintFOV.getValue(); }
    public boolean isEffectFOVDisabled() { return disableEffectFOV.getValue(); }
    public boolean isBoostFOVDisabled() { return disableBoostFOV.getValue(); }
    public boolean isBowFOVDisabled() { return disableBowFOV.getValue(); }
}
