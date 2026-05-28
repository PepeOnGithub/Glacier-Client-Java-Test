package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class GeometryBufferOptimizer extends GlacierMod {

    private final BooleanSetting mergeBuffers = new BooleanSetting("Merge Buffers", "Merge geometry buffers", true);
    private final NumberSetting bufferSize = new NumberSetting("Buffer Size", "Geometry buffer size (KB)", 256, 8192, 2048);
    private final BooleanSetting reuseBuffers = new BooleanSetting("Reuse Buffers", "Reuse allocated geometry buffers", true);

    public GeometryBufferOptimizer() {
        super("Geometry Buffer Optimizer", "Optimize geometry buffers for rendering", Category.PERFORMANCE);
        addSettings(mergeBuffers, bufferSize, reuseBuffers);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isMergeBuffers() { return mergeBuffers.getValue(); }
    public int getBufferSize() { return (int) bufferSize.getValue(); }
    public boolean isReuseBuffers() { return reuseBuffers.getValue(); }
}
