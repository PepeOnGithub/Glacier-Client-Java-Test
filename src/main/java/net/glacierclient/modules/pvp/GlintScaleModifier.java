package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class GlintScaleModifier extends GlacierMod {

    private final NumberSetting scale = new NumberSetting("Scale", "Enchantment glint scale", 0.1, 3.0, 1.0);
    private final BooleanSetting animated = new BooleanSetting("Animated", "Animate the glint scale", false);
    private final NumberSetting speed = new NumberSetting("Speed", "Animation speed", 0.1, 5.0, 1.0);

    private float animOffset = 0f;

    public GlintScaleModifier() {
        super("Glint Scale", "Modify the enchantment glint scale on items", Category.PVP);
        addSettings(scale, animated, speed);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        if (animated.getValue()) {
            animOffset = (animOffset + 0.05f * (float)(double) speed.getValue()) % ((float) Math.PI * 2f);
        }
    }

    public float getEffectiveScale() {
        float base = (float)(double) scale.getValue();
        if (animated.getValue()) base += (float) Math.sin(animOffset) * 0.2f;
        return base;
    }
}
