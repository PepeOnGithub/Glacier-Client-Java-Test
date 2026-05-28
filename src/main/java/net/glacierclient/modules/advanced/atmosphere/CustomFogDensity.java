package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;

public class CustomFogDensity extends GlacierMod {

    private final NumberSetting density = new NumberSetting("Density", "Fog density", 0.0, 1.0, 0.0);
    private final NumberSetting start = new NumberSetting("Start", "Fog start distance", 0, 256, 64);
    private final NumberSetting end = new NumberSetting("End", "Fog end distance", 64, 1024, 256);
    private final ColorSetting fogColor = new ColorSetting("Fog Color", "Fog color", 0xFF23272A);

    public CustomFogDensity() {
        super("Custom Fog", "Control fog density and distance", Category.RENDER);
        addSettings(density, start, end, fogColor);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
    @Override public void onTick() {}

    public float getDensity() { return (float)(double) density.getValue(); }
    public float getFogStart() { return (float)(double) start.getValue(); }
    public float getFogEnd() { return (float)(double) end.getValue(); }
    public int getFogColor() { return fogColor.getValue(); }
}
