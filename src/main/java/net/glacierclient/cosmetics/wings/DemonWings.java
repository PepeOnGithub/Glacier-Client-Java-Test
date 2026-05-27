package net.glacierclient.cosmetics.wings;

import net.glacierclient.core.cosmetic.Cosmetic;
import net.glacierclient.core.cosmetic.CosmeticCategory;
import net.glacierclient.core.settings.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class DemonWings extends Cosmetic {
    private final NumberSetting span = new NumberSetting("Wing Span", "Wing width multiplier", 0.5, 3.0, 1.5, 0.1);
    private final BooleanSetting animated = new BooleanSetting("Animated", "Flap animation", true);
    private final NumberSetting flapSpeed = new NumberSetting("Flap Speed", "Animation speed", 0.1, 5.0, 1.0, 0.1);
    private final ModeSetting style = new ModeSetting("Style", "Wing style variant", "Black", "Black", "Crimson", "Purple", "Void");
    private final NumberSetting spike = new NumberSetting("Spike Size", "Size of wing spikes", 0.5, 2.0, 1.0, 0.1);

    public DemonWings() {
        super("Demon Wings", "Dark corrupted bat-like wings", CosmeticCategory.WINGS);
        addSettings(span, animated, flapSpeed, style, spike);
    }

    @Override
    public void render(MatrixStack matrices, PlayerEntity player, float partialTicks) {
        if (!isEnabled()) return;
        matrices.push();
        matrices.translate(0, 0.1, -0.2);
        matrices.scale((float)(double)span.getValue(), (float)(double)span.getValue(), (float)(double)span.getValue());
        // render demon wing geometry with spikes here
        matrices.pop();
    }
}
