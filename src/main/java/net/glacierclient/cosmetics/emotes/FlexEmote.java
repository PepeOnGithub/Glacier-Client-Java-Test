package net.glacierclient.cosmetics.emotes;
import net.glacierclient.core.module.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
public final class FlexEmote extends GlacierMod {
    private long startTime = -1;
    private static final long DURATION = 2500L;
    public FlexEmote() { super("FlexEmote", "Strikes a muscle flexing pose with pulsing arms", Category.COSMETICS, -1); }
    @EventListen
    public void onKey(KeyInputEvent event) {
        if (event.getKey() == GLFW.GLFW_KEY_N) startTime = System.currentTimeMillis();
    }
    @EventListen
    public void onRenderEntity(RenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || !event.getEntity().equals(mc.player)) return;
        if (startTime < 0 || System.currentTimeMillis() - startTime > DURATION) return;
        float t = (float)((System.currentTimeMillis() - startTime) / (double) DURATION);
        float pulse = (float)(Math.sin(t * Math.PI * 6) * 15f);
        event.getMatrixStack().push();
        event.getMatrixStack().multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotationDegrees(pulse));
        event.getMatrixStack().pop();
    }
}
