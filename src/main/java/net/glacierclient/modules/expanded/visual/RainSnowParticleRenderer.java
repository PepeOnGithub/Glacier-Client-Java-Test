package net.glacierclient.modules.expanded.visual;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class RainSnowParticleRenderer extends GlacierMod {

    private final NumberSetting particleCount = new NumberSetting("Particle Count", "Number of precipitation particles", 1000, 100, 5000);
    private final NumberSetting dropSize = new NumberSetting("Drop Size", "Size of individual rain/snow particles", 1.0, 0.5, 3.0);
    private final BooleanSetting splash = new BooleanSetting("Splash", "Render splash effect when rain hits blocks", false);
    private final BooleanSetting windEffect = new BooleanSetting("Wind Effect", "Apply wind drift to particles", false);

    public RainSnowParticleRenderer() {
        super("Rain/Snow Renderer", "High-quality precipitation particles", Category.RENDER);
        addSettings(particleCount, dropSize, splash, windEffect);
    }

    @Override
    public void onEnable() {
        // Override vanilla weather particle system
    }

    @Override
    public void onDisable() {
        // Restore vanilla weather particles
    }

    public int getParticleCount() { return (int) particleCount.getValue(); }
    public double getDropSize() { return dropSize.getValue(); }
    public boolean isSplash() { return splash.getValue(); }
    public boolean isWindEffect() { return windEffect.getValue(); }
}
