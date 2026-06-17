package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.modules.performance.SmartCulling;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Distance / far-player culling for the Smart Culling module — skips entities vanilla would draw. */
@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Inject(method = "shouldRender", at = @At("RETURN"), cancellable = true, require = 0)
    private void glacier$cull(Entity entity, Frustum frustum, double camX, double camY, double camZ,
                              CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) return; // vanilla already culled it
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return;
        SmartCulling sc = gc.getModuleManager().getModule(SmartCulling.class);
        if (sc == null || !sc.isEnabled()) return;
        if (sc.shouldCull(entity)) { cir.setReturnValue(false); return; }
        if (entity instanceof PlayerEntity p && sc.shouldHidePlayer(p)) cir.setReturnValue(false);
    }
}
