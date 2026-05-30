package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class FPSDisplay extends HUDMod {

    private final ModeSetting style = new ModeSetting("Style", "Display style", "Simple", "Simple", "Colored", "Graph");
    private final BooleanSetting showMin = new BooleanSetting("Show Min", "Show minimum FPS", false);
    private final BooleanSetting showMax = new BooleanSetting("Show Max", "Show maximum FPS", false);

    private int minFPS = Integer.MAX_VALUE;
    private int maxFPS = 0;

    public FPSDisplay() {
        super("FPS Display", "Shows current frames per second", 80, 20);
        addSettings(style, showMin, showMax);
    }

    @Override
    public void onEnable() {
        minFPS = Integer.MAX_VALUE;
        maxFPS = 0;
    }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        int fps = mc.getCurrentFps();
        if (fps < minFPS) minFPS = fps;
        if (fps > maxFPS) maxFPS = fps;
    }

    @Override
    public void render(DrawContext context, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null) return;
        int fps = mc.getCurrentFps();
        int color;
        if ("Colored".equals(style.getValue())) {
            if (fps >= 60) color = GlacierTheme.GREEN;
            else if (fps >= 30) color = 0xFFFAA61A;
            else color = GlacierTheme.RED;
        } else {
            color = getTextColor();
        }
        StringBuilder sb = new StringBuilder("FPS: ").append(fps);
        if (showMin.getValue() && minFPS != Integer.MAX_VALUE) sb.append(" Min:").append(minFPS);
        if (showMax.getValue()) sb.append(" Max:").append(maxFPS);
        String text = sb.toString();
        drawBackground(context, getX() + 2, getY() + 4, mc.textRenderer.getWidth(text), 9);
        context.drawText(mc.textRenderer, text, getX() + 2, getY() + 4, color, hasShadow());
    }
}
