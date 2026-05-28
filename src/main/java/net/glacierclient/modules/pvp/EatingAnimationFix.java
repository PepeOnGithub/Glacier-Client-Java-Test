package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class EatingAnimationFix extends GlacierMod {

    private final BooleanSetting alwaysAnimate = new BooleanSetting("Always Animate", "Always show eating animation", true);
    private final NumberSetting animationSpeed = new NumberSetting("Animation Speed", "Eating animation speed", 0.5, 3.0, 1.0);

    public EatingAnimationFix() {
        super("Eating Animation Fix", "Restore the classic eating animation", Category.PVP);
        addSettings(alwaysAnimate, animationSpeed);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isAlwaysAnimate() { return alwaysAnimate.getValue(); }
    public float getAnimationSpeed() { return (float)(double) animationSpeed.getValue(); }
}
