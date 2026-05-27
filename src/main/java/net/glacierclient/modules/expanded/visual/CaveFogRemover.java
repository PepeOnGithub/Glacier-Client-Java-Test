package net.glacierclient.modules.expanded.visual;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class CaveFogRemover extends GlacierMod {

    private final BooleanSetting removeFog = new BooleanSetting("Remove Fog", "Remove blindness/cave fog effect", true);
    private final NumberSetting fadeStart = new NumberSetting("Fade Start", "Distance (blocks) where fog begins fading", 24, 4, 64);
    private final NumberSetting fadeEnd = new NumberSetting("Fade End", "Distance (blocks) where fog fully dissipates", 80, 16, 256);
    private final BooleanSetting keepAtmosphere = new BooleanSetting("Keep Atmosphere", "Retain atmospheric sky fog in the open", false);

    public CaveFogRemover() {
        super("Cave Fog Remover", "Remove blindness fog with smooth distance fade", Category.RENDER);
        addSettings(removeFog, fadeStart, fadeEnd, keepAtmosphere);
    }

    @Override
    public void onEnable() {
        // Hook FogCallback to override density
    }

    @Override
    public void onDisable() {
        // Restore default fog density
    }

    public boolean isRemoveFog() { return removeFog.getValue(); }
    public int getFadeStart() { return (int) fadeStart.getValue(); }
    public int getFadeEnd() { return (int) fadeEnd.getValue(); }
    public boolean isKeepAtmosphere() { return keepAtmosphere.getValue(); }
}
