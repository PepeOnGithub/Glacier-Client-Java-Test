package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class TileEntityCulling extends GlacierMod {

    private final BooleanSetting enabled = new BooleanSetting("Enabled", "Enable tile entity culling", true);
    private final NumberSetting maxTileEntities = new NumberSetting("Max Tile Entities", "Max rendered tile entities", 1, 2000, 500);
    private final BooleanSetting skipChests = new BooleanSetting("Skip Chests", "Skip chest tile entities", false);

    private int renderedCount = 0;

    public TileEntityCulling() {
        super("Tile Entity Culling", "Limit tile entity rendering for performance", Category.PERFORMANCE);
        addSettings(enabled, maxTileEntities, skipChests);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() { renderedCount = 0; }

    public boolean shouldRender(boolean isChest) {
        if (!enabled.getValue()) return true;
        if (isChest && skipChests.getValue()) return false;
        if (renderedCount >= (int)(double) maxTileEntities.getValue()) return false;
        renderedCount++;
        return true;
    }
}
