package net.glacierclient.cosmetics.hats;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
public final class FoxEarsHat extends GlacierMod {
    private final ColorSetting color = new ColorSetting("Color", 0xFFFF6B00);
    public FoxEarsHat() {
        super("FoxEars", "Renders fox ear accessories on your head", Category.COSMETICS, -1);
        addSettings(color);
    }
    @EventListen
    public void onRenderEntity(RenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || !event.getEntity().equals(mc.player)) return;
        MatrixStack ms = event.getMatrixStack();
        ms.push();
        ms.translate(0, 1.85, 0);
        ms.pop();
    }
}
