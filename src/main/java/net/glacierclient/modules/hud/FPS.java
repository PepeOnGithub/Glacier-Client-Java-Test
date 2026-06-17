package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unified FPS HUD element. A single toggle that replaces the old FPS Display / FPS Graph / FPS Frame
 * Time modules; the {@code Mode} setting picks Counter, Graph or Both, and the remaining settings
 * tune each part individually.
 */
public class FPS extends HUDMod {

    private final ModeSetting mode = new ModeSetting("Mode", "What to show", "Counter", "Counter", "Graph", "Both");
    private final BooleanSetting colored = new BooleanSetting("Colored", "Colour the counter by FPS", true);
    private final BooleanSetting showMin = new BooleanSetting("Show Min", "Show minimum FPS on the counter", false);
    private final BooleanSetting showMax = new BooleanSetting("Show Max", "Show maximum FPS on the counter", false);
    private final NumberSetting history = new NumberSetting("History (s)", "Graph history length in seconds", 5, 60, 20);
    private final BooleanSetting showLows = new BooleanSetting("Show Lows", "Show 1% and 0.1% low FPS on the graph", true);
    private final ModeSetting graphStyle = new ModeSetting("Graph Style", "Graph rendering style", "Line", "Line", "Bar", "Fill");

    private int minFPS = Integer.MAX_VALUE;
    private int maxFPS = 0;
    private final List<Integer> samples = new ArrayList<>();
    private long lastSample = 0L;

    public FPS() {
        super("FPS", "Frames-per-second counter and history graph", 120, 46);
        addSettings(mode, colored, showMin, showMax, history, showLows, graphStyle);
    }

    @Override
    public void onEnable() {
        minFPS = Integer.MAX_VALUE;
        maxFPS = 0;
        samples.clear();
    }

    @Override
    public void onTick() {
        int fps = MinecraftClient.getInstance().getCurrentFps();
        if (fps < minFPS) minFPS = fps;
        if (fps > maxFPS) maxFPS = fps;
        long now = System.currentTimeMillis();
        if (now - lastSample >= 200) {
            lastSample = now;
            samples.add(fps);
            int max = Math.max(2, (int) (history.getValue() * 5)); // 5 samples/sec
            while (samples.size() > max) samples.remove(0);
        }
    }

    private boolean showCounter() { return !"Graph".equals(mode.getValue()); }
    private boolean showGraph() { return !"Counter".equals(mode.getValue()); }

    private int percentileLow(double pct) {
        if (samples.isEmpty()) return 0;
        List<Integer> sorted = new ArrayList<>(samples);
        Collections.sort(sorted);
        int count = Math.max(1, (int) Math.ceil(sorted.size() * pct));
        int sum = 0;
        for (int i = 0; i < count; i++) sum += sorted.get(i);
        return sum / count;
    }

    @Override
    public void render(DrawContext context, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null) return;
        int fps = mc.getCurrentFps();
        int x = getX(), y = getY(), w = getWidth();

        int textColor = getTextColor();
        if (colored.getValue()) {
            if (fps >= 60) textColor = GlacierTheme.GREEN;
            else if (fps >= 30) textColor = 0xFFFAA61A;
            else textColor = GlacierTheme.RED;
        }

        int textY = y + 4;
        if (showCounter()) {
            StringBuilder sb = new StringBuilder("FPS: ").append(fps);
            if (showMin.getValue() && minFPS != Integer.MAX_VALUE) sb.append(" Min:").append(minFPS);
            if (showMax.getValue()) sb.append(" Max:").append(maxFPS);
            String text = sb.toString();
            drawBackground(context, x + 2, textY, mc.textRenderer.getWidth(text), 9);
            context.drawText(mc.textRenderer, text, x + 2, textY, textColor, hasShadow());
        }

        if (!showGraph()) return;

        int graphTop = showCounter() ? y + 15 : y + 2;
        int graphX = x + 2, graphW = w - 4;
        int graphBottom = y + getHeight() - 2;
        int graphH = graphBottom - graphTop;
        if (graphH < 6 || samples.size() < 2) {
            if (showLows.getValue() && showGraph() && samples.size() >= 2) { /* nothing to draw yet */ }
            return;
        }
        context.fill(graphX, graphTop, graphX + graphW, graphBottom, 0xAA1A1A2E);

        Integer[] arr = samples.toArray(new Integer[0]);
        int peak = 60;
        for (int v : arr) if (v > peak) peak = v;
        int color = GlacierTheme.ACCENT;
        String style = graphStyle.getValue();

        if ("Bar".equals(style)) {
            int bw = Math.max(1, graphW / arr.length);
            for (int i = 0; i < arr.length; i++) {
                int bh = (int) ((arr[i] / (float) peak) * graphH);
                context.fill(graphX + i * bw, graphBottom - bh, graphX + (i + 1) * bw - 1, graphBottom, color);
            }
        } else {
            for (int i = 1; i < arr.length; i++) {
                int x1 = graphX + (int) (((i - 1) / (float) (arr.length - 1)) * graphW);
                int y1 = graphBottom - (int) ((arr[i - 1] / (float) peak) * graphH);
                int x2 = graphX + (int) ((i / (float) (arr.length - 1)) * graphW);
                int y2 = graphBottom - (int) ((arr[i] / (float) peak) * graphH);
                context.fill(x1, Math.min(y1, y2), Math.max(x2, x1 + 1), Math.max(y1, y2) + 1, color);
                if ("Fill".equals(style)) {
                    context.fill(x1, Math.min(y1, y2), x2, graphBottom, (color & 0x00FFFFFF) | 0x44000000);
                }
            }
        }

        if (showLows.getValue() && samples.size() >= 2) {
            String lows = "1%:" + percentileLow(0.01) + " 0.1%:" + percentileLow(0.001);
            context.drawText(mc.textRenderer, lows, graphX + 2, graphBottom - 9, GlacierTheme.TEXT_DIM, hasShadow());
        }
    }
}
