package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class ParticleThrottler extends GlacierMod {

    private final NumberSetting maxParticles = new NumberSetting("Max Particles", "Maximum particles at once", 0, 16384, 4096);
    private final NumberSetting particleDistance = new NumberSetting("Particle Distance", "Max particle render distance", 8, 128, 32);
    private final BooleanSetting skipInvisible = new BooleanSetting("Skip Invisible", "Skip invisible particle types", true);

    public ParticleThrottler() {
        super("Particle Throttler", "Limit particle count for better performance", Category.PERFORMANCE);
        addSettings(maxParticles, particleDistance, skipInvisible);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public int getMaxParticles() { return (int) maxParticles.getValue(); }
    public float getParticleDistance() { return (float) particleDistance.getValue(); }
    public boolean isSkipInvisible() { return skipInvisible.getValue(); }
}
