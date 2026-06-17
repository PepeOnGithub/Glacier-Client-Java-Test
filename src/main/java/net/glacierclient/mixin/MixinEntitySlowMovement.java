package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.modules.advanced.mechanics.PhaseWalkMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels cobweb (and configurable sticky-block) slowdown for the local player when PhaseWalk is on,
 * letting them walk through webs unhindered.
 */
@Mixin(Entity.class)
public class MixinEntitySlowMovement {

    @Inject(method = "slowMovement", at = @At("HEAD"), cancellable = true, require = 0)
    private void glacier$phaseWalk(BlockState state, net.minecraft.util.math.Vec3d multiplier, CallbackInfo ci) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return;
        if ((Object) this != MinecraftClient.getInstance().player) return;
        PhaseWalkMod mod = gc.getModuleManager().getModule(PhaseWalkMod.class);
        if (mod == null || !mod.isEnabled()) return;
        if (mod.isWeb() && state.isOf(Blocks.COBWEB)) ci.cancel();
    }
}
