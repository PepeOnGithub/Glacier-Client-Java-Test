package net.glacierclient.cosmetics.pets;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
public final class MiniEndermanPet extends GlacierMod {
    private final NumberSetting scale = new NumberSetting("Scale", 0.3f, 0.1f, 0.8f);
    private final BooleanSetting particles = new BooleanSetting("EndermiteParticles", true);
    public MiniEndermanPet() {
        super("MiniEnderman", "A tiny Enderman pet that teleports when you sprint", Category.COSMETICS, -1);
        addSettings(scale, particles);
    }
    @EventListen
    public void onRenderWorld(RenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        MatrixStack ms = event.getMatrixStack();
        ms.push();
        ms.translate(0.5, 1.4, 0.5);
        ms.scale((float) scale.get(), (float) scale.get(), (float) scale.get());
        ms.pop();
    }
}
