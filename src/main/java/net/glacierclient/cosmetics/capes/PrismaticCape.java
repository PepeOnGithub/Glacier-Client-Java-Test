package net.glacierclient.cosmetics.capes;

import net.glacierclient.core.cosmetic.Cosmetic;
import net.glacierclient.core.cosmetic.CosmeticCategory;
import net.glacierclient.core.settings.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class PrismaticCape extends Cosmetic {
    private final NumberSetting shiftSpeed = new NumberSetting("Shift Speed", "Speed of color shifting", 0.1, 5.0, 1.5, 0.1);
    private final NumberSetting saturation = new NumberSetting("Saturation", "Color saturation", 0.5, 1.0, 0.8, 0.05);
    private final BooleanSetting gradient = new BooleanSetting("Gradient", "Enable gradient effect", true);

    public PrismaticCape() {
        super("Prismatic Cape", "Rainbow color-shifting prismatic cape", CosmeticCategory.CAPES);
        addSettings(shiftSpeed, saturation, gradient);
    }

    @Override
    public void render(MatrixStack matrices, PlayerEntity player, float partialTicks) {
        if (!isEnabled()) return;
        matrices.push();
        matrices.translate(0, 0, 0.2);
        // render rainbow color-shifting prismatic cape here
        matrices.pop();
    }
}
