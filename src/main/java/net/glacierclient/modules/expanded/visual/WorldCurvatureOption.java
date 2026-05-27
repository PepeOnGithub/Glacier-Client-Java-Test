package net.glacierclient.modules.expanded.visual;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class WorldCurvatureOption extends GlacierMod {

    private final NumberSetting intensity = new NumberSetting("Intensity", "Strength of spherical world curvature", 0.15, 0.0, 1.0);
    private final BooleanSetting animate = new BooleanSetting("Animate", "Animate curvature intensity", false);
    private final NumberSetting animSpeed = new NumberSetting("Anim Speed", "Speed of curvature animation", 0.2, 0.1, 1.0);

    public WorldCurvatureOption() {
        super("World Curvature", "Slight spherical world curvature render effect", Category.RENDER);
        addSettings(intensity, animate, animSpeed);
    }

    @Override
    public void onEnable() {
        // Inject vertex shader world curvature transform
    }

    @Override
    public void onDisable() {
        // Remove curvature vertex transform
    }

    public double getIntensity() { return intensity.getValue(); }
    public boolean isAnimate() { return animate.getValue(); }
    public double getAnimSpeed() { return animSpeed.getValue(); }
}
