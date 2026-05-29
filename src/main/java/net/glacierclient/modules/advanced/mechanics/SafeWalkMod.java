package net.glacierclient.modules.advanced.mechanics;
import net.glacierclient.core.module.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
public final class SafeWalkMod extends GlacierMod {
    public SafeWalkMod() { super("SafeWalk", "Prevents walking off edges by sneak-locking at ledges", Category.QOL, -1); }
    @EventListen
    public void onMove(PlayerMoveEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        mc.player.setSneaking(mc.player.isOnGround());
    }
}
