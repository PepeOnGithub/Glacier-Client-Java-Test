package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.modules.advanced.mechanics.FastPlaceMod;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Clamps the vanilla item-use cooldown each tick so blocks/items place faster (FastPlace module). */
@Mixin(MinecraftClient.class)
public class MixinFastPlace {

    @Shadow private int itemUseCooldown;

    @Inject(method = "tick", at = @At("TAIL"), require = 0)
    private void glacier$fastPlace(CallbackInfo ci) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return;
        FastPlaceMod mod = gc.getModuleManager().getModule(FastPlaceMod.class);
        if (mod == null || !mod.isEnabled()) return;
        int delay = mod.getDelay();
        if (this.itemUseCooldown > delay) this.itemUseCooldown = delay;
    }
}
