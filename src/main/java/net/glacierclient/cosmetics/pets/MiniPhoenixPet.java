package net.glacierclient.cosmetics.pets;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
import net.glacierclient.core.event.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
public final class MiniPhoenixPet extends GlacierMod {
    private final NumberSetting scale = new NumberSetting("Scale", 0.35f, 0.1f, 0.8f);
    private final BooleanSetting fireTrail = new BooleanSetting("FireTrail", true);
    public MiniPhoenixPet() {
        super("MiniPhoenix", "A burning phoenix pet that orbits you while flying", Category.COSMETICS, -1);
        addSettings(scale, fireTrail);
    }
    @EventListen
    public void onRenderWorld(EventRenderWorld event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        MatrixStack ms = event.getMatrixStack();
        ms.push();
        long t = System.currentTimeMillis();
        double angle = t / 2000.0 * Math.PI * 2;
        ms.translate(Math.cos(angle) * 0.8, 1.3, Math.sin(angle) * 0.8);
        ms.scale((float) scale.get(), (float) scale.get(), (float) scale.get());
        ms.pop();
    }
}
