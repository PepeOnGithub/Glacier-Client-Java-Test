package net.glacierclient.cosmetics.hats;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
public final class TopHat extends GlacierMod {
    private static final Identifier TEX = new Identifier("glacierclient", "textures/cosmetics/hats/tophat.png");
    private final ColorSetting color = new ColorSetting("Color", 0xFF111111);
    public TopHat() {
        super("TopHat", "Renders a classic top hat cosmetic above your head", Category.COSMETICS, -1);
        addSettings(color);
    }
    @EventListen
    public void onRenderEntity(RenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || !event.getEntity().equals(mc.player)) return;
        MatrixStack ms = event.getMatrixStack();
        ms.push();
        ms.translate(0, 1.85, 0);
        ms.scale(0.6f, 0.6f, 0.6f);
        ms.pop();
    }
}
