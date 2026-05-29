package net.glacierclient.modules.advanced.mechanics;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
public final class AirStrafeExtendedMod extends GlacierMod {
    private final NumberSetting factor = new NumberSetting("Factor", 1.2f, 1.0f, 3.0f);
    public AirStrafeExtendedMod() {
        super("AirStrafe", "Provides extended air strafing control", Category.ADVANCED, -1);
        addSettings(factor);
    }
    @EventListen
    public void onMove(PlayerMoveEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.player.isOnGround()) return;
        mc.player.setVelocity(
            mc.player.getVelocity().x * factor.get(),
            mc.player.getVelocity().y,
            mc.player.getVelocity().z * factor.get()
        );
    }
}
