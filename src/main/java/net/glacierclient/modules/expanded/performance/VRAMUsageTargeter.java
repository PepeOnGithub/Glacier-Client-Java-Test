package net.glacierclient.modules.expanded.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class VRAMUsageTargeter extends GlacierMod {

    private final NumberSetting targetMB = new NumberSetting("Target MB", "Target VRAM budget in megabytes", 2048, 256, 8192);
    private final BooleanSetting autoAdjustMipmap = new BooleanSetting("Auto Mipmap", "Automatically adjust mipmap levels", false);
    private final BooleanSetting autoAdjustDistance = new BooleanSetting("Auto Distance", "Automatically adjust render distance", false);
    private final BooleanSetting autoAdjustAtlas = new BooleanSetting("Auto Atlas", "Automatically adjust texture atlas resolution", false);
    private final BooleanSetting showUsage = new BooleanSetting("Show Usage", "Display current VRAM usage on HUD", false);

    public VRAMUsageTargeter() {
        super("VRAM Targeter", "Dynamic mipmap/texture/distance VRAM management", Category.PERFORMANCE);
        addSettings(targetMB, autoAdjustMipmap, autoAdjustDistance, autoAdjustAtlas, showUsage);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public int getTargetMB() { return (int) targetMB.getValue(); }
    public boolean isAutoAdjustMipmap() { return autoAdjustMipmap.getValue(); }
    public boolean isAutoAdjustDistance() { return autoAdjustDistance.getValue(); }
    public boolean isAutoAdjustAtlas() { return autoAdjustAtlas.getValue(); }
    public boolean isShowUsage() { return showUsage.getValue(); }
}
