package net.glacierclient.cosmetics.pets;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;import java.util.List;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
public final class MiniFoxPet extends GlacierMod {
    private final ModeSetting variant = new ModeSetting("Variant", List.of("Red", "Snow"), "Red");
    private final NumberSetting scale = new NumberSetting("Scale", 0.4f, 0.2f, 0.8f);
    public MiniFoxPet() {
        super("MiniFox", "A tiny sleeping fox that occasionally stretches", Category.COSMETICS, -1);
        addSettings(variant, scale);
    }
    @EventListen
    public void onRenderWorld(RenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        MatrixStack ms = event.getMatrixStack();
        ms.push();
        ms.translate(0.4, 0.6, 0.4);
        ms.scale((float) scale.get(), (float) scale.get(), (float) scale.get());
        ms.pop();
    }
}
