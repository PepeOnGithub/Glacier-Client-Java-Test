package net.glacierclient.cosmetics.capes;

import net.glacierclient.core.cosmetic.Cosmetic;
import net.glacierclient.core.cosmetic.CosmeticCategory;
import net.glacierclient.core.settings.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class CustomImageCape extends Cosmetic {
    private final StringSetting imageUrl = new StringSetting("Image URL", "URL of the custom cape image", "");
    private final NumberSetting width = new NumberSetting("Width", "Cape texture width in pixels", 22, 64, 22, 1);
    private final NumberSetting height = new NumberSetting("Height", "Cape texture height in pixels", 17, 64, 17, 1);

    public CustomImageCape() {
        super("Custom Cape", "Upload a custom image as your cape", CosmeticCategory.CAPES);
        addSettings(imageUrl, width, height);
    }

    @Override
    public void render(MatrixStack matrices, PlayerEntity player, float partialTicks) {
        if (!isEnabled()) return;
        matrices.push();
        matrices.translate(0, 0, 0.2);
        // render custom image cape using imageUrl texture here
        matrices.pop();
    }
}
