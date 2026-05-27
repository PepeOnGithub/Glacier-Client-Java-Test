package net.glacierclient.modules.engine;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;

public class ProximityVoiceVisualization extends GlacierMod {

    private final ColorSetting activeColor = new ColorSetting("Active Color", "Color ring when party member is connected", 0xFF43B581);
    private final ColorSetting speakingColor = new ColorSetting("Speaking Color", "Color ring when party member is speaking", GlacierTheme.ACCENT);
    private final NumberSetting ringSize = new NumberSetting("Ring Size", "Diameter of the voice ring overlay", 40, 10, 80);
    private final BooleanSetting animate = new BooleanSetting("Animate", "Pulse the ring when speaking", false);
    private final NumberSetting fadeTime = new NumberSetting("Fade Time", "Milliseconds for ring to fade after speaking stops", 1000, 500, 5000);

    public ProximityVoiceVisualization() {
        super("Proximity Voice", "Visual audio wave rings around party members using voice", Category.ENGINE);
        addSettings(activeColor, speakingColor, ringSize, animate, fadeTime);
    }

    @Override
    public void onEnable() {
        // Register entity renderer callback for voice rings
    }

    @Override
    public void onDisable() {
        // Unregister entity renderer callback
    }

    public int getActiveColor() { return activeColor.getValue(); }
    public int getSpeakingColor() { return speakingColor.getValue(); }
    public int getRingSize() { return (int) ringSize.getValue(); }
    public boolean isAnimate() { return animate.getValue(); }
    public int getFadeTime() { return (int) fadeTime.getValue(); }
}
