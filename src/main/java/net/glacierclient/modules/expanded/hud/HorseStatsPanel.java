package net.glacierclient.modules.expanded.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.passive.AbstractHorseEntity;

public class HorseStatsPanel extends HUDMod {

    private final BooleanSetting showSpeed = new BooleanSetting("Show Speed", "Display horse speed bar", false);
    private final BooleanSetting showJump = new BooleanSetting("Show Jump", "Display horse jump strength bar", false);
    private final BooleanSetting showHealth = new BooleanSetting("Show Health", "Display horse health bar", false);
    private final BooleanSetting showOnlyWhenRiding = new BooleanSetting("Only When Riding", "Only show when player is mounted", true);

    public HorseStatsPanel() {
        super("Horse Stats", "Speed/jump/health bars for ridden horse", 160, 60);
        addSettings(showSpeed, showJump, showHealth, showOnlyWhenRiding);
    }

    @Override
    public void render(DrawContext context, float delta) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        MinecraftClient mc = MinecraftClient.getInstance();

        if (showOnlyWhenRiding.getValue() &&
            !(mc.player != null && mc.player.getVehicle() instanceof AbstractHorseEntity)) return;

        context.fill(x, y, x + w, y + h, 0xCC1E1E2E);
        context.drawTextWithShadow(mc.textRenderer, "Horse Stats", x + 4, y + 4, GlacierTheme.ACCENT);

        int lineY = y + 16;
        int barW = w - 8;

        if (showSpeed.getValue()) {
            context.drawTextWithShadow(mc.textRenderer, "Speed", x + 4, lineY, GlacierTheme.TEXT);
            context.fill(x + 40, lineY, x + 40 + barW - 36, lineY + 6, 0x44FFFFFF);
            context.fill(x + 40, lineY, x + 40 + (int)((barW - 36) * 0.7f), lineY + 6, 0xFF55FF55);
            lineY += 12;
        }
        if (showJump.getValue()) {
            context.drawTextWithShadow(mc.textRenderer, "Jump", x + 4, lineY, GlacierTheme.TEXT);
            context.fill(x + 40, lineY, x + 40 + barW - 36, lineY + 6, 0x44FFFFFF);
            context.fill(x + 40, lineY, x + 40 + (int)((barW - 36) * 0.5f), lineY + 6, GlacierTheme.ACCENT);
            lineY += 12;
        }
        if (showHealth.getValue()) {
            context.drawTextWithShadow(mc.textRenderer, "HP", x + 4, lineY, GlacierTheme.TEXT);
            context.fill(x + 40, lineY, x + 40 + barW - 36, lineY + 6, 0x44FFFFFF);
            context.fill(x + 40, lineY, x + 40 + (int)((barW - 36) * 0.9f), lineY + 6, 0xFFFF5555);
        }
    }
}
