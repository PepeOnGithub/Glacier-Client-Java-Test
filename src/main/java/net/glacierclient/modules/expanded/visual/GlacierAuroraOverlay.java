package net.glacierclient.modules.expanded.visual;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;

public class GlacierAuroraOverlay extends GlacierMod {

    private final NumberSetting bands = new NumberSetting("Bands", "Number of aurora bands in the skybox", 4, 1, 8);
    private final ColorSetting primaryColor = new ColorSetting("Primary Color", "Primary aurora band color", 0xFF7289DA);
    private final ColorSetting secondaryColor = new ColorSetting("Secondary Color", "Secondary aurora band color", 0xFF43B581);
    private final NumberSetting speed = new NumberSetting("Speed", "Animation speed of aurora movement", 0.5, 0.1, 3.0);
    private final NumberSetting intensity = new NumberSetting("Intensity", "Brightness of the aurora overlay", 1.0, 0.1, 2.0);

    public GlacierAuroraOverlay() {
        super("Aurora Overlay", "Additional aurora skybox patterns synced to time", Category.RENDER);
        addSettings(bands, primaryColor, secondaryColor, speed, intensity);
    }

    @Override
    public void onEnable() {
        // Register skybox renderer for aurora bands
    }

    @Override
    public void onDisable() {
        // Remove aurora skybox renderer
    }

    public int getBands() { return (int)(double) bands.getValue(); }
    public int getPrimaryColor() { return primaryColor.getValue(); }
    public int getSecondaryColor() { return secondaryColor.getValue(); }
    public double getSpeed() { return speed.getValue(); }
    public double getIntensity() { return intensity.getValue(); }
}
