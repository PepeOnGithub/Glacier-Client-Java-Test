package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class MemoryManager extends GlacierMod {

    private final BooleanSetting autoGC = new BooleanSetting("Auto GC", "Run garbage collection automatically", true);
    private final NumberSetting gcInterval = new NumberSetting("GC Interval", "Seconds between periodic GC hints (0 = off)", 0, 300, 60);
    private final NumberSetting gcThreshold = new NumberSetting("GC Threshold", "Heap usage % that forces a GC", 50, 95, 85);
    private final NumberSetting maxRAM = new NumberSetting("Max RAM", "Used heap (MB) that forces a GC", 512, 16384, 4096);
    private final BooleanSetting fixTextureMemory = new BooleanSetting("Fix Texture Memory", "Release leaked texture buffers", true);
    private final BooleanSetting fixSoundBuffer = new BooleanSetting("Fix Sound Buffer", "Release leaked sound buffers", true);
    private final BooleanSetting unloadTextures = new BooleanSetting("Unload Textures", "Unload unused textures to free VRAM", true);
    private final BooleanSetting unloadOnDimension = new BooleanSetting("Unload On Dimension", "Unload textures on dimension change", true);
    private final NumberSetting maxTextureCache = new NumberSetting("Max Texture Cache", "Max cached textures", 64, 2048, 256);

    private long lastGC = 0;

    public MemoryManager() {
        super("Memory Manager", "Unified GC scheduling, RAM cap and texture/VRAM cleanup", Category.PERFORMANCE);
        addSettings(autoGC, gcInterval, gcThreshold, maxRAM,
                fixTextureMemory, fixSoundBuffer, unloadTextures, unloadOnDimension, maxTextureCache);
    }

    @Override
    public void onEnable() {
        lastGC = System.currentTimeMillis();
    }

    @Override
    public void onTick() {
        if (!autoGC.getValue()) return;
        long now = System.currentTimeMillis();
        Runtime rt = Runtime.getRuntime();
        long usedMB = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        long maxMB = rt.maxMemory() / (1024 * 1024);
        double pct = maxMB > 0 ? (double) usedMB / maxMB * 100.0 : 0.0;

        boolean intervalElapsed = gcInterval.getValue() > 0 && now - lastGC > gcInterval.getValue().longValue() * 1000L;
        boolean overThreshold = pct > gcThreshold.getValue();
        boolean overCap = usedMB > maxRAM.getValue().longValue();

        if (intervalElapsed || overThreshold || overCap) {
            lastGC = now;
            System.gc();
        }
    }

    public boolean isFixTexture() { return fixTextureMemory.getValue(); }
    public boolean isFixSound() { return fixSoundBuffer.getValue(); }
    public boolean isUnloadTextures() { return unloadTextures.getValue(); }
    public boolean isUnloadOnDimension() { return unloadOnDimension.getValue(); }
    public int getMaxTextureCache() { return maxTextureCache.getValue().intValue(); }
}
