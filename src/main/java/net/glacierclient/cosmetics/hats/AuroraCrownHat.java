package net.glacierclient.cosmetics.hats;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
public final class AuroraCrownHat extends GlacierMod {
    private final BooleanSetting rainbow = new BooleanSetting("RainbowShift", true);
    public AuroraCrownHat() {
        super("AuroraCrown", "Renders a crown with flowing aurora borealis color effects", Category.COSMETICS, -1);
        addSettings(rainbow);
    }
    @EventListen
    public void onRenderEntity(RenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || !event.getEntity().equals(mc.player)) return;
        MatrixStack ms = event.getMatrixStack();
        ms.push();
        ms.translate(0, 1.92, 0);
        ms.pop();
    }
}
