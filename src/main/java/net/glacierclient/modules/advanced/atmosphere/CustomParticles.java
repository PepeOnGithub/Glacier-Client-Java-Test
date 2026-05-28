package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

public class CustomParticles extends GlacierMod {

    private final ModeSetting style = new ModeSetting("Style", "Particle style", new String[]{"Ice", "Star", "Spark", "Custom"}, "Ice");
    private final ColorSetting color = new ColorSetting("Color", "Particle color", 0xFF7289DA);
    private final NumberSetting count = new NumberSetting("Count", "Particles per trigger", 1, 50, 15);
    private final BooleanSetting onCrit = new BooleanSetting("On Crit", "Spawn particles on critical hit", true);
    private final BooleanSetting onHit = new BooleanSetting("On Hit", "Spawn particles on any hit", false);

    public CustomParticles() {
        super("Custom Particles", "Ice shard particles on critical hits", Category.RENDER);
        addSettings(style, color, count, onCrit, onHit);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
    @Override public void onTick() {}

    public void spawnHitParticles(Vec3d pos, boolean isCrit) {
        if (!isEnabled()) return;
        if (isCrit && !onCrit.getValue()) return;
        if (!isCrit && !onHit.getValue()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientWorld world = mc.world;
        if (world == null) return;
        int n = (int)(double) count.getValue();
        for (int i = 0; i < n; i++) {
            double vx = (Math.random() - 0.5) * 0.5;
            double vy = Math.random() * 0.5;
            double vz = (Math.random() - 0.5) * 0.5;
            world.addParticle(ParticleTypes.SNOWFLAKE, pos.x, pos.y + 0.5, pos.z, vx, vy, vz);
        }
    }
}
