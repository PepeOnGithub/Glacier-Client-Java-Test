package net.glacierclient.modules.expanded.pvp;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class KnockbackCalculator extends HUDMod {

    private final BooleanSetting showReceived = new BooleanSetting("Show Received", "Show KB force received from hits", false);
    private final BooleanSetting showGiven = new BooleanSetting("Show Given", "Show KB force applied to targets", false);
    private final BooleanSetting showDirection = new BooleanSetting("Show Direction", "Show KB direction vector", false);
    private final ColorSetting textColor = new ColorSetting("Text Color", "Color of knockback text display", GlacierTheme.TEXT_DIM);

    public KnockbackCalculator() {
        super("Knockback Calc", "Real-time KB magnitude received/given display", 160, 40);
        addSettings(showReceived, showGiven, showDirection, textColor);
    }

    @Override
    public void render(DrawContext context, float delta) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        MinecraftClient mc = MinecraftClient.getInstance();

        context.fill(x, y, x + w, y + h, 0xCC1E1E2E);

        int lineY = y + 4;
        int col = textColor.getValue();

        if (showReceived.getValue()) {
            context.drawTextWithShadow(mc.textRenderer, "Recv KB: 0.0", x + 4, lineY, col);
            lineY += 12;
        }
        if (showGiven.getValue()) {
            context.drawTextWithShadow(mc.textRenderer, "Given KB: 0.0", x + 4, lineY, col);
            lineY += 12;
        }
        if (showDirection.getValue()) {
            context.drawTextWithShadow(mc.textRenderer, "Dir: N", x + 4, lineY, col);
        }
    }
}
