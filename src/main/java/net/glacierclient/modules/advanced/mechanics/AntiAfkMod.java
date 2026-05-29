package net.glacierclient.modules.advanced.mechanics;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;import java.util.List;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
public final class AntiAfkMod extends GlacierMod {
    private final NumberSetting interval = new NumberSetting("Interval", 60, 10, 300);
    private final ModeSetting action = new ModeSetting("Action", List.of("Rotate", "Jump", "Sneak", "Chat"), "Rotate");
    private long lastAction = 0;
    public AntiAfkMod() {
        super("AntiAFK", "Prevents AFK timeout by performing periodic actions", Category.QOL, -1);
        addSettings(interval, action);
    }
    @EventListen
    public void onUpdate(TickEvent event) {
        long now = System.currentTimeMillis();
        if (now - lastAction < interval.get() * 1000L) return;
        lastAction = now;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        switch (action.get()) {
            case "Rotate" -> mc.player.setYaw(mc.player.getYaw() + 5);
            case "Jump"   -> mc.player.jump();
            case "Sneak"  -> mc.player.setSneaking(!mc.player.isSneaking());
        }
    }
}
