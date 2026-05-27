package net.glacierclient.modules.expanded.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class ShulkerBoxPreview extends GlacierMod {

    private final BooleanSetting showTooltip = new BooleanSetting("Show Tooltip", "Show shulker contents on hover", false);
    private final BooleanSetting showCount = new BooleanSetting("Show Count", "Display item counts in preview", false);
    private final NumberSetting maxItems = new NumberSetting("Max Items", "Maximum number of items to preview", 27, 10, 27);
    private final BooleanSetting colorByType = new BooleanSetting("Color By Type", "Color item slots by item type", false);

    public ShulkerBoxPreview() {
        super("Shulker Preview", "Hover tooltip showing shulker contents", Category.QOL);
        addSettings(showTooltip, showCount, maxItems, colorByType);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public boolean isShowTooltip() { return showTooltip.getValue(); }
    public boolean isShowCount() { return showCount.getValue(); }
    public int getMaxItems() { return (int) maxItems.getValue(); }
    public boolean isColorByType() { return colorByType.getValue(); }
}
