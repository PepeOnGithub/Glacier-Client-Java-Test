package net.glacierclient.modules.advanced.mechanics;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
public final class ElytraFlightMod extends GlacierMod {
    private final NumberSetting speed = new NumberSetting("Speed", 1.5f, 0.5f, 5.0f);
    private final BooleanSetting boost = new BooleanSetting("BoostOnSneak", true);
    public ElytraFlightMod() {
        super("ElytraFlight", "Enhances elytra flight with speed controls", Category.ADVANCED, -1);
        addSettings(speed, boost);
    }
    @EventListen
    public void onMove(PlayerMoveEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || !mc.player.isFallFlying()) return;
        float yaw = (float) Math.toRadians(mc.player.getYaw());
        float pitch = (float) Math.toRadians(mc.player.getPitch());
        double s = speed.get() * 0.05;
        mc.player.addVelocity(-Math.sin(yaw) * Math.cos(pitch) * s, -Math.sin(pitch) * s, Math.cos(yaw) * Math.cos(pitch) * s);
    }
}
