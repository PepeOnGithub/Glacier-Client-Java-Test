package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.gui.DrawContext;
import java.util.ArrayDeque;
import java.util.Deque;

public class CPSGraph extends HUDMod {

    private final NumberSetting historySeconds = new NumberSetting("History", "History in seconds", 10, 120, 30);
    private final ModeSetting style = new ModeSetting("Style", "Which button CPS to show", new String[]{"Left", "Right", "Both"}, "Both");

    private final Deque<Integer> leftCPSHistory = new ArrayDeque<>();
    private final Deque<Integer> rightCPSHistory = new ArrayDeque<>();
    private int leftClicks = 0, rightClicks = 0;
    private int currentLeftCPS = 0, currentRightCPS = 0;
    private long lastSampleTime = System.currentTimeMillis();

    public CPSGraph() {
        super("CPS Graph", "Clicks per second over time graph", 200, 60);
        addSettings(historySeconds, style);
    }

    @Override
    public void onEnable() {
        leftCPSHistory.clear();
        rightCPSHistory.clear();
        leftClicks = 0;
        rightClicks = 0;
    }

    @Override
    public void onDisable() {
        leftCPSHistory.clear();
        rightCPSHistory.clear();
    }

    public void registerLeftClick() { leftClicks++; }
    public void registerRightClick() { rightClicks++; }

    @Override
    public void onTick() {
        long now = System.currentTimeMillis();
        if (now - lastSampleTime >= 1000) {
            currentLeftCPS = leftClicks;
            currentRightCPS = rightClicks;
            leftCPSHistory.addLast(leftClicks);
            rightCPSHistory.addLast(rightClicks);
            leftClicks = 0;
            rightClicks = 0;
            int maxSamples = (int) historySeconds.getValue();
            while (leftCPSHistory.size() > maxSamples) leftCPSHistory.pollFirst();
            while (rightCPSHistory.size() > maxSamples) rightCPSHistory.pollFirst();
            lastSampleTime = now;
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        String styleVal = style.getValue();
        String label = "Both".equals(styleVal)
            ? "L: " + currentLeftCPS + " R: " + currentRightCPS
            : ("Left".equals(styleVal) ? "CPS: " + currentLeftCPS : "CPS: " + currentRightCPS);
        context.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer,
            label, x + 4, y + 4, GlacierTheme.TEXT, true);

        int graphY = y + 14, graphH = h - 16;
        if ("Left".equals(styleVal) || "Both".equals(styleVal)) {
            drawGraph(context, leftCPSHistory.toArray(new Integer[0]), x + 2, graphY, w - 4, graphH, 0xFF7289DA);
        }
        if ("Right".equals(styleVal) || "Both".equals(styleVal)) {
            drawGraph(context, rightCPSHistory.toArray(new Integer[0]), x + 2, graphY, w - 4, graphH, 0xFF43B581);
        }
    }

    private void drawGraph(DrawContext ctx, Integer[] samples, int gx, int gy, int gw, int gh, int color) {
        if (samples.length < 2) return;
        int maxCPS = 20;
        for (int v : samples) if (v > maxCPS) maxCPS = v;
        for (int i = 1; i < samples.length; i++) {
            int x1 = gx + (int) (((i - 1) / (float) (samples.length - 1)) * gw);
            int y1 = gy + gh - (int) ((samples[i - 1] / (float) maxCPS) * gh);
            int x2 = gx + (int) ((i / (float) (samples.length - 1)) * gw);
            int y2 = gy + gh - (int) ((samples[i] / (float) maxCPS) * gh);
            ctx.fill(x1, Math.min(y1, y2), x2, Math.max(y1, y2) + 1, color);
        }
    }
}
