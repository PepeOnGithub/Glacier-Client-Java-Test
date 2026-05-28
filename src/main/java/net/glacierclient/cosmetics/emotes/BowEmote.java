package net.glacierclient.cosmetics.emotes;
import net.glacierclient.core.module.*;
import net.glacierclient.core.event.*;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
public final class BowEmote extends GlacierMod {
    private long startTime = -1;
    private static final long DURATION = 2000L;
    public BowEmote() { super("BowEmote", "Performs a respectful bowing animation", Category.COSMETICS, -1); }
    @EventListen
    public void onKey(EventKeyPress event) {
        if (event.getKey() == GLFW.GLFW_KEY_G) startTime = System.currentTimeMillis();
    }
    @EventListen
    public void onRenderEntity(EventRenderEntity event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || !event.getEntity().equals(mc.player)) return;
        if (startTime < 0 || System.currentTimeMillis() - startTime > DURATION) return;
        float t = (System.currentTimeMillis() - startTime) / (float) DURATION;
        float pitch = t < 0.5f ? t * 2 * 60f : (1f - t) * 2 * 60f;
        event.getMatrixStack().push();
        event.getMatrixStack().multiply(net.minecraft.util.math.RotationAxis.POSITIVE_X.rotationDegrees(pitch));
        event.getMatrixStack().pop();
    }
}
