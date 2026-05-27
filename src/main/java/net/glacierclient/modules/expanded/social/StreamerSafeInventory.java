package net.glacierclient.modules.expanded.social;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;

public class StreamerSafeInventory extends GlacierMod {

    private final BooleanSetting hideNames = new BooleanSetting("Hide Names", "Mask item display names", false);
    private final BooleanSetting hideLore = new BooleanSetting("Hide Lore", "Mask item lore tooltip lines", false);
    private final BooleanSetting hideEnchants = new BooleanSetting("Hide Enchants", "Mask enchantment names on items", false);
    private final BooleanSetting requireOBSActive = new BooleanSetting("Require OBS Active", "Only activate when OBS is detected running", false);

    public StreamerSafeInventory() {
        super("Streamer Safe Inventory", "Hide item names/lore during streaming", Category.QOL);
        addSettings(hideNames, hideLore, hideEnchants, requireOBSActive);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public boolean isHideNames() { return hideNames.getValue(); }
    public boolean isHideLore() { return hideLore.getValue(); }
    public boolean isHideEnchants() { return hideEnchants.getValue(); }
    public boolean isRequireOBSActive() { return requireOBSActive.getValue(); }
}
