package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class DamageTiltFix extends GlacierMod {

    private final NumberSetting intensity = new NumberSetting("Intensity", "Tilt intensity multiplier", 0.0, 2.0, 0.0);
    private final BooleanSetting removeCompletely = new BooleanSetting("Remove Completely", "Completely disable damage tilt", true);

    public DamageTiltFix() {
        super("Damage Tilt Fix", "Control screen tilt when taking damage", Category.RENDER);
        addSettings(intensity, removeCompletely);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public float getTiltMultiplier() {
        if (removeCompletely.getValue()) return 0f;
        return (float) intensity.getValue();
    }
}
