package net.glacierclient.modules.expanded.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;

public class MassItemMover extends GlacierMod {

    private final BooleanSetting requireShift = new BooleanSetting("Require Shift", "Require shift key to activate mass move", true);
    private final BooleanSetting includeOffhand = new BooleanSetting("Include Offhand", "Also move offhand item stacks", false);
    private final BooleanSetting showNotification = new BooleanSetting("Show Notification", "Notify when items are moved", false);

    public MassItemMover() {
        super("Mass Item Mover", "Shift+scroll to move all identical items", Category.QOL);
        addSettings(requireShift, includeOffhand, showNotification);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public boolean isRequireShift() { return requireShift.getValue(); }
    public boolean isIncludeOffhand() { return includeOffhand.getValue(); }
    public boolean isShowNotification() { return showNotification.getValue(); }
}
