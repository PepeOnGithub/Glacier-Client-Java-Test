package net.glacierclient.cosmetics.capes;

import net.glacierclient.core.cosmetic.Cosmetic;
import net.glacierclient.core.cosmetic.CosmeticCategory;
import net.glacierclient.core.settings.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class WaveCape extends Cosmetic {
    private final NumberSetting waveSpeed = new NumberSetting("Wave Speed", "Speed of wave animation", 0.1, 5.0, 1.0, 0.1);
    private final NumberSetting waveHeight = new NumberSetting("Wave Height", "Height of wave pattern", 0.1, 3.0, 1.0, 0.1);
    private final ColorSetting waveColor = new ColorSetting("Wave Color", "Color of wave pattern", 0xFF7289DA);

    public WaveCape() {
        super("Wave Cape", "Cape with animated wave pattern", CosmeticCategory.CAPES);
        addSettings(waveSpeed, waveHeight, waveColor);
    }

    @Override
    public void render(MatrixStack matrices, PlayerEntity player, float partialTicks) {
        if (!isEnabled()) return;
        matrices.push();
        matrices.translate(0, 0, 0.2);
        // render cape with animated wave pattern here
        matrices.pop();
    }
}
