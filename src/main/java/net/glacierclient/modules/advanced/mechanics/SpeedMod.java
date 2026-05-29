package net.glacierclient.modules.advanced.mechanics;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;import java.util.List;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
public final class SpeedMod extends GlacierMod {
    private final NumberSetting speed = new NumberSetting("Speed", 1.5f, 1.0f, 5.0f);
    private final ModeSetting mode = new ModeSetting("Mode", List.of("Vanilla", "Strafe", "Custom"), "Strafe");
    public SpeedMod() {
        super("Speed", "Increases movement speed using various acceleration methods", Category.ADVANCED, -1);
        addSettings(speed, mode);
    }
    @EventListen
    public void onMove(PlayerMoveEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || !mc.player.isOnGround()) return;
        if ("Strafe".equals(mode.get())) {
            double s = speed.get();
            mc.player.setVelocity(mc.player.getVelocity().x * s, mc.player.getVelocity().y, mc.player.getVelocity().z * s);
        }
    }
}
