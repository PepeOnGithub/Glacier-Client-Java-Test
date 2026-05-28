package net.glacierclient.modules.expanded.visual;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class AnimatedItemTextures extends GlacierMod {

    private final BooleanSetting affectEnchanted = new BooleanSetting("Affect Enchanted", "Apply emissive glow to enchanted items", true);
    private final BooleanSetting affectCosmetics = new BooleanSetting("Affect Cosmetics", "Apply emissive glow to cosmetic items", false);
    private final NumberSetting glowIntensity = new NumberSetting("Glow Intensity", "Intensity of the emissive glow", 1.0, 0.0, 2.0);
    private final NumberSetting animSpeed = new NumberSetting("Anim Speed", "Speed of glow pulse animation", 1.0, 0.1, 5.0);

    public AnimatedItemTextures() {
        super("Animated Items", "Emissive glow masks for enchanted items", Category.RENDER);
        addSettings(affectEnchanted, affectCosmetics, glowIntensity, animSpeed);
    }

    @Override
    public void onEnable() {
        // Register item render hook for emissive layer
    }

    @Override
    public void onDisable() {
        // Remove emissive layer hook
    }

    public boolean isAffectEnchanted() { return affectEnchanted.getValue(); }
    public boolean isAffectCosmetics() { return affectCosmetics.getValue(); }
    public double getGlowIntensity() { return glowIntensity.getValue(); }
    public double getAnimSpeed() { return animSpeed.getValue(); }
}
