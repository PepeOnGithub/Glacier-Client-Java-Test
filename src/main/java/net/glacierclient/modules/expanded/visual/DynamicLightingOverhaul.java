package net.glacierclient.modules.expanded.visual;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

public class DynamicLightingOverhaul extends GlacierMod {

    private final BooleanSetting playerLight = new BooleanSetting("Player Light", "Emit light from the player when holding light sources", true);
    private final BooleanSetting torchLight = new BooleanSetting("Torch Light", "Emit dynamic light from held/dropped torches", true);
    private final BooleanSetting glowstoneLight = new BooleanSetting("Glowstone Light", "Emit dynamic light from held glowstone", true);
    private final NumberSetting lightRadius = new NumberSetting("Light Radius", "Radius of dynamic light in blocks", 15, 4, 32);
    private final NumberSetting updateRate = new NumberSetting("Update Rate", "Light update frequency per second", 5, 1, 20);

    private float dynamicLightLevel = 0f;
    private BlockPos lastLightPos = null;

    public DynamicLightingOverhaul() {
        super("Dynamic Lighting", "Real block-light from held sources (visual only)", Category.RENDER);
        addSettings(playerLight, torchLight, glowstoneLight, lightRadius, updateRate);
    }

    @Override
    public void onEnable() {
        dynamicLightLevel = 0f;
        lastLightPos = null;
    }

    @Override
    public void onDisable() {
        dynamicLightLevel = 0f;
        lastLightPos = null;
    }

    @Override
    public void onTick() {
        if (!isEnabled()) {
            dynamicLightLevel = 0f;
            return;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        PlayerEntity player = mc.player;
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();

        boolean emitsLight = false;
        float targetLight = 0f;

        if (playerLight.getValue()) {
            if (isLightSource(mainHand) || isLightSource(offHand)) {
                emitsLight = true;
                targetLight = (float) (double) lightRadius.getValue();
            }
        }

        if (emitsLight) {
            dynamicLightLevel += (targetLight - dynamicLightLevel) * 0.2f;
            lastLightPos = player.getBlockPos();
        } else {
            dynamicLightLevel += (0f - dynamicLightLevel) * 0.2f;
        }
    }

    private boolean isLightSource(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (torchLight.getValue() && (stack.isOf(Items.TORCH) || stack.isOf(Items.SOUL_TORCH) || stack.isOf(Items.REDSTONE_TORCH))) {
            return true;
        }
        if (glowstoneLight.getValue() && (stack.isOf(Items.GLOWSTONE) || stack.isOf(Items.SEA_LANTERN) || stack.isOf(Items.SHROOMLIGHT))) {
            return true;
        }
        return stack.isOf(Items.LAVA_BUCKET) || stack.isOf(Items.JACK_O_LANTERN) || stack.isOf(Items.BEACON);
    }

    /**
     * Custom self-made dynamic lighting multiplier hook.
     * Modifies the lightmap coordinates of nearby rendered blocks to simulate realistic dynamic casting.
     */
    public int getDynamicLightOffset(BlockPos pos, int defaultLight) {
        if (!isEnabled() || dynamicLightLevel <= 0.1f || lastLightPos == null) {
            return defaultLight;
        }

        double distanceSq = pos.getSquaredDistance(lastLightPos.getX(), lastLightPos.getY(), lastLightPos.getZ());
        double radius = lightRadius.getValue();
        double radiusSq = radius * radius;

        if (distanceSq < radiusSq) {
            double factor = 1.0 - (distanceSq / radiusSq);
            int addedLight = (int) (dynamicLightLevel * factor);
            
            // Extract block light component (lower 16 bits of lightmap coordinate texture)
            int blockLight = defaultLight & 0xFFFF;
            int skyLight = (defaultLight >> 16) & 0xFFFF;
            
            blockLight = Math.min(15, blockLight + addedLight);
            
            return blockLight | (skyLight << 16);
        }

        return defaultLight;
    }

    public boolean isPlayerLight() { return playerLight.getValue(); }
    public boolean isTorchLight() { return torchLight.getValue(); }
    public boolean isGlowstoneLight() { return glowstoneLight.getValue(); }
    public int getLightRadius() { return (int)(double) lightRadius.getValue(); }
    public int getUpdateRate() { return (int)(double) updateRate.getValue(); }
}
