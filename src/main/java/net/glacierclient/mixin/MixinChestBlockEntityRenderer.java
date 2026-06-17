package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.modules.performance.FastChestOpenFix;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Fast Chest Open Fix — snaps or removes the chest/ender-chest/trapped-chest lid animation. */
@Mixin(ChestBlockEntityRenderer.class)
public class MixinChestBlockEntityRenderer {

    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/entity/LidOpenable;getAnimationProgress(F)F"), require = 0)
    private float glacier$lid(LidOpenable instance, float tickDelta) {
        float progress = instance.getAnimationProgress(tickDelta);
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return progress;
        FastChestOpenFix mod = gc.getModuleManager().getModule(FastChestOpenFix.class);
        if (mod == null || !mod.isEnabled()) return progress;
        if (mod.isInstantOpen()) return progress > 0.001f ? 1f : 0f;   // jump straight to fully open
        if (mod.isSkipAnimation()) return Math.round(progress);        // snap, no easing
        return progress;
    }
}
