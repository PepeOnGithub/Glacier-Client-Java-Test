package net.glacierclient.modules.expanded.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;

public class BowDrawProgressBar extends GlacierMod {

    private static final int GREEN = 0xFF55FF55;

    private final ModeSetting style = new ModeSetting("Style", "Display style for bow charge", "Bar", "Bar", "Ring", "Number");
    private final ColorSetting emptyColor = new ColorSetting("Empty Color", "Color when bow is not drawn", 0xFF99AAB5);
    private final ColorSetting fullColor = new ColorSetting("Full Color", "Color at full draw", GREEN);
    private final NumberSetting size = new NumberSetting("Size", "Size of the indicator element", 80, 40, 200);

    public BowDrawProgressBar() {
        super("Bow Progress", "Bow charge percentage near crosshair", Category.PVP);
        addSettings(style, emptyColor, fullColor, size);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public String getStyle() { return style.getValue(); }
    public int getEmptyColor() { return emptyColor.getValue(); }
    public int getFullColor() { return fullColor.getValue(); }
    public int getSize() { return (int)(double) size.getValue(); }
}
