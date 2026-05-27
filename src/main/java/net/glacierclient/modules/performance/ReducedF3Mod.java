package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;

public class ReducedF3Mod extends GlacierMod {

    private final BooleanSetting hideEntityCount = new BooleanSetting("Hide Entity Count", "Hide entity count in F3", false);
    private final BooleanSetting hideChunkInfo = new BooleanSetting("Hide Chunk Info", "Hide chunk info in F3", false);
    private final BooleanSetting hideGPUInfo = new BooleanSetting("Hide GPU Info", "Hide GPU info in F3", false);
    private final BooleanSetting compactMode = new BooleanSetting("Compact Mode", "Show compact F3 menu", false);
    private final BooleanSetting hideCoords = new BooleanSetting("Hide Coords", "Hide coordinates in F3", false);

    public ReducedF3Mod() {
        super("Reduced F3", "Clean up and simplify the F3 debug screen", Category.PERFORMANCE);
        addSettings(hideEntityCount, hideChunkInfo, hideGPUInfo, compactMode, hideCoords);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isHideEntityCount() { return hideEntityCount.getValue(); }
    public boolean isHideChunkInfo() { return hideChunkInfo.getValue(); }
    public boolean isHideGPUInfo() { return hideGPUInfo.getValue(); }
    public boolean isCompactMode() { return compactMode.getValue(); }
    public boolean isHideCoords() { return hideCoords.getValue(); }
}
