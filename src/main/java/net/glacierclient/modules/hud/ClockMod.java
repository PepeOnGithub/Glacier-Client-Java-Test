package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockMod extends HUDMod {

    private final ModeSetting format = new ModeSetting("Format", "Time format", "24H", "12H", "24H");
    private final BooleanSetting showSeconds = new BooleanSetting("Show Seconds", "Show seconds", true);
    private final BooleanSetting showDate = new BooleanSetting("Show Date", "Show current date", false);

    public ClockMod() {
        super("Clock", "Shows real-world time", 80, 20);
        addSettings(format, showSeconds, showDate);
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
        if (mc.textRenderer == null) return;
        boolean is12h = "12H".equals(format.getValue());
        String timeFmt = is12h ? (showSeconds.getValue() ? "hh:mm:ss a" : "hh:mm a") : (showSeconds.getValue() ? "HH:mm:ss" : "HH:mm");
        String timeStr = new SimpleDateFormat(timeFmt).format(new Date());
        int y = getY() + 4;
        int w = Math.max(mc.textRenderer.getWidth(timeStr), showDate.getValue() ? 60 : 0);
        drawBackground(context, getX() + 2, y, w, showDate.getValue() ? 19 : 9);
        context.drawText(mc.textRenderer, timeStr, getX() + 2, y, getTextColor(), hasShadow());
        if (showDate.getValue()) {
            String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            context.drawText(mc.textRenderer, dateStr, getX() + 2, y + 10, GlacierTheme.TEXT_DIM, hasShadow());
        }
    }
}
