package net.glacierclient.cosmetics.pets;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
import net.glacierclient.core.event.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
public final class MiniWolfPet extends GlacierMod {
    private final NumberSetting scale = new NumberSetting("Scale", 0.4f, 0.2f, 0.8f);
    private final ColorSetting collarColor = new ColorSetting("CollarColor", 0xFFFF0000);
    public MiniWolfPet() {
        super("MiniWolf", "A tiny tamed wolf that trots alongside you", Category.COSMETICS, -1);
        addSettings(scale, collarColor);
    }
    @EventListen
    public void onRenderWorld(EventRenderWorld event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        MatrixStack ms = event.getMatrixStack();
        ms.push();
        ms.translate(0.5, 0.5, 0.5);
        ms.scale((float) scale.get(), (float) scale.get(), (float) scale.get());
        ms.pop();
    }
}
