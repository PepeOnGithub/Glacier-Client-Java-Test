package net.glacierclient.cosmetics.wings;

import net.glacierclient.core.cosmetic.Cosmetic;
import net.glacierclient.core.cosmetic.CosmeticCategory;
import net.glacierclient.core.settings.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class ButterflyWings extends Cosmetic {
    private final NumberSetting span = new NumberSetting("Wing Span", "Wing width multiplier", 0.5, 3.0, 1.5, 0.1);
    private final BooleanSetting animated = new BooleanSetting("Animated", "Flap animation", true);
    private final NumberSetting flapSpeed = new NumberSetting("Flap Speed", "Animation speed", 0.1, 5.0, 1.0, 0.1);
    private final BooleanSetting iridescent = new BooleanSetting("Iridescent", "Enable iridescent sheen", true);
    private final NumberSetting patternScale = new NumberSetting("Pattern Scale", "Scale of wing pattern", 0.5, 3.0, 1.0, 0.1);
    private final ModeSetting pattern = new ModeSetting("Pattern", "Wing pattern style", "Monarch", "Monarch", "Blue Morpho", "Glasswing", "Custom");

    public ButterflyWings() {
        super("Butterfly Wings", "Colorful iridescent butterfly wings", CosmeticCategory.WINGS);
        addSettings(span, animated, flapSpeed, iridescent, patternScale, pattern);
    }

    @Override
    public void render(MatrixStack matrices, PlayerEntity player, float partialTicks) {
        if (!isEnabled()) return;
        matrices.push();
        matrices.translate(0, 0.1, -0.2);
        matrices.scale((float)(double)span.getValue(), (float)(double)span.getValue(), (float)(double)span.getValue());
        // render butterfly wing geometry with pattern here
        matrices.pop();
    }
}
