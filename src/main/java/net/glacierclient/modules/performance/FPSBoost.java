package net.glacierclient.modules.performance;

import net.glacierclient.core.module.Category;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.ParticlesMode;

/**
 * Unlimited FPS boost — removes the framerate cap and trims the most expensive vanilla render work.
 * All changes are pure client video options, saved on enable and fully restored on disable.
 */
public class FPSBoost extends GlacierMod {

    private final BooleanSetting unlimited = new BooleanSetting("Unlimited FPS", "Remove the framerate cap entirely", true);
    private final BooleanSetting noVsync = new BooleanSetting("Disable VSync", "Uncap frames past the monitor refresh", true);
    private final BooleanSetting noEntityShadows = new BooleanSetting("No Entity Shadows", "Skip per-entity blob shadows", true);
    private final BooleanSetting minimalParticles = new BooleanSetting("Minimal Particles", "Cut particle rendering to a minimum", true);
    private final BooleanSetting noBobbing = new BooleanSetting("No View Bobbing", "Disable hand/view bob", false);

    private int savedMaxFps = 120;
    private boolean savedVsync = true;
    private boolean savedShadows = true;
    private ParticlesMode savedParticles = ParticlesMode.ALL;
    private boolean savedBobbing = true;

    public FPSBoost() {
        super("FPS Boost", "Uncaps the framerate and trims expensive render work for maximum FPS", Category.PERFORMANCE);
        addSettings(unlimited, noVsync, noEntityShadows, minimalParticles, noBobbing);
    }

    @Override
    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options == null) return;
        savedMaxFps = mc.options.getMaxFps().getValue();
        savedVsync = mc.options.getEnableVsync().getValue();
        savedShadows = mc.options.getEntityShadows().getValue();
        savedParticles = mc.options.getParticles().getValue();
        savedBobbing = mc.options.getBobView().getValue();
        apply(mc);
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options == null) return;
        mc.options.getMaxFps().setValue(savedMaxFps);
        mc.options.getEnableVsync().setValue(savedVsync);
        mc.options.getEntityShadows().setValue(savedShadows);
        mc.options.getParticles().setValue(savedParticles);
        mc.options.getBobView().setValue(savedBobbing);
    }

    @Override
    public void onTick() {
        // Re-assert each tick so other code / the options screen can't quietly revert the boost.
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options != null) apply(mc);
    }

    private void apply(MinecraftClient mc) {
        if (unlimited.getValue()) mc.options.getMaxFps().setValue(260); // 260 == "Unlimited" in vanilla
        if (noVsync.getValue()) mc.options.getEnableVsync().setValue(false);
        if (noEntityShadows.getValue()) mc.options.getEntityShadows().setValue(false);
        if (minimalParticles.getValue()) mc.options.getParticles().setValue(ParticlesMode.MINIMAL);
        if (noBobbing.getValue()) mc.options.getBobView().setValue(false);
    }
}
