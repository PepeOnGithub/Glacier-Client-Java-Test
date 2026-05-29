package net.glacierclient.cosmetics.hats;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;import java.util.List;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
public final class CrownHat extends GlacierMod {
    private static final Identifier TEX = new Identifier("glacierclient", "textures/cosmetics/hats/crown.png");
    private final ModeSetting material = new ModeSetting("Material", List.of("Gold", "Diamond", "Netherite"), "Gold");
    public CrownHat() {
        super("CrownHat", "Renders a royal crown cosmetic above your head", Category.COSMETICS, -1);
        addSettings(material);
    }
    @EventListen
    public void onRenderEntity(RenderEvent event) {
        if (MinecraftClient.getInstance().player == null) return;
        if (!event.getEntity().equals(MinecraftClient.getInstance().player)) return;
        MatrixStack ms = event.getMatrixStack();
        ms.push();
        ms.translate(0, 1.9, 0);
        ms.scale(0.5f, 0.5f, 0.5f);
        ms.pop();
    }
}
