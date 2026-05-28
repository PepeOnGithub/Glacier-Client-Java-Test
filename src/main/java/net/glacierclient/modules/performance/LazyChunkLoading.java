package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class LazyChunkLoading extends GlacierMod {

    private final BooleanSetting prioritizeVisible = new BooleanSetting("Prioritize Visible", "Load visible chunks first", true);
    private final NumberSetting loadDelay = new NumberSetting("Load Delay", "Delay between chunk loads (ms)", 0, 500, 50);
    private final NumberSetting maxPerTick = new NumberSetting("Max Per Tick", "Max chunks loaded per tick", 1, 32, 4);

    public LazyChunkLoading() {
        super("Lazy Chunk Loading", "Reduce chunk load rate to improve FPS", Category.PERFORMANCE);
        addSettings(prioritizeVisible, loadDelay, maxPerTick);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isPrioritizeVisible() { return prioritizeVisible.getValue(); }
    public int getLoadDelay() { return (int) loadDelay.getValue(); }
    public int getMaxPerTick() { return (int) maxPerTick.getValue(); }
}
