package net.glacierclient.modules.advanced.mechanics;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
public final class NoSlowMod extends GlacierMod {
    private final BooleanSetting inWater = new BooleanSetting("InWater", true);
    private final BooleanSetting eatingDrinking = new BooleanSetting("EatingDrinking", true);
    private final BooleanSetting soulsand = new BooleanSetting("SoulSand", true);
    public NoSlowMod() {
        super("NoSlow", "Prevents speed reduction from various effects", Category.PVP, -1);
        addSettings(inWater, eatingDrinking, soulsand);
    }
    @EventListen
    public void onMove(PlayerMoveEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (eatingDrinking.get() && mc.player.isUsingItem()) event.setMultiplier(1.0f);
        if (inWater.get() && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater())) event.setMultiplier(1.0f);
        if (soulsand.get() && mc.world != null) {
            var below = mc.world.getBlockState(mc.player.getBlockPos().down()).getBlock();
            if (below == net.minecraft.block.Blocks.SOUL_SAND || below == net.minecraft.block.Blocks.HONEY_BLOCK)
                event.setMultiplier(1.0f);
        }
    }
}
