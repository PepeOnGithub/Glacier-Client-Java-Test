package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.modules.render.ItemPhysics;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Applies the Item Physics module's extra spin / bounce to dropped item entities as they render. */
@Mixin(ItemEntityRenderer.class)
public class MixinItemEntityRenderer {

    @Shadow @Final private ItemRenderer itemRenderer;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void glacier$itemPhysics(ItemEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                                     VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return;
        ItemPhysics mod = gc.getModuleManager().getModule(ItemPhysics.class);
        if (mod == null || !mod.isEnabled()) return;

        ci.cancel(); // Completely replace vanilla item rendering for realism!

        matrices.push();
        ItemStack stack = entity.getStack();
        int seed = stack.isEmpty() ? 187 : net.minecraft.item.Item.getRawId(stack.getItem()) + stack.getDamage();
        BakedModel bakedModel = this.itemRenderer.getModel(stack, entity.getWorld(), null, seed);

        // Calculate physics values
        float age = entity.age + tickDelta;

        // Custom bounce & floor alignment
        float yOffset = 0.0f;
        if (mod.isBounceEnabled()) {
            float decay = Math.max(0f, 1f - age / 60f);
            yOffset = Math.abs(MathHelper.sin(age * 0.4f)) * 0.15f * decay;
        }

        // Translate item downwards so it sits flush on the block ground
        float scaleY = bakedModel.getTransformation().getTransformation(ModelTransformationMode.GROUND).scale.y();
        matrices.translate(0.0, yOffset + 0.05f * scaleY, 0.0);

        // Lay flat on the ground — items must NOT spin; they settle and rest like real physics.
        boolean onGround = entity.isOnGround();
        if (onGround) {
            // Rotate 90 degrees around X so the item lies flush on the block surface.
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));

            // A FIXED per-item yaw (not time-based) so multiple drops fan out instead of overlapping.
            // No continuous rotation — the item stays still once it has landed.
            if (mod.isRotationEnabled()) {
                float fixedYaw = (entity.getId() * 47f) % 360f;
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(fixedYaw));
            }
        }
        // While airborne the item simply falls (bounce handled above) — no spinning.

        // Render the item
        this.itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, false, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, bakedModel);

        matrices.pop();
    }
}
