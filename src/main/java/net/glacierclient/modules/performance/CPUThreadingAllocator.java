package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class CPUThreadingAllocator extends GlacierMod {

    private final NumberSetting chunkThreads = new NumberSetting("Chunk Threads", "Threads for chunk generation", 1, 16, 4);
    private final NumberSetting ioThreads = new NumberSetting("IO Threads", "Threads for IO operations", 1, 8, 2);
    private final BooleanSetting prioritizeMain = new BooleanSetting("Prioritize Main", "Prioritize main thread", true);

    public CPUThreadingAllocator() {
        super("CPU Threading Allocator", "Configure CPU thread allocation for better performance", Category.PERFORMANCE);
        addSettings(chunkThreads, ioThreads, prioritizeMain);
    }

    @Override
    public void onEnable() {
        if (prioritizeMain.getValue()) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        }
    }

    @Override
    public void onDisable() {
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
    }

    @Override
    public void onTick() {}

    public int getChunkThreads() { return (int) chunkThreads.getValue(); }
    public int getIOThreads() { return (int) ioThreads.getValue(); }
}
