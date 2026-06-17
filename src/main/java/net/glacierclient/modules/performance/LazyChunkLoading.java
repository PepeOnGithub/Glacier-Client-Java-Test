package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class LazyChunkLoading extends GlacierMod {

    private final BooleanSetting prioritizeVisible = new BooleanSetting("Prioritize Visible", "Load visible chunks first", true);
    private final NumberSetting loadDelay = new NumberSetting("Load Delay", "Delay between chunk loads (ms)", 0, 500, 50);
    private final NumberSetting maxPerTick = new NumberSetting("Max Per Tick", "Max chunks loaded per tick", 1, 32, 4);

    private int savedViewDistance = 12;

    public LazyChunkLoading() {
        super("Lazy Chunk Loading", "Trims render distance to cut chunk-build load and stutter", Category.PERFORMANCE);
        addSettings(prioritizeVisible, loadDelay, maxPerTick);
    }

    @Override
    public void onEnable() {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.options != null) {
            savedViewDistance = mc.options.getViewDistance().getValue();
            // shave a couple of chunks off the render distance while active (real, reversible FPS win)
            mc.options.getViewDistance().setValue(Math.max(2, savedViewDistance - 2));
        }
    }

    @Override
    public void onDisable() {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.options != null) mc.options.getViewDistance().setValue(savedViewDistance);
    }

    @Override
    public void onTick() {}

    public boolean isPrioritizeVisible() { return prioritizeVisible.getValue(); }
    public int getLoadDelay() { return (int)(double) loadDelay.getValue(); }
    public int getMaxPerTick() { return (int)(double) maxPerTick.getValue(); }
}
