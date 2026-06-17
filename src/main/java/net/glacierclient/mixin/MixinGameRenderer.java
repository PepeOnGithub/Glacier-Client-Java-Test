package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.event.events.RenderEvent;
import net.glacierclient.modules.render.FOVModule;
import net.glacierclient.modules.render.HurtCamIntensitySlider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Shadow private net.minecraft.client.MinecraftClient client;

    /**
     * Re-applies the hurt-camera tilt scaled by the Hurt Cam module's intensity slider (0 removes the
     * shake entirely). Mirrors vanilla {@code tiltViewWhenHurt} but with a configurable magnitude.
     */
    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true, require = 0)
    private void glacier$hurtCam(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return;
        HurtCamIntensitySlider mod = gc.getModuleManager().getModule(HurtCamIntensitySlider.class);
        if (mod == null || !mod.isEnabled()) return;
        if (!(this.client.getCameraEntity() instanceof LivingEntity living)) return;

        ci.cancel(); // we fully replace the vanilla tilt
        float scale = mod.getIntensity(false);
        float f = (float) living.hurtTime - tickDelta;
        if (living.isDead()) {
            float g = Math.min((float) living.deathTime + tickDelta, 20.0F);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(40.0F - 8000.0F / (g + 200.0F)));
        }
        if (f < 0.0F) return;
        f /= (float) living.maxHurtTime;
        f = MathHelper.sin(f * f * f * f * (float) Math.PI);
        float yaw = living.getDamageTiltYaw();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-f * 14.0F * scale));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderPre(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (GlacierClient.getInstance() != null) {
            GlacierClient.getInstance().getEventBus().post(new RenderEvent(null, tickDelta, RenderEvent.Phase.PRE));
        }
    }

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void glacier$getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return;
        FOVModule fov = gc.getModuleManager().getModule(FOVModule.class);
        if (fov == null || !fov.isEnabled()) return;
        cir.setReturnValue(fov.applyFov(cir.getReturnValueD()));
    }
}
