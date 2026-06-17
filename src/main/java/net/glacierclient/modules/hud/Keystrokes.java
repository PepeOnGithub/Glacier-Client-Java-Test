package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.glacierclient.core.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

public class Keystrokes extends HUDMod {

    private final BooleanSetting showMouseButtons = new BooleanSetting("Mouse Buttons", "Show LMB/RMB", true);
    private final BooleanSetting showCps = new BooleanSetting("Show CPS", "Show clicks-per-second on the mouse keys", true);
    private final BooleanSetting showSpacebar = new BooleanSetting("Spacebar", "Show spacebar key", true);
    private final NumberSetting keySize = new NumberSetting("Key Size", "Size of each key display", 12, 30, 22);
    private final ColorSetting pressedColor = new ColorSetting("Pressed Color", "Color when key is pressed", GlacierTheme.ACCENT);

    // Raw click tracking (independent of MC keybinds, so it always registers).
    private final Deque<Long> lmbClicks = new ArrayDeque<>();
    private final Deque<Long> rmbClicks = new ArrayDeque<>();
    private boolean lmbWas, rmbWas;

    public Keystrokes() {
        super("Keystrokes", "Shows keyboard and mouse button presses", 74, 100);
        addSettings(showMouseButtons, showCps, showSpacebar, keySize, pressedColor);
    }

    private int cps(Deque<Long> q, long now) {
        q.removeIf(t -> t < now - 1000L);
        return q.size();
    }

    private void drawKey(DrawContext ctx, MinecraftClient mc, String label, int x, int y, int w, int h, boolean pressed) {
        int bg = pressed ? pressedColor.getValue() : 0xC0181A1F;
        int fg = pressed ? GlacierTheme.BG : getTextColor();
        RenderUtil.drawRoundedRect(ctx, x, y, w, h, 3, bg);
        RenderUtil.drawOutline(ctx, x, y, w, h, 1, pressed ? pressedColor.getValue() : 0x40FFFFFF);
        int tx = x + (w - mc.textRenderer.getWidth(label)) / 2;
        int ty = y + (h - 8) / 2;
        ctx.drawText(mc.textRenderer, label, tx, ty, fg, hasShadow());
    }

    private void drawMouseKey(DrawContext ctx, MinecraftClient mc, String label, int count,
                              int x, int y, int w, int h, boolean pressed) {
        int bg = pressed ? pressedColor.getValue() : 0xC0181A1F;
        int fg = pressed ? GlacierTheme.BG : getTextColor();
        RenderUtil.drawRoundedRect(ctx, x, y, w, h, 3, bg);
        RenderUtil.drawOutline(ctx, x, y, w, h, 1, pressed ? pressedColor.getValue() : 0x40FFFFFF);
        boolean twoLine = showCps.getValue() && h >= 18;
        if (twoLine) {
            int tx = x + (w - mc.textRenderer.getWidth(label)) / 2;
            ctx.drawText(mc.textRenderer, label, tx, y + 3, fg, hasShadow());
            String c = String.valueOf(count);
            int cx = x + (w - mc.textRenderer.getWidth(c)) / 2;
            ctx.drawText(mc.textRenderer, c, cx, y + h - 11, fg, hasShadow());
        } else {
            int tx = x + (w - mc.textRenderer.getWidth(label)) / 2;
            ctx.drawText(mc.textRenderer, label, tx, y + (h - 8) / 2, fg, hasShadow());
        }
    }

    @Override
    public void render(DrawContext context, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null || mc.options == null) return;
        long win = mc.getWindow().getHandle();
        long now = System.currentTimeMillis();

        int ks = (int) (double) keySize.getValue();
        int gap = 3;
        int bx = getX();
        int by = getY();
        int rowW = ks * 3 + gap * 2;

        boolean w = mc.options.forwardKey.isPressed();
        boolean a = mc.options.leftKey.isPressed();
        boolean s = mc.options.backKey.isPressed();
        boolean d = mc.options.rightKey.isPressed();

        drawBackground(context, bx, by, rowW, getScaledHeight());

        int y = by;
        drawKey(context, mc, "W", bx + ks + gap, y, ks, ks, w);
        y += ks + gap;
        drawKey(context, mc, "A", bx, y, ks, ks, a);
        drawKey(context, mc, "S", bx + ks + gap, y, ks, ks, s);
        drawKey(context, mc, "D", bx + (ks + gap) * 2, y, ks, ks, d);
        y += ks + gap;

        if (showMouseButtons.getValue()) {
            boolean lmb = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            boolean rmb = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
            if (lmb && !lmbWas) lmbClicks.addLast(now);
            if (rmb && !rmbWas) rmbClicks.addLast(now);
            lmbWas = lmb; rmbWas = rmb;
            int half = (rowW - gap) / 2;
            drawMouseKey(context, mc, "LMB", cps(lmbClicks, now), bx, y, half, ks, lmb);
            drawMouseKey(context, mc, "RMB", cps(rmbClicks, now), bx + half + gap, y, rowW - half - gap, ks, rmb);
            y += ks + gap;
        }

        if (showSpacebar.getValue()) {
            boolean space = mc.options.jumpKey.isPressed();
            int spaceH = Math.max(8, ks / 2);
            drawKey(context, mc, "", bx, y, rowW, spaceH, space);
        }
    }
}
