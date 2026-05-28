package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;

public class GlacierSkybox extends GlacierMod {

    private final BooleanSetting animate = new BooleanSetting("Animate", "Animate skybox aurora", true);
    private final NumberSetting speed = new NumberSetting("Speed", "Animation speed", 0.1, 5.0, 1.0);
    private final ColorSetting primaryColor = new ColorSetting("Primary Color", "Primary aurora color", 0xFF7289DA);
    private final ColorSetting secondaryColor = new ColorSetting("Secondary Color", "Secondary aurora color", 0xFF43B581);
    private final NumberSetting intensity = new NumberSetting("Intensity", "Skybox intensity", 0.1, 2.0, 1.0);

    private float animOffset = 0;

    public GlacierSkybox() {
        super("Glacier Skybox", "Procedural animated aurora skybox", Category.RENDER);
        addSettings(animate, speed, primaryColor, secondaryColor, intensity);
    }

    @Override
    public void onEnable() {
        animOffset = 0;
    }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        if (animate.getValue()) {
            animOffset += (float) (speed.getValue() * 0.01);
            if (animOffset > Math.PI * 2) animOffset -= (float) (Math.PI * 2);
        }
    }

    public float getAnimOffset() { return animOffset; }
    public int getPrimaryColor() { return primaryColor.getValue(); }
    public int getSecondaryColor() { return secondaryColor.getValue(); }
    public float getIntensity() { return (float)(double) intensity.getValue(); }
}
