package net.glacierclient.cosmetics.pets;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
public final class MiniBlazePet extends GlacierMod {
    private final NumberSetting scale = new NumberSetting("Scale", 0.3f, 0.1f, 0.7f);
    public MiniBlazePet() {
        super("MiniBlaze", "A miniature Blaze that spins and emits flame particles", Category.COSMETICS, -1);
        addSettings(scale);
    }
    @EventListen
    public void onRenderWorld(RenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        MatrixStack ms = event.getMatrixStack();
        ms.push();
        long t = System.currentTimeMillis();
        ms.translate(0, 1.2 + Math.sin(t / 500.0) * 0.1, 0.6);
        ms.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(t / 20f));
        ms.scale((float) scale.get(), (float) scale.get(), (float) scale.get());
        ms.pop();
    }
}
