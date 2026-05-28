package net.glacierclient.cosmetics.emotes;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
import net.glacierclient.core.event.*;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
public final class SpinEmote extends GlacierMod {
    private final NumberSetting speed = new NumberSetting("Speed", 2.0f, 0.5f, 10.0f);
    private boolean spinning = false;
    private long startTime = -1;
    public SpinEmote() {
        super("SpinEmote", "Spins the player 360° when triggered", Category.COSMETICS, -1);
        addSettings(speed);
    }
    @EventListen
    public void onKey(EventKeyPress event) {
        if (event.getKey() == GLFW.GLFW_KEY_K) { spinning = true; startTime = System.currentTimeMillis(); }
    }
    @EventListen
    public void onUpdate(EventUpdate event) {
        if (!spinning) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        mc.player.setYaw(mc.player.getYaw() + (float) speed.get() * 10f);
        if (System.currentTimeMillis() - startTime > 1000) spinning = false;
    }
}
