package net.glacierclient.modules.expanded.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class CustomChunkPregenerator extends GlacierMod {

    private final BooleanSetting enabled = new BooleanSetting("Enabled", "Enable chunk pre-generation on idle", true);
    private final NumberSetting radius = new NumberSetting("Radius", "Radius of chunks to pre-generate in chunks", 4, 2, 16);
    private final NumberSetting maxPerSecond = new NumberSetting("Max Per Second", "Maximum chunks to pre-generate per second", 4, 1, 20);
    private final BooleanSetting onlyDuringIdle = new BooleanSetting("Only During Idle", "Only pre-generate when player is stationary", false);

    public CustomChunkPregenerator() {
        super("Chunk Pregenerator", "Pre-cache chunks in spiral during idle time", Category.PERFORMANCE);
        addSettings(enabled, radius, maxPerSecond, onlyDuringIdle);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public boolean isEnabled() { return enabled.getValue(); }
    public int getRadius() { return (int) radius.getValue(); }
    public int getMaxPerSecond() { return (int) maxPerSecond.getValue(); }
    public boolean isOnlyDuringIdle() { return onlyDuringIdle.getValue(); }
}
