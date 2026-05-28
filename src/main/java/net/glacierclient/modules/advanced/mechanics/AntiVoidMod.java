package net.glacierclient.modules.advanced.mechanics;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
import net.glacierclient.core.event.*;
import net.minecraft.client.MinecraftClient;
public final class AntiVoidMod extends GlacierMod {
    private final NumberSetting yThreshold = new NumberSetting("YThreshold", -60, -200, 0);
    public AntiVoidMod() {
        super("AntiVoid", "Activates flight temporarily when falling below Y threshold", Category.ADVANCED, -1);
        addSettings(yThreshold);
    }
    @EventListen
    public void onUpdate(EventUpdate event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (mc.player.getY() < yThreshold.get() && !mc.player.isOnGround()) {
            mc.player.setVelocity(mc.player.getVelocity().x, 0.05, mc.player.getVelocity().z);
        }
    }
}
