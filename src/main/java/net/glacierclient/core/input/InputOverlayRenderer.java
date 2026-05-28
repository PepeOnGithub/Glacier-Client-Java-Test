package net.glacierclient.core.input;

import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class InputOverlayRenderer {
    private static final InputOverlayRenderer INSTANCE = new InputOverlayRenderer();
    public static InputOverlayRenderer get() { return INSTANCE; }

    private boolean visible = false;
    private int x = 4, y = 4;

    public void setVisible(boolean v) { visible = v; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }

    public void render(DrawContext ctx) {
        if (!visible) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        boolean w = mc.options.forwardKey.isPressed();
        boolean a = mc.options.leftKey.isPressed();
        boolean s = mc.options.backKey.isPressed();
        boolean d = mc.options.rightKey.isPressed();
        boolean space = mc.options.jumpKey.isPressed();
        boolean shift = mc.options.sneakKey.isPressed();
        boolean lmb = mc.options.attackKey.isPressed();
        boolean rmb = mc.options.useKey.isPressed();

        int keyW = 18, keyH = 14, gap = 2;
        int px = x, py = y;

        drawKey(ctx, px + keyW + gap, py, keyW, keyH, "W", w);
        drawKey(ctx, px, py + keyH + gap, keyW, keyH, "A", a);
        drawKey(ctx, px + keyW + gap, py + keyH + gap, keyW, keyH, "S", s);
        drawKey(ctx, px + (keyW + gap) * 2, py + keyH + gap, keyW, keyH, "D", d);
        drawKey(ctx, px, py + (keyH + gap) * 2, keyW * 2 + gap, keyH, "SPACE", space);
        drawKey(ctx, px + (keyW + gap) * 2 + keyW + gap, py + keyH + gap, keyW, keyH, "⇧", shift);
        drawKey(ctx, px + (keyW + gap) * 3 + keyW, py, keyW, keyH, "LMB", lmb);
        drawKey(ctx, px + (keyW + gap) * 4 + keyW, py, keyW, keyH, "RMB", rmb);
    }

    private void drawKey(DrawContext ctx, int x, int y, int w, int h, String label, boolean pressed) {
        int bg = pressed ? GlacierTheme.ACCENT : GlacierTheme.BG_PANEL;
        int fg = pressed ? GlacierTheme.TEXT : GlacierTheme.TEXT_DIM;
        ctx.fill(x, y, x + w, y + h, bg);
        ctx.drawCenteredTextWithShadow(
            MinecraftClient.getInstance().textRenderer,
            net.minecraft.text.Text.literal(label),
            x + w / 2, y + (h - 6) / 2, fg
        );
    }
}
