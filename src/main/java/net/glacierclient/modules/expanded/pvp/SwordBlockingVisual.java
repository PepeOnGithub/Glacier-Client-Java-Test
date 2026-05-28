package net.glacierclient.modules.expanded.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class SwordBlockingVisual extends GlacierMod {

    private final BooleanSetting showOnRightClick = new BooleanSetting("Show On Right Click", "Trigger block animation on right click", false);
    private final NumberSetting holdDelay = new NumberSetting("Hold Delay", "Milliseconds before blocking animation activates", 50, 0, 500);
    private final BooleanSetting thirdPersonOnly = new BooleanSetting("Third Person Only", "Only show animation in third person view", false);

    public SwordBlockingVisual() {
        super("Sword Block Visual", "Restore sword block animation for 1.8 PvP style", Category.PVP);
        addSettings(showOnRightClick, holdDelay, thirdPersonOnly);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public boolean isShowOnRightClick() { return showOnRightClick.getValue(); }
    public int getHoldDelay() { return (int)(double) holdDelay.getValue(); }
    public boolean isThirdPersonOnly() { return thirdPersonOnly.getValue(); }
}
