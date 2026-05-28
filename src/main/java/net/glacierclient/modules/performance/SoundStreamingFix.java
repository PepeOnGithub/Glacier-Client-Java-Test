package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class SoundStreamingFix extends GlacierMod {

    private final BooleanSetting fixStreamingBugs = new BooleanSetting("Fix Streaming Bugs", "Fix sound streaming bugs", true);
    private final NumberSetting bufferSize = new NumberSetting("Buffer Size", "Sound streaming buffer size (MB)", 1, 8, 2);
    private final BooleanSetting reduceLatency = new BooleanSetting("Reduce Latency", "Reduce sound streaming latency", true);

    public SoundStreamingFix() {
        super("Sound Streaming Fix", "Fix sound streaming and reduce audio latency", Category.PERFORMANCE);
        addSettings(fixStreamingBugs, bufferSize, reduceLatency);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isFixStreamingBugs() { return fixStreamingBugs.getValue(); }
    public int getBufferSize() { return (int)(double) bufferSize.getValue(); }
    public boolean isReduceLatency() { return reduceLatency.getValue(); }
}
