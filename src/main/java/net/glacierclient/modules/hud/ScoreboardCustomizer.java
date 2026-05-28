package net.glacierclient.modules.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ScoreboardCustomizer extends HUDMod {

    private final BooleanSetting hideScoreboard = new BooleanSetting("Hide Scoreboard", "Completely hide scoreboard", false);
    private final NumberSetting posX = new NumberSetting("Position X", "Horizontal position %", 0, 100, 100);
    private final NumberSetting posY = new NumberSetting("Position Y", "Vertical position %", 0, 100, 50);
    private final NumberSetting backgroundAlpha = new NumberSetting("Background Alpha", "Scoreboard background transparency", 0, 255, 80);

    public ScoreboardCustomizer() {
        super("Scoreboard Customizer", "Modifies scoreboard rendering", 200, 100);
        addSettings(hideScoreboard, posX, posY, backgroundAlpha);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isScoreboardHidden() {
        return hideScoreboard.getValue();
    }

    public int getBackgroundAlpha() {
        return (int)(double) backgroundAlpha.getValue();
    }

    public float getScoreboardPosX(int screenWidth) {
        return screenWidth * (float)(double) posX.getValue() / 100f;
    }

    public float getScoreboardPosY(int screenHeight) {
        return screenHeight * (float)(double) posY.getValue() / 100f;
    }

    @Override
    public void render(DrawContext context, float partialTicks) {
        // Scoreboard rendering is handled via mixin; this module has no direct HUD draw
    }
}
