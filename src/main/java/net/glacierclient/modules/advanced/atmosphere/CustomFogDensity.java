package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;

public class CustomFogDensity extends GlacierMod {

    private final NumberSetting density = new NumberSetting("Density", "Fog density", 0.0, 1.0, 0.0);
    private final NumberSetting start = new NumberSetting("Start", "Fog start distance", 0, 256, 64);
    private final NumberSetting end = new NumberSetting("End", "Fog end distance", 64, 1024, 256);
    private final ColorSetting fogColor = new ColorSetting("Fog Color", "Fog color", 0xFF23272A);
    private final BooleanSetting removeCaveFog = new BooleanSetting("Remove Cave Fog", "Remove blindness/darkness fog underground", false);
    private final BooleanSetting keepAtmosphere = new BooleanSetting("Keep Atmosphere", "Retain atmospheric sky fog in the open", false);

    public CustomFogDensity() {
        super("Fog Control", "Control fog density, distance, color and cave fog", Category.RENDER);
        addSettings(density, start, end, fogColor, removeCaveFog, keepAtmosphere);
    }

    public float getDensity() { return (float)(double) density.getValue(); }
    public float getFogStart() { return (float)(double) start.getValue(); }
    public float getFogEnd() { return (float)(double) end.getValue(); }
    public int getFogColor() { return fogColor.getValue(); }
    public boolean isRemoveCaveFog() { return removeCaveFog.getValue(); }
    public boolean isKeepAtmosphere() { return keepAtmosphere.getValue(); }
}
