package net.glacierclient.modules.expanded.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import java.util.ArrayDeque;
import java.util.Deque;

public class FPSFrameTimeGraph extends HUDMod {

    private final NumberSetting history = new NumberSetting("History", "Number of frames to keep in graph", 60, 10, 120);
    private final BooleanSetting showLows = new BooleanSetting("Show Lows", "Display 1%/0.1% low frame times", false);
    private final BooleanSetting showAverage = new BooleanSetting("Show Average", "Display average frame time", false);
    private final ColorSetting lineColor = new ColorSetting("Line Color", "Graph line color", GlacierTheme.ACCENT);
    private final ModeSetting style = new ModeSetting("Style", "Graph display style", "Line", "Line", "Bar");

    private final Deque<Float> frameTimes = new ArrayDeque<>();

    public FPSFrameTimeGraph() {
        super("FPS Frame Time", "Frame time analyzer with 1%/0.1% low tracking", 220, 80);
        addSettings(history, showLows, showAverage, lineColor, style);
    }

    @Override
    public void render(DrawContext context, float delta) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        MinecraftClient mc = MinecraftClient.getInstance();

        // Record frame time
        float ft = delta * 50f;
        if (frameTimes.size() >= (int) history.getValue()) frameTimes.pollFirst();
        frameTimes.addLast(ft);

        // Background
        context.fill(x, y, x + w, y + h, 0xCC1E1E2E);

        int fps = mc.getCurrentFps();
        context.drawTextWithShadow(mc.textRenderer, "FPS: " + fps, x + 4, y + 4, GlacierTheme.TEXT);

        if (showAverage.getValue() && !frameTimes.isEmpty()) {
            float avg = (float) frameTimes.stream().mapToDouble(Float::doubleValue).average().orElse(0);
            context.drawTextWithShadow(mc.textRenderer,
                String.format("Avg: %.1fms", avg), x + 4, y + 14, GlacierTheme.TEXT_DIM);
        }

        if (showLows.getValue() && frameTimes.size() > 1) {
            float[] sorted = frameTimes.stream().mapToInt(f -> (int)(f * 10)).sorted().asLongStream().mapToObj(v -> v / 10f).mapToDouble(Float::doubleValue).collect(() -> new float[frameTimes.size()], (arr, v) -> {}, (a, b) -> {});
            context.drawTextWithShadow(mc.textRenderer, "1% Low", x + 4, y + 24, 0xFFFF5555);
        }

        // Draw graph
        int graphX = x + 4;
        int graphY = y + 35;
        int graphW = w - 8;
        int graphH = h - 40;
        context.fill(graphX, graphY, graphX + graphW, graphY + graphH, 0x33FFFFFF);

        float[] times = frameTimes.stream().mapToDouble(Float::doubleValue).collect(() -> new float[frameTimes.size()], (arr, v) -> {}, (a, b) -> {});
        float maxFT = 50f;
        boolean isBar = "Bar".equals(style.getValue());
        int n = frameTimes.size();
        if (n > 1) {
            float step = (float) graphW / Math.max(n - 1, 1);
            int col = lineColor.getValue();
            Float[] arr = frameTimes.toArray(new Float[0]);
            for (int i = 0; i < arr.length; i++) {
                int px = graphX + (int)(i * step);
                int py = graphY + graphH - (int)(arr[i] / maxFT * graphH);
                if (isBar) {
                    context.fill(px, py, px + Math.max(1, (int)step - 1), graphY + graphH, col);
                } else if (i > 0) {
                    int prevX = graphX + (int)((i - 1) * step);
                    int prevY = graphY + graphH - (int)(arr[i - 1] / maxFT * graphH);
                    context.fill(prevX, Math.min(prevY, py), prevX + 1, Math.max(prevY, py) + 1, col);
                    context.fill(prevX, py, px, py + 1, col);
                }
            }
        }
    }
}
