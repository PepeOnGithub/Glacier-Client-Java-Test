package net.glacierclient.modules.expanded.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;

public class HitCooldownRing extends GlacierMod {

    private final ModeSetting style = new ModeSetting("Style", "Display style for attack cooldown", "Ring", "Ring", "Arc", "Bar");
    private final ColorSetting fillColor = new ColorSetting("Fill Color", "Color when cooldown is ready", GlacierTheme.ACCENT);
    private final ColorSetting emptyColor = new ColorSetting("Empty Color", "Color when on cooldown", 0x337289DA);
    private final NumberSetting size = new NumberSetting("Size", "Size of the cooldown ring", 32, 20, 80);
    private final BooleanSetting showOnlyDuring = new BooleanSetting("Show Only During", "Only show while holding weapon", false);

    public HitCooldownRing() {
        super("Hit Cooldown Ring", "Animated ring filling for weapon attack cooldown", Category.PVP);
        addSettings(style, fillColor, emptyColor, size, showOnlyDuring);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public String getStyle() { return style.getValue(); }
    public int getFillColor() { return fillColor.getValue(); }
    public int getEmptyColor() { return emptyColor.getValue(); }
    public int getSize() { return (int)(double) size.getValue(); }
    public boolean isShowOnlyDuring() { return showOnlyDuring.getValue(); }
}
