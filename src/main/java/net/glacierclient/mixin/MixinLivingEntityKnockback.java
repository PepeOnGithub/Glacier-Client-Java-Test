package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.modules.advanced.mechanics.AntiKnockbackMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Scales the strength of knockback applied to the local player according to the AntiKnockback module.
 */
@Mixin(LivingEntity.class)
public class MixinLivingEntityKnockback {

    @ModifyVariable(method = "takeKnockback", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private double glacier$reduceKnockback(double strength) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return strength;
        // Only affect the local player, not other entities.
        if ((Object) this != MinecraftClient.getInstance().player) return strength;
        AntiKnockbackMod mod = gc.getModuleManager().getModule(AntiKnockbackMod.class);
        if (mod == null || !mod.isEnabled()) return strength;
        return strength * mod.getFactor();
    }
}
