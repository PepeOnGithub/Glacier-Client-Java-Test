package net.glacierclient.cosmetics.wings;

import net.glacierclient.core.cosmetic.Cosmetic;
import net.glacierclient.core.cosmetic.CosmeticCategory;
import net.glacierclient.core.settings.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class FairyWings extends Cosmetic {
    private final NumberSetting span = new NumberSetting("Wing Span", "Wing width multiplier", 0.5, 3.0, 1.5, 0.1);
    private final BooleanSetting animated = new BooleanSetting("Animated", "Flap animation", true);
    private final NumberSetting flapSpeed = new NumberSetting("Flap Speed", "Animation speed", 0.1, 5.0, 1.0, 0.1);
    private final BooleanSetting sparkles = new BooleanSetting("Sparkles", "Enable sparkle particles", true);
    private final NumberSetting sparkleRate = new NumberSetting("Sparkle Rate", "Rate of sparkle particles", 0.1, 5.0, 1.0, 0.1);
    private final NumberSetting transparency = new NumberSetting("Transparency", "Wing transparency (alpha)", 50, 255, 180, 1);

    public FairyWings() {
        super("Fairy Wings", "Delicate translucent fairy wings with sparkles", CosmeticCategory.WINGS);
        addSettings(span, animated, flapSpeed, sparkles, sparkleRate, transparency);
    }

    @Override
    public void render(MatrixStack matrices, PlayerEntity player, float partialTicks) {
        if (!isEnabled()) return;
        matrices.push();
        matrices.translate(0, 0.1, -0.2);
        matrices.scale((float)(double)span.getValue(), (float)(double)span.getValue(), (float)(double)span.getValue());
        // render fairy wing geometry with sparkle particles here
        matrices.pop();
    }
}
