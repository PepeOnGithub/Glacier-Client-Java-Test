package net.glacierclient.cosmetics.capes;

import net.glacierclient.core.cosmetic.Cosmetic;
import net.glacierclient.core.cosmetic.CosmeticCategory;
import net.glacierclient.core.settings.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class GlacierAnimatedCape extends Cosmetic {
    private final BooleanSetting iceParticles = new BooleanSetting("Ice Particles", "Enable ice crystal particles", true);
    private final NumberSetting animSpeed = new NumberSetting("Anim Speed", "Speed of cape animation", 0.1, 3.0, 1.0, 0.1);

    public GlacierAnimatedCape() {
        super("Glacier Cape", "Official Glacier Client animated cape with ice crystal pattern", CosmeticCategory.CAPES);
        addSettings(iceParticles, animSpeed);
    }

    @Override
    public void render(MatrixStack matrices, PlayerEntity player, float partialTicks) {
        if (!isEnabled()) return;
        matrices.push();
        matrices.translate(0, 0, 0.2);
        // render Glacier animated cape with ice crystal pattern here
        matrices.pop();
    }
}
