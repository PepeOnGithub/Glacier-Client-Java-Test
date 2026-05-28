package net.glacierclient.cosmetics.wings;

import net.glacierclient.core.cosmetic.Cosmetic;
import net.glacierclient.core.cosmetic.CosmeticCategory;
import net.glacierclient.core.settings.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class PhoenixWings extends Cosmetic {
    private final NumberSetting span = new NumberSetting("Wing Span", "Wing width multiplier", 0.5, 3.0, 1.5, 0.1);
    private final BooleanSetting animated = new BooleanSetting("Animated", "Flap animation", true);
    private final NumberSetting flapSpeed = new NumberSetting("Flap Speed", "Animation speed", 0.1, 5.0, 1.0, 0.1);
    private final BooleanSetting fireParticles = new BooleanSetting("Fire Particles", "Enable fire particle trail", true);
    private final ColorSetting flameColor = new ColorSetting("Flame Color", "Color of wing flames", 0xFFFF6B35);
    private final NumberSetting flameDensity = new NumberSetting("Flame Density", "Density of flame particles", 0.1, 3.0, 1.5, 0.1);

    public PhoenixWings() {
        super("Phoenix Wings", "Blazing phoenix wings with fire particle trail", CosmeticCategory.WINGS);
        addSettings(span, animated, flapSpeed, fireParticles, flameColor, flameDensity);
    }

    @Override
    public void render(MatrixStack matrices, PlayerEntity player, float partialTicks) {
        if (!isEnabled()) return;
        matrices.push();
        matrices.translate(0, 0.1, -0.2);
        matrices.scale((float)(double)span.getValue(), (float)(double)span.getValue(), (float)(double)span.getValue());
        // render phoenix wing geometry with fire particles here
        matrices.pop();
    }
}
