package net.glacierclient.cosmetics.emotes;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
public final class WaveEmote extends GlacierMod {
    private long startTime = -1;
    private static final long DURATION = 2000L;
    public WaveEmote() { super("WaveEmote", "Plays a waving arm animation when triggered", Category.COSMETICS, -1); }
    @EventListen
    public void onKey(KeyInputEvent event) {
        if (event.getKey() == GLFW.GLFW_KEY_H) startTime = System.currentTimeMillis();
    }
    @EventListen
    public void onRenderEntity(RenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || !event.getEntity().equals(mc.player)) return;
        if (startTime < 0 || System.currentTimeMillis() - startTime > DURATION) return;
        float t = (System.currentTimeMillis() - startTime) / (float) DURATION;
        float angle = (float) Math.sin(t * Math.PI * 4) * 45f;
        event.getMatrixStack().push();
        event.getMatrixStack().multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotationDegrees(angle));
        event.getMatrixStack().pop();
    }
}
