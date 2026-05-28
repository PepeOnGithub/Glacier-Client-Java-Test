package net.glacierclient.modules.expanded.social;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class AntiStreamSnipingBlocker extends GlacierMod {

    private final NumberSetting joinDelay = new NumberSetting("Join Delay", "Milliseconds to delay join notifications", 3000, 0, 10000);
    private final BooleanSetting shuffleFriendNotifs = new BooleanSetting("Shuffle Friend Notifs", "Randomize timing of friend join notifications", false);
    private final BooleanSetting showIndicator = new BooleanSetting("Show Indicator", "Show indicator when anti-snipe is active", false);

    public AntiStreamSnipingBlocker() {
        super("Anti Snipe", "Delay join messages and shuffle friend notifs", Category.QOL);
        addSettings(joinDelay, shuffleFriendNotifs, showIndicator);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public int getJoinDelay() { return (int) joinDelay.getValue(); }
    public boolean isShuffleFriendNotifs() { return shuffleFriendNotifs.getValue(); }
    public boolean isShowIndicator() { return showIndicator.getValue(); }
}
