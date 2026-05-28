package net.glacierclient.cosmetics.wings;

import net.glacierclient.core.cosmetic.Cosmetic;
import net.glacierclient.core.cosmetic.CosmeticCategory;
import net.glacierclient.core.settings.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class VoidWings extends Cosmetic {
    private final NumberSetting span = new NumberSetting("Wing Span", "Wing width multiplier", 0.5, 3.0, 1.5, 0.1);
    private final BooleanSetting animated = new BooleanSetting("Animated", "Flap animation", true);
    private final NumberSetting flapSpeed = new NumberSetting("Flap Speed", "Animation speed", 0.1, 5.0, 1.0, 0.1);
    private final BooleanSetting starField = new BooleanSetting("Star Field", "Enable animated starfield", true);
    private final NumberSetting starDensity = new NumberSetting("Star Density", "Density of stars", 0.1, 5.0, 2.0, 0.1);
    private final NumberSetting warpSpeed = new NumberSetting("Warp Speed", "Speed of warp effect", 0.1, 3.0, 0.5, 0.1);
    private final ColorSetting voidColor = new ColorSetting("Void Color", "Background void color", 0xFF1A1A2E);

    public VoidWings() {
        super("Void Wings", "Animated starfield void wings", CosmeticCategory.WINGS);
        addSettings(span, animated, flapSpeed, starField, starDensity, warpSpeed, voidColor);
    }

    @Override
    public void render(MatrixStack matrices, PlayerEntity player, float partialTicks) {
        if (!isEnabled()) return;
        matrices.push();
        matrices.translate(0, 0.1, -0.2);
        matrices.scale((float)(double)span.getValue(), (float)(double)span.getValue(), (float)(double)span.getValue());
        // render void wing geometry with starfield animation here
        matrices.pop();
    }
}
