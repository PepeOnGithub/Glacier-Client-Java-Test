package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.modules.performance.ParticleThrottler;
import net.glacierclient.modules.pvp.CustomBlockBreakParticles;
import net.glacierclient.modules.pvp.ParticlesMultiplier;
import net.glacierclient.modules.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Suppresses particle categories (No Render), removes block-break particles, and multiplies density. */
@Mixin(ParticleManager.class)
public class MixinParticleManager {

    // Guard so multiplier-spawned extra particles don't recurse into this hook.
    private static boolean glacier$spawningExtra = false;

    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At("HEAD"), cancellable = true)
    private void glacier$filterParticles(ParticleEffect parameters, double x, double y, double z,
                                         double vx, double vy, double vz, CallbackInfoReturnable<Particle> cir) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return;

        ParticleType<?> ptype = parameters.getType();

        // Custom Block Break Particles: optionally remove block-break particles entirely.
        CustomBlockBreakParticles cb = gc.getModuleManager().getModule(CustomBlockBreakParticles.class);
        if (cb != null && cb.isEnabled() && cb.isRemoved() && ptype == ParticleTypes.BLOCK) {
            cir.setReturnValue(null);
            return;
        }

        // Particles Multiplier: scale density up (extra spawns) or down (probabilistic skip).
        ParticlesMultiplier pmul = gc.getModuleManager().getModule(ParticlesMultiplier.class);
        if (pmul != null && pmul.isEnabled() && !glacier$spawningExtra) {
            String cat = ptype == ParticleTypes.CRIT ? "crit"
                       : ptype == ParticleTypes.SWEEP_ATTACK ? "sweep"
                       : ptype == ParticleTypes.BLOCK ? "block" : "other";
            float mult = pmul.getMultiplier(cat);
            if (mult < 1f && Math.random() > mult) { cir.setReturnValue(null); return; }
            if (mult > 1f) {
                int extra = Math.min(4, (int) mult - 1);
                float frac = mult - (int) mult;
                ParticleManager self = (ParticleManager) (Object) this;
                glacier$spawningExtra = true;
                try {
                    for (int e = 0; e < extra; e++) self.addParticle(parameters, x, y, z, vx, vy, vz);
                    if (frac > 0 && Math.random() < frac) self.addParticle(parameters, x, y, z, vx, vy, vz);
                } finally {
                    glacier$spawningExtra = false;
                }
            }
        }

        // Particle Throttler: drop particles spawning beyond the configured distance from the player.
        ParticleThrottler pt = gc.getModuleManager().getModule(ParticleThrottler.class);
        if (pt != null && pt.isEnabled()) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null) {
                double dx = x - mc.player.getX(), dy = y - mc.player.getY(), dz = z - mc.player.getZ();
                double d = pt.getParticleDistance();
                if (dx * dx + dy * dy + dz * dz > d * d) { cir.setReturnValue(null); return; }
            }
        }

        NoRender nr = gc.getModuleManager().getModule(NoRender.class);
        if (nr == null || !nr.isEnabled()) return;
        if (nr.isNoParticles()) { cir.setReturnValue(null); return; }

        ParticleType<?> t = parameters.getType();
        boolean blocked =
                (nr.isNoExplosions() && (t == ParticleTypes.EXPLOSION || t == ParticleTypes.EXPLOSION_EMITTER))
             || (nr.isNoFirework() && t == ParticleTypes.FIREWORK)
             || (nr.isNoBlockBreak() && t == ParticleTypes.BLOCK)
             || (nr.isNoPotionParticles() && (t == ParticleTypes.EFFECT || t == ParticleTypes.INSTANT_EFFECT
                    || t == ParticleTypes.ENTITY_EFFECT || t == ParticleTypes.AMBIENT_ENTITY_EFFECT));
        if (blocked) cir.setReturnValue(null);
    }
}
