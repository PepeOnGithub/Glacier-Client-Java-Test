package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class CombatLogTimer extends HUDMod {

    private final NumberSetting flagDuration = new NumberSetting("Flag Duration", "Combat flag duration in seconds", 5, 60, 15);
    private final BooleanSetting flashWarning = new BooleanSetting("Flash Warning", "Flash when combat flagged", true);
    private final ColorSetting activeColor = new ColorSetting("Active Color", "Color when combat flagged", 0xFFF04747);

    private boolean combatFlagged = false;
    private long flaggedAt = 0;
    private long tickCount = 0;

    public CombatLogTimer() {
        super("Combat Log Timer", "PvP logging timer for combat-flagged servers", 120, 30);
        addSettings(flagDuration, flashWarning, activeColor);
    }

    @Override public void onEnable() { combatFlagged = false; }
    @Override public void onDisable() { combatFlagged = false; }

    @Override
    public void onTick() {
        tickCount++;
        if (combatFlagged) {
            long elapsed = System.currentTimeMillis() - flaggedAt;
            if (elapsed >= flagDuration.getValue() * 1000) {
                combatFlagged = false;
            }
        }
    }

    public void flagCombat() {
        combatFlagged = true;
        flaggedAt = System.currentTimeMillis();
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        var tr = MinecraftClient.getInstance().textRenderer;
        if (!combatFlagged) {
            context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
            context.drawText(tr, "Combat: Safe", x + 4, y + 10, 0xFF43B581, true);
            return;
        }
        long remaining = (long) (flagDuration.getValue() * 1000) - (System.currentTimeMillis() - flaggedAt);
        boolean flash = flashWarning.getValue() && (tickCount % 20 < 10);
        int bg = flash ? 0xCC3A0000 : 0xAA1A1A2E;
        context.fill(x, y, x + w, y + h, bg);
        int color = flash ? 0xFFFF5555 : activeColor.getValue();
        context.drawText(tr, "COMBAT: " + String.format("%.1fs", remaining / 1000.0), x + 4, y + 10, color, true);
    }
}
