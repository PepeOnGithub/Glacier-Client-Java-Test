package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.gui.DrawContext;

public class ScoreboardCustomizer extends HUDMod {

    private final BooleanSetting hideScoreboard = new BooleanSetting("Hide Scoreboard", "Completely hide scoreboard", false);
    private final BooleanSetting hideNumbers = new BooleanSetting("Hide Numbers", "Hide the red score numbers", false);
    private final NumberSetting posX = new NumberSetting("Position X", "Horizontal position %", 0, 100, 100);
    private final NumberSetting posY = new NumberSetting("Position Y", "Vertical position %", 0, 100, 50);
    private final NumberSetting backgroundAlpha = new NumberSetting("Background Alpha", "Scoreboard background transparency", 0, 255, 80);
    private final ColorSetting bgColor = new ColorSetting("BG Color", "Scoreboard background color", 0xFF2C2F33);

    public ScoreboardCustomizer() {
        super("Scoreboard Customizer", "Reposition, recolor and hide elements of the scoreboard", 200, 100);
        addSettings(hideScoreboard, hideNumbers, posX, posY, backgroundAlpha, bgColor);
    }

    public boolean isScoreboardHidden() {
        return hideScoreboard.getValue();
    }

    public boolean shouldHideNumbers() {
        return hideNumbers.getValue();
    }

    public int getBackgroundAlpha() {
        return (int)(double) backgroundAlpha.getValue();
    }

    public int getBgColor() {
        return bgColor.getValue();
    }

    public float getScoreboardPosX(int screenWidth) {
        return screenWidth * (float)(double) posX.getValue() / 100f;
    }

    public float getScoreboardPosY(int screenHeight) {
        return screenHeight * (float)(double) posY.getValue() / 100f;
    }

    @Override
    public void render(DrawContext context, float partialTicks) {
    }
}
