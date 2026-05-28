package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class RAMCapLimiter extends GlacierMod {

    private final NumberSetting maxRAM = new NumberSetting("Max RAM", "Max RAM before GC (MB)", 512, 16384, 4096);
    private final BooleanSetting triggerGCAtCap = new BooleanSetting("Trigger GC At Cap", "Run GC when limit reached", true);
    private final NumberSetting gcThreshold = new NumberSetting("GC Threshold", "Memory usage % to trigger GC", 70, 95, 85);

    public RAMCapLimiter() {
        super("RAM Cap Limiter", "Monitor and limit RAM usage", Category.PERFORMANCE);
        addSettings(maxRAM, triggerGCAtCap, gcThreshold);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        if (!triggerGCAtCap.getValue()) return;
        Runtime rt = Runtime.getRuntime();
        long usedMB = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        double pct = (double) usedMB / (rt.maxMemory() / (1024 * 1024)) * 100.0;
        if (usedMB > (int) maxRAM.getValue() || pct > (int) gcThreshold.getValue()) {
            System.gc();
        }
    }
}
