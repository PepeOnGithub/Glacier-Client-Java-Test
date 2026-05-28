package net.glacierclient.modules.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayDeque;
import java.util.Deque;

public class CPSDisplay extends HUDMod {

    private final NumberSetting maxCPS = new NumberSetting("Max CPS", "Maximum CPS display cap", 1, 50, 20);
    private final ModeSetting style = new ModeSetting("Style", "Display style", "Simple", "Simple", "Graph");
    private final ColorSetting textColor = new ColorSetting("Text Color", "Color of the text", GlacierTheme.ACCENT);

    private final Deque<Long> leftClicks = new ArrayDeque<>();
    private final Deque<Long> rightClicks = new ArrayDeque<>();
    private int leftCPS = 0;
    private int rightCPS = 0;

    public CPSDisplay() {
        super("CPS Display", "Shows clicks per second", 80, 20);
        addSettings(maxCPS, style, textColor);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        long now = System.currentTimeMillis();
        long cutoff = now - 1000L;
        leftClicks.removeIf(t -> t < cutoff);
        rightClicks.removeIf(t -> t < cutoff);
        leftCPS = leftClicks.size();
        rightCPS = rightClicks.size();
    }

    public void registerClick(boolean isLeft) {
        if (isLeft) leftClicks.addLast(System.currentTimeMillis());
        else rightClicks.addLast(System.currentTimeMillis());
    }

    @Override
    public void render(DrawContext context, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null) return;
        int color = textColor.getValue();
        String text;
        if ("Simple".equals(style.getValue())) {
            int total = Math.min(leftCPS + rightCPS, (int)(double) maxCPS.getValue());
            text = "CPS: " + total;
        } else {
            text = "L:" + leftCPS + " R:" + rightCPS;
        }
        context.drawText(mc.textRenderer, text, getX() + 2, getY() + 4, color, false);
    }
}
