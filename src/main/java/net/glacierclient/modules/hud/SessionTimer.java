package net.glacierclient.modules.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class SessionTimer extends HUDMod {

    private final ModeSetting format = new ModeSetting("Format", "Time format", "HH:MM:SS", "HH:MM:SS", "MM:SS");
    private final BooleanSetting pauseOnMenu = new BooleanSetting("Pause On Menu", "Pause timer when in GUI", false);

    private long startTime;
    private long pauseAccum;
    private long pauseStart;
    private boolean paused = false;

    public SessionTimer() {
        super("Session Timer", "Time elapsed since session start", 100, 20);
        addSettings(format, pauseOnMenu);
    }

    @Override
    public void onEnable() {
        startTime = System.currentTimeMillis();
        pauseAccum = 0;
        paused = false;
    }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        boolean inMenu = mc.currentScreen != null;
        if (pauseOnMenu.getValue()) {
            if (inMenu && !paused) {
                paused = true;
                pauseStart = System.currentTimeMillis();
            } else if (!inMenu && paused) {
                paused = false;
                pauseAccum += System.currentTimeMillis() - pauseStart;
            }
        }
    }

    @Override
    public void render(DrawContext context, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null) return;
        long now = System.currentTimeMillis();
        long elapsed = now - startTime - pauseAccum;
        if (paused) elapsed -= (now - pauseStart);
        elapsed = Math.max(0, elapsed);
        long secs = elapsed / 1000;
        long h = secs / 3600, m = (secs % 3600) / 60, s = secs % 60;
        String time;
        if ("MM:SS".equals(format.getValue())) time = String.format("%02d:%02d", m + h * 60, s);
        else time = String.format("%02d:%02d:%02d", h, m, s);
        context.drawText(mc.textRenderer, "Time: " + time, getX() + 2, getY() + 4, GlacierTheme.TEXT, false);
    }
}
