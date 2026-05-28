package net.glacierclient.modules.expanded.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;

public class RecipeBookQuickCrafter extends GlacierMod {

    private final BooleanSetting autoShiftClick = new BooleanSetting("Auto Shift-Click", "Automatically shift-click craftable recipes", false);
    private final BooleanSetting showAvailable = new BooleanSetting("Show Available", "Highlight recipes with all materials present", false);
    private final BooleanSetting notification = new BooleanSetting("Notification", "Show notification when recipe becomes craftable", false);

    public RecipeBookQuickCrafter() {
        super("Quick Crafter", "Shift-click recipe when materials available", Category.QOL);
        addSettings(autoShiftClick, showAvailable, notification);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public boolean isAutoShiftClick() { return autoShiftClick.getValue(); }
    public boolean isShowAvailable() { return showAvailable.getValue(); }
    public boolean isNotification() { return notification.getValue(); }
}
