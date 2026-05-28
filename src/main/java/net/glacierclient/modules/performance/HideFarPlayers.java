package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class HideFarPlayers extends GlacierMod {

    private final NumberSetting hideDistance = new NumberSetting("Hide Distance", "Distance to hide players (blocks)", 16, 256, 128);
    private final BooleanSetting hideNametags = new BooleanSetting("Hide Nametags", "Hide player nametags at distance", true);
    private final BooleanSetting hideSounds = new BooleanSetting("Hide Sounds", "Mute sounds from far players", false);
    private final BooleanSetting fadeOut = new BooleanSetting("Fade Out", "Fade players at distance", true);

    public HideFarPlayers() {
        super("Hide Far Players", "Reduce rendering of distant players", Category.PERFORMANCE);
        addSettings(hideDistance, hideNametags, hideSounds, fadeOut);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean shouldHide(PlayerEntity player) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || player == mc.player) return false;
        return player.distanceTo(mc.player) > (float)(double) hideDistance.getValue();
    }

    public boolean isHideNametags() { return hideNametags.getValue(); }
    public boolean isHideSounds() { return hideSounds.getValue(); }
    public boolean isFadeOut() { return fadeOut.getValue(); }
}
