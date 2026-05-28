package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class LightEngineTweaker extends GlacierMod {

    private final NumberSetting lightUpdatesPerTick = new NumberSetting("Light Updates/Tick", "Max light updates per tick", 1, 64, 8);
    private final BooleanSetting fastLightPropagation = new BooleanSetting("Fast Propagation", "Use fast light propagation", true);
    private final BooleanSetting skipSkylight = new BooleanSetting("Skip Skylight", "Skip skylight calculations underground", false);

    public LightEngineTweaker() {
        super("Light Engine Tweaker", "Tune light engine for better performance", Category.PERFORMANCE);
        addSettings(lightUpdatesPerTick, fastLightPropagation, skipSkylight);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public int getLightUpdatesPerTick() { return (int)(double) lightUpdatesPerTick.getValue(); }
    public boolean isFastPropagation() { return fastLightPropagation.getValue(); }
    public boolean isSkipSkylight() { return skipSkylight.getValue(); }
}
