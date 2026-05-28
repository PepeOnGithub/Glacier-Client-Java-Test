package net.glacierclient.cosmetics.hats;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
import net.glacierclient.core.event.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
public final class CatEarsHat extends GlacierMod {
    private final ColorSetting color = new ColorSetting("Color", 0xFFFFB6C1);
    public CatEarsHat() {
        super("CatEars", "Renders cute cat ear accessories on your head", Category.COSMETICS, -1);
        addSettings(color);
    }
    @EventListen
    public void onRenderEntity(EventRenderEntity event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || !event.getEntity().equals(mc.player)) return;
        MatrixStack ms = event.getMatrixStack();
        ms.push();
        ms.translate(-0.15, 1.85, 0);
        ms.pop();
    }
}
