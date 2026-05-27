package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;

public class ScoreboardPositioner extends GlacierMod {

    private final NumberSetting x = new NumberSetting("X Position", "Scoreboard X% of screen", 0, 100, 100);
    private final NumberSetting y = new NumberSetting("Y Position", "Scoreboard Y% of screen", 0, 100, 50);
    private final BooleanSetting hideNumbers = new BooleanSetting("Hide Numbers", "Hide score numbers", false);
    private final BooleanSetting customBackground = new BooleanSetting("Custom BG", "Use custom background", true);
    private final ColorSetting bgColor = new ColorSetting("BG Color", "Background color", 0xFF2C2F33);

    public ScoreboardPositioner() {
        super("Scoreboard Positioner", "Reposition the vanilla scoreboard", Category.RENDER);
        addSettings(x, y, hideNumbers, customBackground, bgColor);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
    @Override public void onTick() {}

    public float getXPercent() { return (float) x.getValue() / 100f; }
    public float getYPercent() { return (float) y.getValue() / 100f; }
    public boolean shouldHideNumbers() { return hideNumbers.getValue(); }
    public int getBgColor() { return bgColor.getValue(); }
    public boolean hasCustomBackground() { return customBackground.getValue(); }
}
