package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class GeometryBufferOptimizer extends GlacierMod {

    private final BooleanSetting mergeBuffers = new BooleanSetting("Merge Buffers", "Merge geometry buffers", true);
    private final NumberSetting bufferSize = new NumberSetting("Buffer Size", "Geometry buffer size (KB)", 256, 8192, 2048);
    private final BooleanSetting reuseBuffers = new BooleanSetting("Reuse Buffers", "Reuse allocated geometry buffers", true);

    private net.minecraft.client.option.GraphicsMode savedMode = net.minecraft.client.option.GraphicsMode.FANCY;

    public GeometryBufferOptimizer() {
        super("Geometry Buffer Optimizer", "Switches to Fast graphics to lighten the geometry pipeline", Category.PERFORMANCE);
        addSettings(mergeBuffers, bufferSize, reuseBuffers);
    }

    @Override
    public void onEnable() {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.options != null) {
            savedMode = mc.options.getGraphicsMode().getValue();
            if (mergeBuffers.getValue()) mc.options.getGraphicsMode().setValue(net.minecraft.client.option.GraphicsMode.FAST);
        }
    }

    @Override
    public void onDisable() {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.options != null) mc.options.getGraphicsMode().setValue(savedMode);
    }

    @Override
    public void onTick() {}

    public boolean isMergeBuffers() { return mergeBuffers.getValue(); }
    public int getBufferSize() { return (int)(double) bufferSize.getValue(); }
    public boolean isReuseBuffers() { return reuseBuffers.getValue(); }
}
