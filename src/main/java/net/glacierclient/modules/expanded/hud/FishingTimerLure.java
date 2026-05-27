package net.glacierclient.modules.expanded.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class FishingTimerLure extends HUDMod {

    private final BooleanSetting showRing = new BooleanSetting("Show Ring", "Display progress ring for bite window", false);
    private final ColorSetting ringColor = new ColorSetting("Ring Color", "Color of the bite progress ring", GlacierTheme.ACCENT);
    private final BooleanSetting soundAlert = new BooleanSetting("Sound Alert", "Play sound when bite window is predicted", false);
    private final BooleanSetting autoRecast = new BooleanSetting("Auto Recast", "Automatically recast rod after catching", false);

    public FishingTimerLure() {
        super("Fishing Timer", "Fishing bite window predictor with progress ring", 160, 40);
        addSettings(showRing, ringColor, soundAlert, autoRecast);
    }

    @Override
    public void render(DrawContext context, float delta) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        MinecraftClient mc = MinecraftClient.getInstance();

        context.fill(x, y, x + w, y + h, 0xCC1E1E2E);
        context.drawTextWithShadow(mc.textRenderer, "Fishing Timer", x + 4, y + 4, GlacierTheme.ACCENT);

        // Progress bar placeholder
        if (showRing.getValue()) {
            int barY = y + 18;
            context.fill(x + 4, barY, x + w - 4, barY + 6, 0x44FFFFFF);
            context.fill(x + 4, barY, x + 4 + (int)((w - 8) * 0.6f), barY + 6, ringColor.getValue());
        }

        context.drawTextWithShadow(mc.textRenderer, "Waiting...", x + 4, y + 28, GlacierTheme.TEXT_DIM);
    }
}
