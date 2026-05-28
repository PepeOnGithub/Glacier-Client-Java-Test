package net.glacierclient.modules.expanded.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;

public class PearlTrailPrediction extends GlacierMod {

    private static final int GREEN = 0xFF55FF55;

    private final ColorSetting arcColor = new ColorSetting("Arc Color", "Color of the predicted pearl arc", GlacierTheme.ACCENT);
    private final NumberSetting arcSegments = new NumberSetting("Arc Segments", "Number of segments for arc resolution", 40, 10, 100);
    private final BooleanSetting showLanding = new BooleanSetting("Show Landing", "Highlight predicted landing position", false);
    private final ColorSetting landingColor = new ColorSetting("Landing Color", "Color of landing point marker", GREEN);

    public PearlTrailPrediction() {
        super("Pearl Prediction", "Dotted arc prediction for thrown ender pearls", Category.PVP);
        addSettings(arcColor, arcSegments, showLanding, landingColor);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public int getArcColor() { return arcColor.getValue(); }
    public int getArcSegments() { return (int) arcSegments.getValue(); }
    public boolean isShowLanding() { return showLanding.getValue(); }
    public int getLandingColor() { return landingColor.getValue(); }
}
