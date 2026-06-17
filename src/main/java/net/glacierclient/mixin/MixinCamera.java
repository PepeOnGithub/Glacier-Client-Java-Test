package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.modules.pvp.PerspectiveMod;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 360 Perspective (freelook): after the camera has been positioned/oriented from the player, override
 * its rotation with the free-look yaw/pitch while freelook is active. First-person only matters for
 * the view direction (position stays at the eye), so this gives a true look-around.
 */
@Mixin(Camera.class)
public abstract class MixinCamera {

    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At("TAIL"), require = 0)
    private void glacier$freelook(BlockView area, Entity focusedEntity, boolean thirdPerson,
                                  boolean inverseView, float tickDelta, CallbackInfo ci) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return;
        PerspectiveMod p = gc.getModuleManager().getModule(PerspectiveMod.class);
        if (p != null && p.isActive()) {
            setRotation(p.getPerspYaw(), p.getPerspPitch());
        }
    }
}
