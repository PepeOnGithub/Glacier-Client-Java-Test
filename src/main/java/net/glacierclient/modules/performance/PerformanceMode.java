package net.glacierclient.modules.performance;

import net.glacierclient.core.module.Category;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;

/**
 * One-click "Performance Mode" preset.
 *
 * <p>Wires the real FPS levers the vanilla renderer actually reads — render/simulation distance,
 * entity distance scaling (entity &amp; tile-entity render cutoff), particle throttling, mipmap/VRAM
 * limits and the FPS cap — and pushes them to aggressive defaults tuned for weak integrated GPUs
 * (e.g. Intel UHD / Celeron N-series). Toggling the module off restores the player's previous
 * settings, so it is non-destructive.</p>
 */
public final class PerformanceMode extends GlacierMod {

    // --- Tunable levers (aggressive defaults) ---
    private final NumberSetting renderDistance   = new NumberSetting("Render Distance", "Chunk render distance cap", 2, 16, 4);
    private final NumberSetting simDistance       = new NumberSetting("Simulation Distance", "Chunk tick distance cap", 5, 12, 5);
    private final NumberSetting maxFps            = new NumberSetting("Max FPS", "FPS cap (260 = uncapped)", 30, 260, 260);
    private final NumberSetting entityDistance    = new NumberSetting("Entity Distance", "Entity/tile-entity render cutoff scale", 0.5, 1.0, 0.5);
    private final NumberSetting mipmap            = new NumberSetting("Mipmap Levels", "Lower = less VRAM", 0, 4, 0);
    private final NumberSetting biomeBlend        = new NumberSetting("Biome Blend", "Biome colour blend radius (0 = off)", 0, 7, 0);

    private final BooleanSetting minimalParticles = new BooleanSetting("Minimal Particles", "Throttle particles to minimal", true);
    private final BooleanSetting fastGraphics     = new BooleanSetting("Fast Graphics", "Use Fast graphics mode", true);
    private final BooleanSetting cloudsOff         = new BooleanSetting("Disable Clouds", "Turn clouds off", true);
    private final BooleanSetting noEntityShadows  = new BooleanSetting("No Entity Shadows", "Disable entity shadows", true);
    private final BooleanSetting noViewBob        = new BooleanSetting("No View Bob", "Disable view bobbing", true);
    private final BooleanSetting noSmoothLighting = new BooleanSetting("No Smooth Lighting", "Disable ambient occlusion", true);
    private final BooleanSetting vsyncOff          = new BooleanSetting("Disable VSync", "Uncap FPS by disabling VSync", true);

    // --- Saved originals for restore on disable ---
    private boolean captured = false;
    private int oRender, oSim, oFps, oMip, oBlend;
    private double oEntity;
    private ParticlesMode oParticles;
    private GraphicsMode oGraphics;
    private CloudRenderMode oClouds;
    private boolean oShadows, oBob, oAo, oVsync;

    public PerformanceMode() {
        super("Performance Mode", "One-click FPS boost preset for weak hardware", Category.PERFORMANCE);
        addSettings(renderDistance, simDistance, maxFps, entityDistance, mipmap, biomeBlend,
                minimalParticles, fastGraphics, cloudsOff, noEntityShadows, noViewBob,
                noSmoothLighting, vsyncOff);
    }

    @Override
    public void onEnable() {
        apply();
    }

    @Override
    public void onDisable() {
        restore();
    }

    private void apply() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.options == null) return;
        GameOptions o = mc.options;

        if (!captured) {
            oRender = o.getViewDistance().getValue();
            oSim = o.getSimulationDistance().getValue();
            oFps = o.getMaxFps().getValue();
            oMip = o.getMipmapLevels().getValue();
            oBlend = o.getBiomeBlendRadius().getValue();
            oEntity = o.getEntityDistanceScaling().getValue();
            oParticles = o.getParticles().getValue();
            oGraphics = o.getGraphicsMode().getValue();
            oClouds = o.getCloudRenderMode().getValue();
            oShadows = o.getEntityShadows().getValue();
            oBob = o.getBobView().getValue();
            oAo = o.getAo().getValue();
            oVsync = o.getEnableVsync().getValue();
            captured = true;
        }

        o.getViewDistance().setValue(renderDistance.getValueAsInt());
        o.getSimulationDistance().setValue((int) simDistance.get());
        o.getMaxFps().setValue((int) maxFps.get());
        o.getMipmapLevels().setValue((int) mipmap.get());
        o.getBiomeBlendRadius().setValue((int) biomeBlend.get());
        o.getEntityDistanceScaling().setValue(entityDistance.get());
        if (minimalParticles.get()) o.getParticles().setValue(ParticlesMode.MINIMAL);
        if (fastGraphics.get()) o.getGraphicsMode().setValue(GraphicsMode.FAST);
        if (cloudsOff.get()) o.getCloudRenderMode().setValue(CloudRenderMode.OFF);
        if (noEntityShadows.get()) o.getEntityShadows().setValue(false);
        if (noViewBob.get()) o.getBobView().setValue(false);
        if (noSmoothLighting.get()) o.getAo().setValue(false);
        if (vsyncOff.get()) o.getEnableVsync().setValue(false);

        o.write();
    }

    private void restore() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.options == null || !captured) return;
        GameOptions o = mc.options;

        o.getViewDistance().setValue(oRender);
        o.getSimulationDistance().setValue(oSim);
        o.getMaxFps().setValue(oFps);
        o.getMipmapLevels().setValue(oMip);
        o.getBiomeBlendRadius().setValue(oBlend);
        o.getEntityDistanceScaling().setValue(oEntity);
        o.getParticles().setValue(oParticles);
        o.getGraphicsMode().setValue(oGraphics);
        o.getCloudRenderMode().setValue(oClouds);
        o.getEntityShadows().setValue(oShadows);
        o.getBobView().setValue(oBob);
        o.getAo().setValue(oAo);
        o.getEnableVsync().setValue(oVsync);

        o.write();
        captured = false;
    }
}
