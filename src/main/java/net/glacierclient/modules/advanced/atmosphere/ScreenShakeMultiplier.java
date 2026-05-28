package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class ScreenShakeMultiplier extends GlacierMod {

    private final NumberSetting multiplier = new NumberSetting("Multiplier", "Screen shake multiplier", 0.0, 2.0, 0.5);
    private final BooleanSetting disableExplosionShake = new BooleanSetting("Disable Explosion Shake", "Disable explosion shake", false);
    private final BooleanSetting disableDamageShake = new BooleanSetting("Disable Damage Shake", "Disable damage shake", false);

    public ScreenShakeMultiplier() {
        super("Screen Shake", "Control screen shake intensity from explosions", Category.RENDER);
        addSettings(multiplier, disableExplosionShake, disableDamageShake);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
    @Override public void onTick() {}

    public float applyShakeMultiplier(float original, boolean isExplosion) {
        if (isExplosion && disableExplosionShake.getValue()) return 0;
        if (!isExplosion && disableDamageShake.getValue()) return 0;
        return original * (float)(double) multiplier.getValue();
    }
}
