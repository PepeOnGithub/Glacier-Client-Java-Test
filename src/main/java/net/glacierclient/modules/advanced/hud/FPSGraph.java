package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class FPSGraph extends HUDMod {

    private final NumberSetting historySeconds = new NumberSetting("History", "History in seconds", 10, 120, 30);
    private final BooleanSetting showLows = new BooleanSetting("Show Lows", "Show 1% and 0.1% low FPS", true);
    private final ModeSetting style = new ModeSetting("Style", "Graph style", new String[]{"Line", "Bar", "Fill"}, "Line");

    private final Deque<Integer> fpsHistory = new ArrayDeque<>();
    private int currentFPS = 0;
    private long lastSampleTime = System.currentTimeMillis();

    public FPSGraph() {
        super("FPS Graph", "FPS history graph with 1% low and 0.1% low tracking", 200, 60);
        addSettings(historySeconds, showLows, style);
    }

    @Override
    public void onEnable() {
        fpsHistory.clear();
    }

    @Override
    public void onDisable() {
        fpsHistory.clear();
    }

    @Override
    public void onTick() {
        long now = System.currentTimeMillis();
        if (now - lastSampleTime >= 200) {
            currentFPS = MinecraftClient.getInstance().getCurrentFps();
            fpsHistory.addLast(currentFPS);
            int maxSamples = (int) (historySeconds.getValue() * 5);
            while (fpsHistory.size() > maxSamples) fpsHistory.pollFirst();
            lastSampleTime = now;
        }
    }

    private int calculatePercentileLow(Integer[] samples, double pct) {
        if (samples.length == 0) return 0;
        List<Integer> sorted = new ArrayList<>();
        Collections.addAll(sorted, samples);
        Collections.sort(sorted);
        int count = Math.max(1, (int) Math.ceil(sorted.size() * pct));
        int sum = 0;
        for (int i = 0; i < count; i++) sum += sorted.get(i);
        return sum / count;
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        Integer[] samples = fpsHistory.toArray(new Integer[0]);
        String label = "FPS: " + currentFPS;
        if (showLows.getValue() && samples.length > 0) {
            int low1 = calculatePercentileLow(samples, 0.01);
            int low01 = calculatePercentileLow(samples, 0.001);
            label += " 1%: " + low1 + " 0.1%: " + low01;
        }
        context.drawText(MinecraftClient.getInstance().textRenderer, label, x + 4, y + 4, GlacierTheme.TEXT, true);

        if (samples.length < 2) return;
        int maxFPS = 60;
        for (int v : samples) if (v > maxFPS) maxFPS = v;
        int graphX = x + 2, graphY = y + 14, graphW = w - 4, graphH = h - 16;
        String styleVal = style.getValue();
        int color = GlacierTheme.ACCENT;

        if ("Bar".equals(styleVal)) {
            int bw = Math.max(1, graphW / Math.max(1, samples.length));
            for (int i = 0; i < samples.length; i++) {
                int bh = (int) ((samples[i] / (float) maxFPS) * graphH);
                context.fill(graphX + i * bw, graphY + graphH - bh, graphX + (i + 1) * bw - 1, graphY + graphH, color);
            }
        } else {
            for (int i = 1; i < samples.length; i++) {
                int x1 = graphX + (int) (((i - 1) / (float) (samples.length - 1)) * graphW);
                int y1 = graphY + graphH - (int) ((samples[i - 1] / (float) maxFPS) * graphH);
                int x2 = graphX + (int) ((i / (float) (samples.length - 1)) * graphW);
                int y2 = graphY + graphH - (int) ((samples[i] / (float) maxFPS) * graphH);
                context.fill(x1, Math.min(y1, y2), x2, Math.max(y1, y2) + 1, color);
                if ("Fill".equals(styleVal)) {
                    context.fill(x1, Math.min(y1, y2), x2, graphY + graphH, (color & 0x00FFFFFF) | 0x44000000);
                }
            }
        }
    }
}
