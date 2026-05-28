package net.glacierclient.modules.advanced.mechanics;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
import net.glacierclient.core.event.*;
import net.minecraft.client.MinecraftClient;
public final class LongJumpMod extends GlacierMod {
    private final NumberSetting boost = new NumberSetting("Boost", 1.8f, 1.0f, 4.0f);
    public LongJumpMod() {
        super("LongJump", "Boosts horizontal velocity when jumping", Category.ADVANCED, -1);
        addSettings(boost);
    }
    @EventListen
    public void onMove(EventPlayerMove event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (mc.options.jumpKey.isPressed() && mc.player.isOnGround()) {
            double yaw = Math.toRadians(mc.player.getYaw());
            mc.player.addVelocity(-Math.sin(yaw) * boost.get() * 0.1, 0, Math.cos(yaw) * boost.get() * 0.1);
        }
    }
}
