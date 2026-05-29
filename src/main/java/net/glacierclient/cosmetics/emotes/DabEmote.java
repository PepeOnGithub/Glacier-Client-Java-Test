package net.glacierclient.cosmetics.emotes;
import net.glacierclient.core.module.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
public final class DabEmote extends GlacierMod {
    private long startTime = -1;
    private static final long DURATION = 1500L;
    public DabEmote() { super("DabEmote", "Strikes a dab pose for 1.5 seconds when triggered", Category.COSMETICS, -1); }
    @EventListen
    public void onKey(KeyInputEvent event) {
        if (event.getKey() == GLFW.GLFW_KEY_J) startTime = System.currentTimeMillis();
    }
    @EventListen
    public void onRenderEntity(RenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || !event.getEntity().equals(mc.player)) return;
        if (startTime < 0 || System.currentTimeMillis() - startTime > DURATION) return;
        event.getMatrixStack().push();
        event.getMatrixStack().multiply(net.minecraft.util.math.RotationAxis.POSITIVE_X.rotationDegrees(-30));
        event.getMatrixStack().pop();
    }
}
