package net.glacierclient.cosmetics.pets;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;import java.util.List;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
public final class MiniParrotPet extends GlacierMod {
    private final ModeSetting variant = new ModeSetting("Variant", List.of("Red", "Blue", "Green", "Yellow"), "Red");
    public MiniParrotPet() {
        super("MiniParrot", "A colorful parrot that perches on your shoulder", Category.COSMETICS, -1);
        addSettings(variant);
    }
    @EventListen
    public void onRenderWorld(RenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        MatrixStack ms = event.getMatrixStack();
        ms.push();
        ms.translate(0.25, 1.65, 0.1);
        ms.scale(0.3f, 0.3f, 0.3f);
        ms.pop();
    }
}
