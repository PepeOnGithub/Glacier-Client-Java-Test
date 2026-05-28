package net.glacierclient.cosmetics.hats;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
import net.glacierclient.core.event.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
public final class HaloHat extends GlacierMod {
    private final ColorSetting color = new ColorSetting("Color", 0xFFFFD700);
    private final BooleanSetting glow = new BooleanSetting("Glow", true);
    private final NumberSetting speed = new NumberSetting("SpinSpeed", 1.0f, 0.1f, 5.0f);
    public HaloHat() {
        super("Halo", "Renders a glowing halo that slowly rotates above your head", Category.COSMETICS, -1);
        addSettings(color, glow, speed);
    }
    @EventListen
    public void onRenderEntity(EventRenderEntity event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || !event.getEntity().equals(mc.player)) return;
        MatrixStack ms = event.getMatrixStack();
        ms.push();
        ms.translate(0, 2.05, 0);
        long t = System.currentTimeMillis();
        ms.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees((t / 20f) * (float) speed.get()));
        ms.pop();
    }
}
