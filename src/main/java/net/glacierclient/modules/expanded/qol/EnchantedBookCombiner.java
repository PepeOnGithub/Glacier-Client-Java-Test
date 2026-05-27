package net.glacierclient.modules.expanded.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.theme.GlacierTheme;

public class EnchantedBookCombiner extends GlacierMod {

    private final BooleanSetting highlightPath = new BooleanSetting("Highlight Path", "Highlight optimal enchantment combination order", false);
    private final BooleanSetting showCost = new BooleanSetting("Show Cost", "Display total XP cost of combination path", false);
    private final BooleanSetting showOrder = new BooleanSetting("Show Order", "Show step-by-step combination order", false);
    private final ColorSetting highlightColor = new ColorSetting("Highlight Color", "Color for path highlighting", GlacierTheme.ACCENT);

    public EnchantedBookCombiner() {
        super("Book Combiner", "Show optimal anvil combination path", Category.QOL);
        addSettings(highlightPath, showCost, showOrder, highlightColor);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public boolean isHighlightPath() { return highlightPath.getValue(); }
    public boolean isShowCost() { return showCost.getValue(); }
    public boolean isShowOrder() { return showOrder.getValue(); }
    public int getHighlightColor() { return highlightColor.getValue(); }
}
