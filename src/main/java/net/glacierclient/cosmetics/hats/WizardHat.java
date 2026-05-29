package net.glacierclient.cosmetics.hats;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
public final class WizardHat extends GlacierMod {
    private final ColorSetting color = new ColorSetting("Color", 0xFF4B0082);
    private final BooleanSetting stars = new BooleanSetting("StarParticles", true);
    public WizardHat() {
        super("WizardHat", "Renders a pointy wizard hat with optional star particles", Category.COSMETICS, -1);
        addSettings(color, stars);
    }
    @EventListen
    public void onRenderEntity(RenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || !event.getEntity().equals(mc.player)) return;
        MatrixStack ms = event.getMatrixStack();
        ms.push();
        ms.translate(0, 2.0, 0);
        ms.pop();
    }
}
