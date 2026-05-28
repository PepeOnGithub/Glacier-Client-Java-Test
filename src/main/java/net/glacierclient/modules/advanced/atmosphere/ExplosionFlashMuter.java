package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class ExplosionFlashMuter extends GlacierMod {

    private final NumberSetting flashIntensity = new NumberSetting("Flash Intensity", "Explosion flash opacity (0=none)", 0.0, 1.0, 0.0);
    private final BooleanSetting removeCompletely = new BooleanSetting("Remove Completely", "Completely remove flash", true);

    public ExplosionFlashMuter() {
        super("Explosion Flash", "Reduce or remove explosion screen flash", Category.RENDER);
        addSettings(flashIntensity, removeCompletely);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
    @Override public void onTick() {}

    public float getEffectiveFlashIntensity(float original) {
        if (removeCompletely.getValue()) return 0;
        return original * (float)(double) flashIntensity.getValue();
    }

    public boolean shouldBlockFlash() { return removeCompletely.getValue(); }
}
