package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Unified CPS HUD element — replaces the old CPS Display + CPS Graph modules. {@code Mode} chooses
 * Counter, Graph or Both; clicks are fed in by {@code MixinMouse}.
 */
public class CPS extends HUDMod {

    private final ModeSetting mode = new ModeSetting("Mode", "What to show", "Counter", "Counter", "Graph", "Both");
    private final ModeSetting buttons = new ModeSetting("Buttons", "Which buttons to count", "Both", "Left", "Right", "Both");
    private final NumberSetting maxCPS = new NumberSetting("Max CPS", "Counter display cap", 1, 50, 20);
    private final ColorSetting textColor = new ColorSetting("Text Color", "Counter text colour", GlacierTheme.ACCENT);
    private final NumberSetting historySeconds = new NumberSetting("History (s)", "Graph history length", 5, 120, 30);

    private final Deque<Long> leftClicks = new ArrayDeque<>();
    private final Deque<Long> rightClicks = new ArrayDeque<>();
    private final List<Integer> leftHistory = new ArrayList<>();
    private final List<Integer> rightHistory = new ArrayList<>();
    private int leftCPS = 0, rightCPS = 0;
    private long lastSample = 0L;

    public CPS() {
        super("CPS", "Clicks-per-second counter and history graph", 130, 46);
        addSettings(mode, buttons, maxCPS, textColor, historySeconds);
    }

    @Override
    public void onEnable() {
        leftClicks.clear(); rightClicks.clear();
        leftHistory.clear(); rightHistory.clear();
    }

    /** Called from MixinMouse on a raw button press. */
    public void registerClick(boolean left) {
        (left ? leftClicks : rightClicks).addLast(System.currentTimeMillis());
    }

    @Override
    public void onTick() {
        long now = System.currentTimeMillis();
        long cutoff = now - 1000L;
        leftClicks.removeIf(t -> t < cutoff);
        rightClicks.removeIf(t -> t < cutoff);
        leftCPS = leftClicks.size();
        rightCPS = rightClicks.size();
        if (now - lastSample >= 1000) {
            lastSample = now;
            leftHistory.add(leftCPS);
            rightHistory.add(rightCPS);
            int max = Math.max(2, (int) (double) historySeconds.getValue());
            while (leftHistory.size() > max) leftHistory.remove(0);
            while (rightHistory.size() > max) rightHistory.remove(0);
        }
    }

    private boolean showCounter() { return !"Graph".equals(mode.getValue()); }
    private boolean showGraph() { return !"Counter".equals(mode.getValue()); }
    private boolean useLeft() { return !"Right".equals(buttons.getValue()); }
    private boolean useRight() { return !"Left".equals(buttons.getValue()); }

    @Override
    public void render(DrawContext context, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null) return;
        int x = getX(), y = getY(), w = getWidth();

        if (showCounter()) {
            String text;
            if ("Both".equals(buttons.getValue())) {
                text = "L:" + leftCPS + " R:" + rightCPS;
            } else {
                int v = Math.min(useLeft() ? leftCPS : rightCPS, (int) (double) maxCPS.getValue());
                text = "CPS: " + v;
            }
            drawBackground(context, x + 2, y + 4, mc.textRenderer.getWidth(text), 9);
            context.drawText(mc.textRenderer, text, x + 2, y + 4, textColor.getValue(), hasShadow());
        }

        if (!showGraph()) return;
        int graphTop = showCounter() ? y + 15 : y + 2;
        int graphBottom = y + getHeight() - 2;
        int graphX = x + 2, graphW = w - 4, graphH = graphBottom - graphTop;
        if (graphH < 6) return;
        context.fill(graphX, graphTop, graphX + graphW, graphBottom, 0xAA1A1A2E);
        if (useLeft()) drawGraph(context, leftHistory, graphX, graphTop, graphW, graphH, 0xFF7289DA);
        if (useRight()) drawGraph(context, rightHistory, graphX, graphTop, graphW, graphH, 0xFF43B581);
    }

    private void drawGraph(DrawContext ctx, List<Integer> samples, int gx, int gy, int gw, int gh, int color) {
        if (samples.size() < 2) return;
        int peak = 8;
        for (int v : samples) if (v > peak) peak = v;
        for (int i = 1; i < samples.size(); i++) {
            int x1 = gx + (int) (((i - 1) / (float) (samples.size() - 1)) * gw);
            int y1 = gy + gh - (int) ((samples.get(i - 1) / (float) peak) * gh);
            int x2 = gx + (int) ((i / (float) (samples.size() - 1)) * gw);
            int y2 = gy + gh - (int) ((samples.get(i) / (float) peak) * gh);
            ctx.fill(x1, Math.min(y1, y2), Math.max(x2, x1 + 1), Math.max(y1, y2) + 1, color);
        }
    }
}
