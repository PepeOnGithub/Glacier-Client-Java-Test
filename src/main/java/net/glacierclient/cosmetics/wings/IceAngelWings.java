package net.glacierclient.cosmetics.wings;

import net.glacierclient.core.cosmetic.Cosmetic;
import net.glacierclient.core.cosmetic.CosmeticCategory;
import net.glacierclient.core.settings.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class IceAngelWings extends Cosmetic {
    private final NumberSetting span = new NumberSetting("Wing Span", "Wing width multiplier", 0.5, 2.5, 1.2, 0.1);
    private final BooleanSetting animated = new BooleanSetting("Animated", "Flap animation", true);
    private final NumberSetting flapSpeed = new NumberSetting("Flap Speed", "Animation speed", 0.1, 5.0, 1.0, 0.1);
    private final ModeSetting style = new ModeSetting("Style", "Wing style variant", "White", "White", "Blue", "Crystal", "Gold");

    public IceAngelWings() {
        super("Ice Angel Wings", "Elegant frost-white feathered angel wings", CosmeticCategory.WINGS);
        addSettings(span, animated, flapSpeed, style);
    }

    @Override
    public void render(MatrixStack matrices, PlayerEntity player, float partialTicks) {
        if (!isEnabled()) return;
        matrices.push();
        matrices.translate(0, 0.1, -0.2);
        matrices.scale((float)(double)span.getValue(), (float)(double)span.getValue(), (float)(double)span.getValue());
        // render ice angel wing geometry here
        matrices.pop();
    }
}
