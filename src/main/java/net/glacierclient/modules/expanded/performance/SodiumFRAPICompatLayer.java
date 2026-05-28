package net.glacierclient.modules.expanded.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class SodiumFRAPICompatLayer extends GlacierMod {

    private final BooleanSetting enabled = new BooleanSetting("Enabled", "Enable FRAPI compatibility layer", true);
    private final BooleanSetting mergeDrawCalls = new BooleanSetting("Merge Draw Calls", "Merge compatible draw calls for performance", false);
    private final BooleanSetting rebuildChunks = new BooleanSetting("Rebuild Chunks", "Force chunk rebuild on setting change", false);
    private final NumberSetting rebuildDelay = new NumberSetting("Rebuild Delay", "Milliseconds to delay chunk rebuild", 100, 0, 500);

    public SodiumFRAPICompatLayer() {
        super("FRAPI Compat", "Fabric Rendering API compat layer for Glacier performance", Category.PERFORMANCE);
        addSettings(enabled, mergeDrawCalls, rebuildChunks, rebuildDelay);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public boolean isEnabled() { return enabled.getValue(); }
    public boolean isMergeDrawCalls() { return mergeDrawCalls.getValue(); }
    public boolean isRebuildChunks() { return rebuildChunks.getValue(); }
    public int getRebuildDelay() { return (int)(double) rebuildDelay.getValue(); }
}
