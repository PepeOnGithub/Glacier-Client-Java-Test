package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class StatTracker extends HUDMod {

    private final BooleanSetting showKD = new BooleanSetting("Show K/D", "Show kill/death ratio", true);
    private final BooleanSetting showWinrate = new BooleanSetting("Show Winrate", "Show win rate", true);
    private final BooleanSetting autoReset = new BooleanSetting("Auto Reset", "Reset stats on server change", false);
    private final ModeSetting display = new ModeSetting("Display", "Display mode", new String[]{"Compact", "Full"}, "Full");

    private int kills = 0;
    private int deaths = 0;
    private int wins = 0;
    private int losses = 0;
    private int streak = 0;

    public StatTracker() {
        super("Stat Tracker", "Session kills, deaths, win tracking", 160, 80);
        addSettings(showKD, showWinrate, autoReset, display);
    }

    @Override
    public void onEnable() {
        if (autoReset.getValue()) resetStats();
    }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public void registerKill() { kills++; streak++; }
    public void registerDeath() { deaths++; streak = 0; }
    public void registerWin() { wins++; }
    public void registerLoss() { losses++; }
    public void resetStats() { kills = 0; deaths = 0; wins = 0; losses = 0; streak = 0; }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        var tr = MinecraftClient.getInstance().textRenderer;
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        context.drawText(tr, "Stat Tracker", x + 4, y + 4, GlacierTheme.ACCENT, true);

        boolean compact = "Compact".equals(display.getValue());
        if (compact) {
            String line = "K:" + kills + " D:" + deaths;
            if (showKD.getValue() && deaths > 0) {
                line += " KD:" + String.format("%.2f", kills / (float) deaths);
            }
            context.drawText(tr, line, x + 4, y + 16, GlacierTheme.TEXT, true);
            if (showWinrate.getValue()) {
                int total = wins + losses;
                String wr = "W:" + wins + " L:" + losses + (total > 0 ? " WR:" + String.format("%.0f%%", wins * 100f / total) : "");
                context.drawText(tr, wr, x + 4, y + 26, GlacierTheme.TEXT_DIM, true);
            }
        } else {
            context.drawText(tr, "Kills: " + kills + "  Deaths: " + deaths, x + 4, y + 16, GlacierTheme.TEXT, true);
            if (showKD.getValue()) {
                float kd = deaths > 0 ? kills / (float) deaths : kills;
                context.drawText(tr, "K/D: " + String.format("%.2f", kd), x + 4, y + 26, GlacierTheme.TEXT, true);
            }
            if (showWinrate.getValue()) {
                int total = wins + losses;
                context.drawText(tr, "Wins: " + wins + "  Losses: " + losses, x + 4, y + 36, GlacierTheme.TEXT, true);
                if (total > 0) {
                    context.drawText(tr, "Winrate: " + String.format("%.1f%%", wins * 100f / total), x + 4, y + 46, GlacierTheme.TEXT_DIM, true);
                }
            }
            context.drawText(tr, "Streak: " + streak, x + 4, y + 56, streak > 5 ? 0xFFFFD700 : GlacierTheme.TEXT_DIM, true);
        }
    }
}
