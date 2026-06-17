package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.modules.render.BlockOverlay;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {

    @Inject(method = "drawBlockOutline", at = @At("HEAD"), cancellable = true)
    private void glacier$drawBlockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity,
                                          double cameraX, double cameraY, double cameraZ, BlockPos pos,
                                          BlockState state, CallbackInfo ci) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return;
        BlockOverlay mod = gc.getModuleManager().getModule(BlockOverlay.class);
        if (mod == null || !mod.isEnabled()) return;

        // Custom selected block overlay color & style!
        ci.cancel(); // Disable vanilla black outline

        // Retrieve custom settings
        int color = mod.getColor();
        float red = ((color >> 16) & 0xFF) / 255f;
        float green = ((color >> 8) & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;
        float alpha = ((color >> 24) & 0xFF) / 255f;
        if (alpha == 0) alpha = 0.4f;

        net.minecraft.util.shape.VoxelShape shape = state.getOutlineShape(entity.getWorld(), pos);
        if (shape.isEmpty()) return;

        double x = pos.getX() - cameraX;
        double y = pos.getY() - cameraY;
        double z = pos.getZ() - cameraZ;

        // Draw outline using configured colors
        WorldRenderer.drawShapeOutline(matrices, vertexConsumer, shape, x, y, z, red, green, blue, alpha, true);

        // Fill chosen block if fill is enabled!
        if (mod.isFill()) {
            int fillC = mod.getFillColor();
            float fRed = ((fillC >> 16) & 0xFF) / 255f;
            float fGreen = ((fillC >> 8) & 0xFF) / 255f;
            float fBlue = (fillC & 0xFF) / 255f;
            float fAlpha0 = ((fillC >> 24) & 0xFF) / 255f;
            final float fAlpha = fAlpha0 == 0 ? 0.15f : fAlpha0;

            // Draw beautiful translucent solid box filling the target block VoxelShape!
            shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
                WorldRenderer.drawBox(matrices, vertexConsumer,
                    minX + x, minY + y, minZ + z,
                    maxX + x, maxY + y, maxZ + z,
                    fRed, fGreen, fBlue, fAlpha);
            });
        }
    }
}
