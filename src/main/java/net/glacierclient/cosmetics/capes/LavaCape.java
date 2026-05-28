package net.glacierclient.cosmetics.capes;

import net.glacierclient.core.cosmetic.Cosmetic;
import net.glacierclient.core.cosmetic.CosmeticCategory;
import net.glacierclient.core.settings.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class LavaCape extends Cosmetic {
    private final BooleanSetting animateLava = new BooleanSetting("Animate Lava", "Enable lava flow animation", true);
    private final NumberSetting lavaSpeed = new NumberSetting("Lava Speed", "Speed of lava flow", 0.1, 5.0, 1.5, 0.1);
    private final ColorSetting lavaColor = new ColorSetting("Lava Color", "Color of the lava", 0xFFFF4500);

    public LavaCape() {
        super("Lava Cape", "Animated lava flow cape", CosmeticCategory.CAPES);
        addSettings(animateLava, lavaSpeed, lavaColor);
    }

    @Override
    public void render(MatrixStack matrices, PlayerEntity player, float partialTicks) {
        if (!isEnabled()) return;
        matrices.push();
        matrices.translate(0, 0, 0.2);
        // render animated lava flow cape here
        matrices.pop();
    }
}
