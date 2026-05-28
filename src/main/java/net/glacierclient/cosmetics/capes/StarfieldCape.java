package net.glacierclient.cosmetics.capes;

import net.glacierclient.core.cosmetic.Cosmetic;
import net.glacierclient.core.cosmetic.CosmeticCategory;
import net.glacierclient.core.settings.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class StarfieldCape extends Cosmetic {
    private final NumberSetting starCount = new NumberSetting("Star Count", "Number of stars", 100, 1000, 400, 10);
    private final NumberSetting starSpeed = new NumberSetting("Star Speed", "Speed of star movement", 0.1, 5.0, 1.0, 0.1);
    private final BooleanSetting coloredStars = new BooleanSetting("Colored Stars", "Enable colored star rendering", true);

    public StarfieldCape() {
        super("Starfield Cape", "Animated starfield moving through space", CosmeticCategory.CAPES);
        addSettings(starCount, starSpeed, coloredStars);
    }

    @Override
    public void render(MatrixStack matrices, PlayerEntity player, float partialTicks) {
        if (!isEnabled()) return;
        matrices.push();
        matrices.translate(0, 0, 0.2);
        // render animated starfield cape here
        matrices.pop();
    }
}
