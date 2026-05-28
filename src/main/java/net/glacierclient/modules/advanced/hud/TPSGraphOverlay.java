package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.gui.DrawContext;
import java.util.ArrayDeque;
import java.util.Deque;

public class TPSGraphOverlay extends HUDMod {

    private final NumberSetting historySeconds = new NumberSetting("History", "History duration in seconds", 10, 120, 30);
    private final ModeSetting style = new ModeSetting("Style", "Graph style", new String[]{"Line", "Bar", "Fill"}, "Line");
    private final ColorSetting lineColor = new ColorSetting("Line Color", "Graph line color", GlacierTheme.ACCENT);

    private final Deque<Float> tpsHistory = new ArrayDeque<>();
    private long lastTickTime = System.currentTimeMillis();
    private float currentTPS = 20.0f;
    private long lastSampleTime = System.currentTimeMillis();
    private int tickCount = 0;

    public TPSGraphOverlay() {
        super("TPS Graph", "Real-time server TPS display with history graph", 200, 80);
        addSettings(historySeconds, style, lineColor);
    }

    @Override
    public void onEnable() {
        tpsHistory.clear();
        lastTickTime = System.currentTimeMillis();
        lastSampleTime = System.currentTimeMillis();
        tickCount = 0;
    }

    @Override
    public void onDisable() {
        tpsHistory.clear();
    }

    @Override
    public void onTick() {
        long now = System.currentTimeMillis();
        tickCount++;
        long elapsed = now - lastSampleTime;
        if (elapsed >= 1000) {
            currentTPS = Math.min(20.0f, (tickCount * 1000.0f) / elapsed);
            tpsHistory.addLast(currentTPS);
            tickCount = 0;
            lastSampleTime = now;
            int maxSamples = (int) historySeconds.getValue();
            while (tpsHistory.size() > maxSamples) {
                tpsHistory.pollFirst();
            }
        }
        lastTickTime = now;
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();

        // Background
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        // Border
        context.fill(x, y, x + w, y + 1, lineColor.getValue());
        context.fill(x, y + h - 1, x + w, y + h, lineColor.getValue());
        context.fill(x, y, x + 1, y + h, lineColor.getValue());
        context.fill(x + w - 1, y, x + w, y + h, lineColor.getValue());

        // Title
        context.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer,
            "TPS: " + String.format("%.1f", currentTPS),
            x + 4, y + 4, GlacierTheme.TEXT, true);

        if (tpsHistory.isEmpty()) return;

        Float[] samples = tpsHistory.toArray(new Float[0]);
        int graphX = x + 2;
        int graphY = y + 16;
        int graphW = w - 4;
        int graphH = h - 20;
        String styleVal = style.getValue();

        if ("Bar".equals(styleVal)) {
            int barWidth = Math.max(1, graphW / Math.max(1, samples.length));
            for (int i = 0; i < samples.length; i++) {
                int barH = (int) ((samples[i] / 20.0f) * graphH);
                int bx = graphX + i * barWidth;
                int color = samples[i] >= 18 ? 0xFF43B581 : samples[i] >= 10 ? 0xFFFAA61A : 0xFFF04747;
                context.fill(bx, graphY + graphH - barH, bx + barWidth - 1, graphY + graphH, color);
            }
        } else {
            // Line or Fill
            for (int i = 1; i < samples.length; i++) {
                int x1 = graphX + (int) (((i - 1) / (float) (samples.length - 1)) * graphW);
                int y1 = graphY + graphH - (int) ((samples[i - 1] / 20.0f) * graphH);
                int x2 = graphX + (int) ((i / (float) (samples.length - 1)) * graphW);
                int y2 = graphY + graphH - (int) ((samples[i] / 20.0f) * graphH);
                context.fill(x1, Math.min(y1, y2), x2, Math.max(y1, y2) + 1, lineColor.getValue());
                if ("Fill".equals(styleVal)) {
                    int fillColor = (lineColor.getValue() & 0x00FFFFFF) | 0x44000000;
                    context.fill(x1, Math.min(y1, y2), x2, graphY + graphH, fillColor);
                }
            }
        }
    }
}
