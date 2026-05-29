package net.glacierclient.cosmetics.pets;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
public final class MiniDragonPet extends GlacierMod {
    private final NumberSetting scale = new NumberSetting("Scale", 0.25f, 0.1f, 0.6f);
    private final ColorSetting color = new ColorSetting("DragonColor", 0xFF7B0A91);
    public MiniDragonPet() {
        super("MiniDragon", "A tiny Ender Dragon that hovers behind your shoulder", Category.COSMETICS, -1);
        addSettings(scale, color);
    }
    @EventListen
    public void onRenderWorld(RenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        MatrixStack ms = event.getMatrixStack();
        ms.push();
        ms.translate(-0.4, 1.5, -0.4);
        ms.scale((float) scale.get(), (float) scale.get(), (float) scale.get());
        ms.pop();
    }
}
