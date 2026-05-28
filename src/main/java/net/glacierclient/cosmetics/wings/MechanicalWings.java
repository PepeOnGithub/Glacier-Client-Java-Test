package net.glacierclient.cosmetics.wings;

import net.glacierclient.core.cosmetic.Cosmetic;
import net.glacierclient.core.cosmetic.CosmeticCategory;
import net.glacierclient.core.settings.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class MechanicalWings extends Cosmetic {
    private final NumberSetting span = new NumberSetting("Wing Span", "Wing width multiplier", 0.5, 3.0, 1.5, 0.1);
    private final BooleanSetting animated = new BooleanSetting("Animated", "Mechanical animation", true);
    private final NumberSetting flapSpeed = new NumberSetting("Flap Speed", "Animation speed", 0.1, 5.0, 1.0, 0.1);
    private final BooleanSetting gearAnimation = new BooleanSetting("Gear Animation", "Animate gear components", true);
    private final NumberSetting gearSpeed = new NumberSetting("Gear Speed", "Speed of gear rotation", 0.1, 5.0, 1.0, 0.1);
    private final ModeSetting material = new ModeSetting("Material", "Wing material style", "Brass", "Brass", "Iron", "Gold", "Chrome");

    public MechanicalWings() {
        super("Mechanical Wings", "Steampunk mechanical gear-driven wings", CosmeticCategory.WINGS);
        addSettings(span, animated, flapSpeed, gearAnimation, gearSpeed, material);
    }

    @Override
    public void render(MatrixStack matrices, PlayerEntity player, float partialTicks) {
        if (!isEnabled()) return;
        matrices.push();
        matrices.translate(0, 0.1, -0.2);
        matrices.scale((float)(double)span.getValue(), (float)(double)span.getValue(), (float)(double)span.getValue());
        // render mechanical wing geometry with gear animation here
        matrices.pop();
    }
}
