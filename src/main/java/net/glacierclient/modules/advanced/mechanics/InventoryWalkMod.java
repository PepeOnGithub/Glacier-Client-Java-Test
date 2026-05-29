package net.glacierclient.modules.advanced.mechanics;
import net.glacierclient.core.module.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
public final class InventoryWalkMod extends GlacierMod {
    public InventoryWalkMod() { super("InventoryWalk", "Allows moving while a container GUI is open", Category.QOL, -1); }
    @EventListen
    public void onUpdate(TickEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen == null || mc.player == null) return;
        mc.player.input.movementForward = mc.options.forwardKey.isPressed() ? 1f : mc.options.backKey.isPressed() ? -1f : 0f;
        mc.player.input.movementSideways = mc.options.leftKey.isPressed() ? 1f : mc.options.rightKey.isPressed() ? -1f : 0f;
    }
}
