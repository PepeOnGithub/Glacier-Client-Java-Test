package net.glacierclient.cosmetics.capes;

import net.glacierclient.core.cosmetic.Cosmetic;
import net.glacierclient.core.cosmetic.CosmeticCategory;
import net.glacierclient.core.settings.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class BannerCape extends Cosmetic {
    private final ModeSetting pattern = new ModeSetting("Pattern", "Banner pattern style", "Gradient", "Gradient", "Stripe", "Cross", "Custom");
    private final ColorSetting secondaryColor = new ColorSetting("Secondary Color", "Secondary banner color", 0xFF99AAB5);

    public BannerCape() {
        super("Banner Cape", "Minecraft banner-pattern cape", CosmeticCategory.CAPES);
        addSettings(pattern, secondaryColor);
    }

    @Override
    public void render(MatrixStack matrices, PlayerEntity player, float partialTicks) {
        if (!isEnabled()) return;
        matrices.push();
        matrices.translate(0, 0, 0.2);
        // render banner pattern cape here
        matrices.pop();
    }
}
