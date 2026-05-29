package net.glacierclient.gui.widget;

import net.glacierclient.core.theme.GlacierTheme;
import net.glacierclient.core.util.RenderUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.function.IntConsumer;

/**
 * Self-contained ARGB colour picker overlay with four draggable channel sliders (R, G, B, A) and a
 * live preview swatch. Renders on top of everything and reports whether it consumed input.
 */
public class ColorPicker {

    public static final int W = 188;
    public static final int H = 116;

    private boolean open;
    private int x, y;
    private int a, r, g, b;
    private IntConsumer onChange;
    private int dragChannel = -1; // 0=R 1=G 2=B 3=A

    public boolean isOpen() { return open; }

    public void open(int x, int y, int argb, IntConsumer onChange) {
        this.open = true;
        this.x = x;
        this.y = y;
        this.a = (argb >> 24) & 0xFF;
        this.r = (argb >> 16) & 0xFF;
        this.g = (argb >> 8) & 0xFF;
        this.b = argb & 0xFF;
        this.onChange = onChange;
        this.dragChannel = -1;
    }

    public void close() { open = false; dragChannel = -1; }

    public int current() { return (a << 24) | (r << 16) | (g << 8) | b; }

    private int channelValue(int ch) {
        return switch (ch) { case 0 -> r; case 1 -> g; case 2 -> b; default -> a; };
    }

    private void setChannel(int ch, int v) {
        v = Math.max(0, Math.min(255, v));
        switch (ch) { case 0 -> r = v; case 1 -> g = v; case 2 -> b = v; default -> a = v; }
        if (onChange != null) onChange.accept(current());
    }

    // bar geometry
    private int barX() { return x + 10; }
    private int barW() { return W - 20; }
    private int barY(int ch) { return y + 34 + ch * 18; }

    public void render(DrawContext ctx, TextRenderer tr, int mouseX, int mouseY) {
        if (!open) return;
        RenderUtil.drawRoundedRect(ctx, x, y, W, H, GlacierTheme.RADIUS_SM, GlacierTheme.BG_PANEL);
        RenderUtil.drawOutline(ctx, x, y, W, H, 1, GlacierTheme.ACCENT);

        ctx.drawTextWithShadow(tr, "Color", x + 10, y + 8, GlacierTheme.ACCENT);
        // preview swatch (checkerboard behind for alpha)
        int pwX = x + W - 34, pwY = y + 6, pw = 24;
        checker(ctx, pwX, pwY, pw, pw);
        ctx.fill(pwX, pwY, pwX + pw, pwY + pw, current());
        RenderUtil.drawOutline(ctx, pwX, pwY, pw, pw, 1, GlacierTheme.TEXT_DIM);

        String[] labels = {"R", "G", "B", "A"};
        for (int ch = 0; ch < 4; ch++) {
            int by = barY(ch);
            int bx = barX(), bw = barW();
            // track with channel-tinted fill
            ctx.fill(bx, by, bx + bw, by + 6, GlacierTheme.BG_ITEM);
            int fill = (int) (bw * channelValue(ch) / 255f);
            int tint = switch (ch) { case 0 -> 0xFFF04747; case 1 -> 0xFF43B581; case 2 -> 0xFF7289DA; default -> 0xFFCCCCCC; };
            ctx.fill(bx, by, bx + fill, by + 6, tint);
            int knobX = bx + fill - 2;
            ctx.fill(knobX, by - 2, knobX + 4, by + 8, GlacierTheme.TEXT);
            ctx.drawTextWithShadow(tr, labels[ch], x + 2, by - 1, GlacierTheme.TEXT_DIM);
            String val = String.valueOf(channelValue(ch));
            ctx.drawTextWithShadow(tr, val, bx + bw + 2, by - 1, GlacierTheme.TEXT_DIM);
        }
    }

    private void checker(DrawContext ctx, int x, int y, int w, int h) {
        int s = 4;
        for (int iy = 0; iy < h; iy += s)
            for (int ix = 0; ix < w; ix += s) {
                boolean dark = ((ix / s) + (iy / s)) % 2 == 0;
                ctx.fill(x + ix, y + iy, Math.min(x + ix + s, x + w), Math.min(y + iy + s, y + h),
                        dark ? 0xFF888888 : 0xFFCCCCCC);
            }
    }

    /** @return true if the click was inside the picker (consumed). */
    public boolean mouseClicked(double mx, double my) {
        if (!open) return false;
        if (mx < x || mx > x + W || my < y || my > y + H) { close(); return true; }
        for (int ch = 0; ch < 4; ch++) {
            int by = barY(ch);
            if (my >= by - 3 && my <= by + 9 && mx >= barX() - 2 && mx <= barX() + barW() + 2) {
                dragChannel = ch;
                setChannel(ch, (int) ((mx - barX()) / barW() * 255));
                return true;
            }
        }
        return true; // swallow clicks inside the panel
    }

    public boolean mouseDragged(double mx, double my) {
        if (!open || dragChannel < 0) return false;
        setChannel(dragChannel, (int) ((mx - barX()) / barW() * 255));
        return true;
    }

    public void mouseReleased() { dragChannel = -1; }
}
