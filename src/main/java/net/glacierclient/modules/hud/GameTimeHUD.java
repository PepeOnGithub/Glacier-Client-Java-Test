package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class GameTimeHUD extends HUDMod {

    private final ModeSetting format = new ModeSetting("Format", "Display format", "Both", "Ticks", "Time", "Both");
    private final BooleanSetting showDay = new BooleanSetting("Show Day", "Show current game day", true);

    public GameTimeHUD() {
        super("Game Time", "Shows in-game world time and day", 100, 20);
        addSettings(format, showDay);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    @Override
    public void render(DrawContext context, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null || mc.world == null) return;
        long ticks = mc.world.getTimeOfDay();
        long dayTick = ticks % 24000;
        long day = ticks / 24000;
        int h = (int)((dayTick / 1000 + 6) % 24);
        int m = (int)((dayTick % 1000) * 60 / 1000);
        StringBuilder sb = new StringBuilder();
        if (showDay.getValue()) sb.append("Day ").append(day).append(" ");
        String fmt = format.getValue();
        if ("Ticks".equals(fmt)) sb.append(dayTick).append("t");
        else if ("Time".equals(fmt)) sb.append(String.format("%02d:%02d", h, m));
        else sb.append(dayTick).append("t ").append(String.format("%02d:%02d", h, m));
        String text = sb.toString();
        drawBackground(context, getX() + 2, getY() + 4, mc.textRenderer.getWidth(text), 9);
        context.drawText(mc.textRenderer, text, getX() + 2, getY() + 4, getTextColor(), hasShadow());
    }
}
