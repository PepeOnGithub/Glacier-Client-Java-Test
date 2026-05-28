package net.glacierclient.cosmetics.hats;
import net.glacierclient.core.module.*;
import net.glacierclient.core.event.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
public final class PumpkinHat extends GlacierMod {
    public PumpkinHat() { super("PumpkinHat", "Renders a carved pumpkin as a cosmetic hat overlay", Category.COSMETICS, -1); }
    @EventListen
    public void onRenderEntity(EventRenderEntity event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || !event.getEntity().equals(mc.player)) return;
        MatrixStack ms = event.getMatrixStack();
        ms.push();
        ms.translate(0, 1.75, 0);
        ms.scale(0.55f, 0.55f, 0.55f);
        ms.pop();
    }
}
