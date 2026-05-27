package net.glacierclient.cosmetics.capes;

import net.glacierclient.core.cosmetic.Cosmetic;
import net.glacierclient.core.cosmetic.CosmeticCategory;
import net.glacierclient.core.settings.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class PixelCape extends Cosmetic {
    private final ModeSetting pattern = new ModeSetting("Pattern", "Pixel art pattern", "Creeper", "Creeper", "Sword", "Diamond", "Custom");
    private final NumberSetting pixelSize = new NumberSetting("Pixel Size", "Size of each pixel block", 1, 4, 2, 1);

    public PixelCape() {
        super("Pixel Cape", "Retro pixel art style cape", CosmeticCategory.CAPES);
        addSettings(pattern, pixelSize);
    }

    @Override
    public void render(MatrixStack matrices, PlayerEntity player, float partialTicks) {
        if (!isEnabled()) return;
        matrices.push();
        matrices.translate(0, 0, 0.2);
        // render pixel art cape pattern here
        matrices.pop();
    }
}
