package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class MemoryLeakFix extends GlacierMod {

    private final BooleanSetting fixTextureMemory = new BooleanSetting("Fix Texture Memory", "Fix texture memory leaks", true);
    private final BooleanSetting fixSoundBuffer = new BooleanSetting("Fix Sound Buffer", "Fix sound buffer leaks", true);
    private final BooleanSetting fixParticlePool = new BooleanSetting("Fix Particle Pool", "Fix particle pool leaks", true);
    private final NumberSetting gcInterval = new NumberSetting("GC Interval", "Seconds between GC hints", 30, 300, 60);

    private long lastGC = 0;

    public MemoryLeakFix() {
        super("Memory Leak Fix", "Fix common Minecraft memory leaks", Category.PERFORMANCE);
        addSettings(fixTextureMemory, fixSoundBuffer, fixParticlePool, gcInterval);
    }

    @Override
    public void onEnable() { lastGC = System.currentTimeMillis(); }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        long now = System.currentTimeMillis();
        if (now - lastGC > gcInterval.getValue() * 1000L) {
            lastGC = now;
            System.gc();
        }
    }

    public boolean isFixTexture() { return fixTextureMemory.getValue(); }
    public boolean isFixSound() { return fixSoundBuffer.getValue(); }
    public boolean isFixParticle() { return fixParticlePool.getValue(); }
}
