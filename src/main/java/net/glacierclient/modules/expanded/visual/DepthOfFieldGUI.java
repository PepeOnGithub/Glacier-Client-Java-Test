package net.glacierclient.modules.expanded.visual;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class DepthOfFieldGUI extends GlacierMod {

    private final NumberSetting blurRadius = new NumberSetting("Blur Radius", "Radius of background blur effect", 14, 1, 32);
    private final NumberSetting blurStrength = new NumberSetting("Blur Strength", "Strength multiplier for blur shader", 1.0, 0.1, 3.0);
    private final BooleanSetting animate = new BooleanSetting("Animate", "Smoothly animate blur on/off", false);
    private final NumberSetting animSpeed = new NumberSetting("Anim Speed", "Speed of blur animation transition", 2.0, 0.1, 5.0);

    public DepthOfFieldGUI() {
        super("Depth of Field GUI", "Background blur when menus open", Category.RENDER);
        addSettings(blurRadius, blurStrength, animate, animSpeed);
    }

    @Override
    public void onEnable() {
        // Register screen open/close event hooks for blur
    }

    @Override
    public void onDisable() {
        // Remove blur shader
    }

    public int getBlurRadius() { return (int) blurRadius.getValue(); }
    public double getBlurStrength() { return blurStrength.getValue(); }
    public boolean isAnimate() { return animate.getValue(); }
    public double getAnimSpeed() { return animSpeed.getValue(); }
}
