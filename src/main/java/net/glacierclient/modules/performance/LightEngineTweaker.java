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

    private boolean savedAo = true;

    @Override
    public void onEnable() {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.options != null) { savedAo = mc.options.getAo().getValue(); apply(mc); }
    }

    @Override
    public void onDisable() {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.options != null) mc.options.getAo().setValue(savedAo);
    }

    @Override
    public void onTick() {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.options != null) apply(mc);
    }

    /** Fast propagation disables smooth lighting (ambient occlusion) — a real, measurable FPS win. */
    private void apply(net.minecraft.client.MinecraftClient mc) {
        boolean wantAo = !fastLightPropagation.getValue();
        if (mc.options.getAo().getValue() != wantAo) mc.options.getAo().setValue(wantAo);
    }

    public int getLightUpdatesPerTick() { return (int)(double) lightUpdatesPerTick.getValue(); }
    public boolean isFastPropagation() { return fastLightPropagation.getValue(); }
    public boolean isSkipSkylight() { return skipSkylight.getValue(); }
}
