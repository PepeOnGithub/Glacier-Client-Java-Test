package net.glacierclient.modules.advanced.mechanics;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
import net.glacierclient.core.event.*;
import net.minecraft.client.MinecraftClient;
public final class HighJumpMod extends GlacierMod {
    private final NumberSetting power = new NumberSetting("Power", 0.5f, 0.1f, 3.0f);
    public HighJumpMod() {
        super("HighJump", "Increases jump height by adding extra upward velocity", Category.ADVANCED, -1);
        addSettings(power);
    }
    @EventListen
    public void onMove(EventPlayerMove event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || !mc.player.isOnGround()) return;
        if (mc.options.jumpKey.isPressed()) {
            mc.player.addVelocity(0, power.get(), 0);
        }
    }
}
