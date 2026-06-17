package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Unified Ping HUD element — replaces the old Ping Display + Ping Graph modules. {@code Mode} chooses
 * Counter, Graph or Both.
 */
public class Ping extends HUDMod {

    private final ModeSetting mode = new ModeSetting("Mode", "What to show", "Counter", "Counter", "Graph", "Both");
    private final BooleanSetting colored = new BooleanSetting("Colored", "Colour by latency", true);
    private final BooleanSetting showText = new BooleanSetting("Show Label", "Show the 'Ping:' label", true);
    private final ModeSetting graphStyle = new ModeSetting("Graph Style", "Graph rendering style", "Line", "Line", "Bar", "Fill");
    private final NumberSetting historySeconds = new NumberSetting("History (s)", "Graph history length", 5, 120, 30);

    private final List<Integer> history = new ArrayList<>();
    private int currentPing = 0;
    private long lastSample = 0L;

    public Ping() {
        super("Ping", "Server latency counter and history graph", 130, 46);
        addSettings(mode, colored, showText, graphStyle, historySeconds);
    }

    @Override
    public void onEnable() { history.clear(); }

    private int readPing() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.getNetworkHandler() == null) return -1;
        PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return e != null ? e.getLatency() : -1;
    }

    @Override
    public void onTick() {
        int p = readPing();
        if (p >= 0) currentPing = p;
        long now = System.currentTimeMillis();
        if (now - lastSample >= 1000) {
            lastSample = now;
            history.add(currentPing);
            int max = Math.max(2, (int) (double) historySeconds.getValue());
            while (history.size() > max) history.remove(0);
        }
    }

    private boolean showCounter() { return !"Graph".equals(mode.getValue()); }
    private boolean showGraph() { return !"Counter".equals(mode.getValue()); }

    private int pingColor(int ping) {
        if (ping < 80) return GlacierTheme.GREEN;
        if (ping < 150) return 0xFFFAA61A;
        return GlacierTheme.RED;
    }

    @Override
    public void render(DrawContext context, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null) return;
        int ping = readPing();
        if (ping < 0) ping = currentPing;
        int x = getX(), y = getY(), w = getWidth();

        if (showCounter()) {
            int color = colored.getValue() ? pingColor(ping) : getTextColor();
            String label = showText.getValue() ? "Ping: " + ping + "ms" : ping + "ms";
            drawBackground(context, x + 2, y + 4, mc.textRenderer.getWidth(label), 9);
            context.drawText(mc.textRenderer, label, x + 2, y + 4, color, hasShadow());
        }

        if (!showGraph()) return;
        int graphTop = showCounter() ? y + 15 : y + 2;
        int graphBottom = y + getHeight() - 2;
        int graphX = x + 2, graphW = w - 4, graphH = graphBottom - graphTop;
        if (graphH < 6 || history.size() < 2) return;
        context.fill(graphX, graphTop, graphX + graphW, graphBottom, 0xAA1A1A2E);

        Integer[] s = history.toArray(new Integer[0]);
        int maxPing = 200;
        for (int v : s) if (v > maxPing) maxPing = v;
        String style = graphStyle.getValue();
        int line = colored.getValue() ? GlacierTheme.ACCENT : getTextColor();

        if ("Bar".equals(style)) {
            int bw = Math.max(1, graphW / s.length);
            for (int i = 0; i < s.length; i++) {
                int bh = (int) (Math.min(1f, s[i] / (float) maxPing) * graphH);
                context.fill(graphX + i * bw, graphBottom - bh, graphX + (i + 1) * bw - 1, graphBottom, pingColor(s[i]));
            }
        } else {
            for (int i = 1; i < s.length; i++) {
                int x1 = graphX + (int) (((i - 1) / (float) (s.length - 1)) * graphW);
                int y1 = graphBottom - (int) (Math.min(1f, s[i - 1] / (float) maxPing) * graphH);
                int x2 = graphX + (int) ((i / (float) (s.length - 1)) * graphW);
                int y2 = graphBottom - (int) (Math.min(1f, s[i] / (float) maxPing) * graphH);
                context.fill(x1, Math.min(y1, y2), Math.max(x2, x1 + 1), Math.max(y1, y2) + 1, line);
                if ("Fill".equals(style)) context.fill(x1, Math.min(y1, y2), x2, graphBottom, (line & 0x00FFFFFF) | 0x44000000);
            }
        }
    }
}
