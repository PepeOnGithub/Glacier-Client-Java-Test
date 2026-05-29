package net.glacierclient.cosmetics.pets;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
public final class MiniCreeperPet extends GlacierMod {
    private final NumberSetting scale = new NumberSetting("Scale", 0.3f, 0.1f, 0.8f);
    private final BooleanSetting float_ = new BooleanSetting("Float", true);
    public MiniCreeperPet() {
        super("MiniCreeper", "A tiny floating Creeper that follows you around", Category.COSMETICS, -1);
        addSettings(scale, float_);
    }
    @EventListen
    public void onRenderWorld(RenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        MatrixStack ms = event.getMatrixStack();
        ms.push();
        double bob = float_.get() ? Math.sin(System.currentTimeMillis() / 800.0) * 0.1 : 0;
        ms.translate(0.5, 1.2 + bob, 0.5);
        ms.scale((float) scale.get(), (float) scale.get(), (float) scale.get());
        ms.pop();
    }
}
