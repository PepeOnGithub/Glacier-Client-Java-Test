package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import java.util.ArrayDeque;
import java.util.Deque;

public class PingGraphOverlay extends HUDMod {

    private final NumberSetting historySeconds = new NumberSetting("History", "History duration in seconds", 10, 120, 30);
    private final ModeSetting style = new ModeSetting("Style", "Graph style", new String[]{"Line", "Bar", "Fill"}, "Line");
    private final ColorSetting lineColor = new ColorSetting("Line Color", "Graph line color", GlacierTheme.ACCENT);

    private final Deque<Integer> pingHistory = new ArrayDeque<>();
    private int currentPing = 0;
    private long lastSampleTime = System.currentTimeMillis();

    public PingGraphOverlay() {
        super("Ping Graph", "Live ping history graph", 200, 80);
        addSettings(historySeconds, style, lineColor);
    }

    @Override
    public void onEnable() {
        pingHistory.clear();
        lastSampleTime = System.currentTimeMillis();
    }

    @Override
    public void onDisable() {
        pingHistory.clear();
    }

    @Override
    public void onTick() {
        long now = System.currentTimeMillis();
        if (now - lastSampleTime >= 1000) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null && mc.getNetworkHandler() != null) {
                PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
                if (entry != null) {
                    currentPing = entry.getLatency();
                }
            }
            pingHistory.addLast(currentPing);
            int maxSamples = (int) historySeconds.getValue();
            while (pingHistory.size() > maxSamples) {
                pingHistory.pollFirst();
            }
            lastSampleTime = now;
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        context.fill(x, y, x + w, y + 1, lineColor.getValue());
        context.fill(x, y + h - 1, x + w, y + h, lineColor.getValue());
        context.fill(x, y, x + 1, y + h, lineColor.getValue());
        context.fill(x + w - 1, y, x + w, y + h, lineColor.getValue());

        context.drawText(MinecraftClient.getInstance().textRenderer,
            "Ping: " + currentPing + "ms", x + 4, y + 4, GlacierTheme.TEXT, true);

        if (pingHistory.isEmpty()) return;
        Integer[] samples = pingHistory.toArray(new Integer[0]);
        int maxPing = 500;
        int graphX = x + 2, graphY = y + 16, graphW = w - 4, graphH = h - 20;
        String styleVal = style.getValue();

        if ("Bar".equals(styleVal)) {
            int barWidth = Math.max(1, graphW / Math.max(1, samples.length));
            for (int i = 0; i < samples.length; i++) {
                int barH = (int) (Math.min(1.0f, samples[i] / (float) maxPing) * graphH);
                int bx = graphX + i * barWidth;
                int color = samples[i] < 80 ? 0xFF43B581 : samples[i] < 200 ? 0xFFFAA61A : 0xFFF04747;
                context.fill(bx, graphY + graphH - barH, bx + barWidth - 1, graphY + graphH, color);
            }
        } else {
            for (int i = 1; i < samples.length; i++) {
                int x1 = graphX + (int) (((i - 1) / (float) (samples.length - 1)) * graphW);
                int y1 = graphY + graphH - (int) (Math.min(1.0f, samples[i - 1] / (float) maxPing) * graphH);
                int x2 = graphX + (int) ((i / (float) (samples.length - 1)) * graphW);
                int y2 = graphY + graphH - (int) (Math.min(1.0f, samples[i] / (float) maxPing) * graphH);
                context.fill(x1, Math.min(y1, y2), x2, Math.max(y1, y2) + 1, lineColor.getValue());
                if ("Fill".equals(styleVal)) {
                    context.fill(x1, Math.min(y1, y2), x2, graphY + graphH, (lineColor.getValue() & 0x00FFFFFF) | 0x44000000);
                }
            }
        }
    }
}
