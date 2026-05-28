package net.glacierclient.modules.expanded.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;

public class PotionEffectTicker extends GlacierMod {

    private final ModeSetting style = new ModeSetting("Style", "Display style for effect tickers", "Bars", "Bars", "Icons", "Both");
    private final NumberSetting barWidth = new NumberSetting("Bar Width", "Width of each effect duration bar", 8, 4, 20);
    private final NumberSetting barHeight = new NumberSetting("Bar Height", "Height of each effect duration bar", 60, 40, 120);
    private final BooleanSetting showText = new BooleanSetting("Show Text", "Display effect name and time remaining", false);

    public PotionEffectTicker() {
        super("Effect Ticker", "Vertical bars for active effects duration", Category.PVP);
        addSettings(style, barWidth, barHeight, showText);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public String getStyle() { return style.getValue(); }
    public int getBarWidth() { return (int)(double) barWidth.getValue(); }
    public int getBarHeight() { return (int)(double) barHeight.getValue(); }
    public boolean isShowText() { return showText.getValue(); }
}
