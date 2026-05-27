package net.glacierclient.cosmetics.wings;

import net.glacierclient.core.cosmetic.Cosmetic;
import net.glacierclient.core.cosmetic.CosmeticCategory;
import net.glacierclient.core.settings.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class CrystallineWings extends Cosmetic {
    private final NumberSetting span = new NumberSetting("Wing Span", "Wing width multiplier", 0.5, 3.0, 1.5, 0.1);
    private final BooleanSetting animated = new BooleanSetting("Animated", "Flap animation", true);
    private final NumberSetting flapSpeed = new NumberSetting("Flap Speed", "Animation speed", 0.1, 5.0, 1.0, 0.1);
    private final BooleanSetting refraction = new BooleanSetting("Refraction", "Enable light refraction effect", true);
    private final NumberSetting crystalSize = new NumberSetting("Crystal Size", "Size of crystal structures", 0.5, 2.0, 1.0, 0.1);
    private final ColorSetting crystalTint = new ColorSetting("Crystal Tint", "Tint color of crystals", 0xAA7289DA);

    public CrystallineWings() {
        super("Crystalline Wings", "Insect-style crystalline transparent wings", CosmeticCategory.WINGS);
        addSettings(span, animated, flapSpeed, refraction, crystalSize, crystalTint);
    }

    @Override
    public void render(MatrixStack matrices, PlayerEntity player, float partialTicks) {
        if (!isEnabled()) return;
        matrices.push();
        matrices.translate(0, 0.1, -0.2);
        matrices.scale((float)(double)span.getValue(), (float)(double)span.getValue(), (float)(double)span.getValue());
        // render crystalline wing geometry with refraction here
        matrices.pop();
    }
}
