package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

public class CustomDeathAnimations extends GlacierMod {

    private final ModeSetting style = new ModeSetting("Style", "Death animation style",
        new String[]{"Vanilla", "Explosion", "Fade", "Spiral"}, "Fade");
    private final BooleanSetting onPlayerDeath = new BooleanSetting("On Player Death", "Trigger on player death", true);
    private final BooleanSetting onMobDeath = new BooleanSetting("On Mob Death", "Trigger on mob death", true);
    private final ColorSetting particleColor = new ColorSetting("Particle Color", "Death particle color", GlacierTheme.ACCENT);

    public CustomDeathAnimations() {
        super("Death Animations", "Visual effects when entities die", Category.RENDER);
        addSettings(style, onPlayerDeath, onMobDeath, particleColor);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
    @Override public void onTick() {}

    public void onEntityDeath(Entity entity) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientWorld world = mc.world;
        if (world == null) return;
        // Honour which entity kinds should trigger the effect.
        boolean isPlayer = entity instanceof net.minecraft.entity.player.PlayerEntity;
        if (isPlayer && !onPlayerDeath.getValue()) return;
        if (!isPlayer && !onMobDeath.getValue()) return;

        Vec3d pos = entity.getPos();
        String styleVal = style.getValue();
        if ("Vanilla".equals(styleVal)) return;

        int rgb = particleColor.getValue();
        org.joml.Vector3f tint = new org.joml.Vector3f(
            ((rgb >> 16) & 0xFF) / 255f, ((rgb >> 8) & 0xFF) / 255f, (rgb & 0xFF) / 255f);
        net.minecraft.particle.DustParticleEffect dust = new net.minecraft.particle.DustParticleEffect(tint, 1.0f);

        if ("Explosion".equals(styleVal)) {
            for (int i = 0; i < 20; i++) {
                double vx = (Math.random() - 0.5) * 0.5;
                double vy = Math.random() * 0.5;
                double vz = (Math.random() - 0.5) * 0.5;
                world.addParticle(ParticleTypes.EXPLOSION, pos.x, pos.y + 1, pos.z, vx, vy, vz);
                world.addParticle(dust, pos.x, pos.y + 1, pos.z, vx, vy, vz);
            }
        } else if ("Fade".equals(styleVal) || "Spiral".equals(styleVal)) {
            for (int i = 0; i < 15; i++) {
                double angle = ("Spiral".equals(styleVal)) ? (i / 15.0 * Math.PI * 2) : Math.random() * Math.PI * 2;
                double vx = Math.cos(angle) * 0.3;
                double vz2 = Math.sin(angle) * 0.3;
                world.addParticle(dust, pos.x, pos.y + 1, pos.z, vx, 0.1, vz2);
            }
        }
    }
}
