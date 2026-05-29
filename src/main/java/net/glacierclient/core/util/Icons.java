package net.glacierclient.core.util;

import net.minecraft.client.gui.DrawContext;

/**
 * Lightweight programmatic icon set drawn with fills (no texture assets required).
 *
 * <p>{@link #draw} picks a glyph by matching keywords in the module/cosmetic name, falling back to
 * a category glyph and finally to a rounded letter badge. All glyphs are axis-aligned vector art so
 * they render crisply at any GUI scale.</p>
 */
public final class Icons {

    private Icons() {}

    /** Draws an icon centred on (cx, cy) sized roughly {@code size} px, picking a glyph from {@code name}. */
    public static void draw(DrawContext ctx, net.minecraft.client.font.TextRenderer tr,
                            String name, String category, int cx, int cy, int size, int color) {
        String n = name.toLowerCase();
        int r = size / 2;

        if (contains(n, "chat", "message", "compact", "spam", "mention")) { chat(ctx, cx, cy, r, color); return; }
        if (contains(n, "clock", "time", "timer", "session")) { clock(ctx, cx, cy, r, color); return; }
        if (contains(n, "compass", "direction")) { compass(ctx, cx, cy, r, color); return; }
        if (contains(n, "coord", "radar", "target", "tracker", "minimap", "map")) {
            if (n.contains("map") || n.contains("chunk")) { grid(ctx, cx, cy, r, color); return; }
            target(ctx, cx, cy, r, color); return;
        }
        if (contains(n, "armor", "shield", "totem", "durab")) { shield(ctx, cx, cy, r, color); return; }
        if (contains(n, "bow", "arrow", "pearl")) { bow(ctx, cx, cy, r, color); return; }
        if (contains(n, "fps", "graph", "tps", "stat", "frame", "memory", "ram", "cpu")) { bars(ctx, cx, cy, r, color); return; }
        if (contains(n, "ping", "packet", "signal", "stream", "voice", "discord")) { signal(ctx, cx, cy, r, color); return; }
        if (contains(n, "crosshair")) { crosshair(ctx, cx, cy, r, color); return; }
        if (contains(n, "combo", "heart", "health", "hit", "pvp", "weapon", "sword")) { heart(ctx, cx, cy, r, color); return; }
        if (contains(n, "music", "sound", "note", "audio", "spotify")) { note(ctx, cx, cy, r, color); return; }
        if (contains(n, "player", "friend", "nick", "account", "party")) { person(ctx, cx, cy, r, color); return; }
        if (contains(n, "render", "fov", "fog", "sky", "cape", "wing", "fullbright", "glow", "shader", "water", "light")) { eye(ctx, cx, cy, r, color); return; }
        if (contains(n, "performance", "fast", "boost", "optimi", "culling", "lod")) { bolt(ctx, cx, cy, r, color); return; }
        if (contains(n, "key", "control", "bind")) { grid(ctx, cx, cy, r, color); return; }

        // Category fallbacks
        if (category != null) {
            switch (category.toUpperCase()) {
                case "HUD": bars(ctx, cx, cy, r, color); return;
                case "RENDER": eye(ctx, cx, cy, r, color); return;
                case "PVP": heart(ctx, cx, cy, r, color); return;
                case "PERFORMANCE": bolt(ctx, cx, cy, r, color); return;
                case "COSMETICS": note(ctx, cx, cy, r, color); return;
                default: break;
            }
        }

        // Letter badge fallback
        letterBadge(ctx, tr, name, cx, cy, r, color);
    }

    private static boolean contains(String haystack, String... needles) {
        for (String s : needles) if (haystack.contains(s)) return true;
        return false;
    }

    // ---------------------------------------------------------------------
    // Primitives
    // ---------------------------------------------------------------------

    /** Filled disc. */
    public static void disc(DrawContext ctx, int cx, int cy, int r, int color) {
        for (int dy = -r; dy <= r; dy++) {
            int dx = (int) Math.sqrt((double) r * r - dy * dy);
            ctx.fill(cx - dx, cy + dy, cx + dx + 1, cy + dy + 1, color);
        }
    }

    /** Circle outline of the given thickness. */
    public static void ring(DrawContext ctx, int cx, int cy, int rOuter, int thickness, int color) {
        int rInner = Math.max(0, rOuter - thickness);
        for (int dy = -rOuter; dy <= rOuter; dy++) {
            int outDx = (int) Math.sqrt((double) rOuter * rOuter - dy * dy);
            double innSq = (double) rInner * rInner - dy * dy;
            if (innSq <= 0) {
                ctx.fill(cx - outDx, cy + dy, cx + outDx + 1, cy + dy + 1, color);
            } else {
                int innDx = (int) Math.sqrt(innSq);
                ctx.fill(cx - outDx, cy + dy, cx - innDx, cy + dy + 1, color);
                ctx.fill(cx + innDx + 1, cy + dy, cx + outDx + 1, cy + dy + 1, color);
            }
        }
    }

    private static void rect(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + h, color);
    }

    // ---------------------------------------------------------------------
    // Glyphs
    // ---------------------------------------------------------------------

    private static void chat(DrawContext ctx, int cx, int cy, int r, int c) {
        int w = (int) (r * 1.8), h = (int) (r * 1.3);
        int x = cx - w / 2, y = cy - h / 2 - 2;
        RenderUtil.drawRoundedRect(ctx, x, y, w, h, 4, c);
        // tail
        rect(ctx, x + w / 4, y + h - 1, 6, 6, c);
        // dots (background-coloured holes approximated with darker accent)
        int dot = 0xFF23272A;
        int dy = y + h / 2 - 1;
        rect(ctx, cx - r / 2 - 1, dy, 3, 3, dot);
        rect(ctx, cx - 1, dy, 3, 3, dot);
        rect(ctx, cx + r / 2 - 1, dy, 3, 3, dot);
    }

    private static void clock(DrawContext ctx, int cx, int cy, int r, int c) {
        ring(ctx, cx, cy, r, 2, c);
        rect(ctx, cx - 1, cy - r + 4, 2, r - 3, c); // minute hand up
        rect(ctx, cx, cy - 1, r / 2, 2, c);          // hour hand right
    }

    private static void compass(DrawContext ctx, int cx, int cy, int r, int c) {
        ring(ctx, cx, cy, r, 2, c);
        // needle: a vertical diamond
        for (int dy = -r + 4; dy <= r - 4; dy++) {
            int wdt = (r - 4 - Math.abs(dy)) / 2;
            if (wdt > 0) rect(ctx, cx - wdt, cy + dy, wdt * 2, 1, c);
        }
    }

    private static void grid(DrawContext ctx, int cx, int cy, int r, int c) {
        int cell = Math.max(3, r / 2 - 1);
        int gap = 3;
        int total = cell * 3 + gap * 2;
        int x0 = cx - total / 2, y0 = cy - total / 2;
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                rect(ctx, x0 + col * (cell + gap), y0 + row * (cell + gap), cell, cell, c);
    }

    private static void shield(DrawContext ctx, int cx, int cy, int r, int c) {
        int top = cy - r, h = r * 2;
        for (int i = 0; i < h; i++) {
            int y = top + i;
            int half;
            if (i < h * 0.6) half = r;                       // square top
            else half = (int) (r * (1 - (i - h * 0.6) / (h * 0.4))); // taper to point
            if (half > 0) rect(ctx, cx - half, y, half * 2, 1, c);
        }
    }

    private static void bow(DrawContext ctx, int cx, int cy, int r, int c) {
        // diagonal arrow shaft
        for (int i = -r; i <= r; i++) {
            rect(ctx, cx + i - 1, cy - i - 1, 3, 3, c);
        }
        // arrowhead (top-right)
        rect(ctx, cx + r - 5, cy - r, 6, 2, c);
        rect(ctx, cx + r - 1, cy - r, 2, 6, c);
    }

    private static void bars(DrawContext ctx, int cx, int cy, int r, int c) {
        int bw = Math.max(3, r / 2 - 1);
        int gap = 3;
        int total = bw * 3 + gap * 2;
        int x0 = cx - total / 2;
        int base = cy + r;
        int[] heights = {r, (int) (r * 1.5), (int) (r * 1.1)};
        for (int i = 0; i < 3; i++) {
            int hgt = heights[i];
            rect(ctx, x0 + i * (bw + gap), base - hgt, bw, hgt, c);
        }
    }

    private static void signal(DrawContext ctx, int cx, int cy, int r, int c) {
        int bw = Math.max(3, r / 2 - 1);
        int gap = 3;
        int total = bw * 3 + gap * 2;
        int x0 = cx - total / 2;
        int base = cy + r;
        for (int i = 0; i < 3; i++) {
            int hgt = (i + 1) * (r * 2 / 3);
            rect(ctx, x0 + i * (bw + gap), base - hgt, bw, hgt, c);
        }
    }

    private static void target(DrawContext ctx, int cx, int cy, int r, int c) {
        ring(ctx, cx, cy, r, 2, c);
        ring(ctx, cx, cy, Math.max(3, r * 2 / 3), 2, c);
        disc(ctx, cx, cy, Math.max(2, r / 4), c);
    }

    private static void crosshair(DrawContext ctx, int cx, int cy, int r, int c) {
        ring(ctx, cx, cy, r, 2, c);
        rect(ctx, cx - 1, cy - r - 3, 2, 6, c);
        rect(ctx, cx - 1, cy + r - 3, 2, 6, c);
        rect(ctx, cx - r - 3, cy - 1, 6, 2, c);
        rect(ctx, cx + r - 3, cy - 1, 6, 2, c);
        rect(ctx, cx - 1, cy - 1, 2, 2, c);
    }

    private static void heart(DrawContext ctx, int cx, int cy, int r, int c) {
        int lobe = r / 2;
        disc(ctx, cx - lobe + 1, cy - lobe / 2, lobe, c);
        disc(ctx, cx + lobe - 1, cy - lobe / 2, lobe, c);
        for (int i = 0; i <= r; i++) {
            int half = r - i;
            rect(ctx, cx - half, cy - lobe / 2 + i, half * 2, 1, c);
        }
    }

    private static void note(DrawContext ctx, int cx, int cy, int r, int c) {
        disc(ctx, cx - r / 2, cy + r / 2, Math.max(3, r / 3), c); // note head
        rect(ctx, cx - r / 2 + r / 3 - 1, cy - r, 2, r + r / 2, c); // stem
        rect(ctx, cx - r / 2 + r / 3 - 1, cy - r, r / 2, 2, c);     // flag
    }

    private static void person(DrawContext ctx, int cx, int cy, int r, int c) {
        disc(ctx, cx, cy - r / 2, Math.max(3, r / 3), c); // head
        // shoulders (trapezoid)
        for (int i = 0; i < r; i++) {
            int half = r / 3 + i / 2;
            rect(ctx, cx - half, cy + i, half * 2, 1, c);
        }
    }

    private static void eye(DrawContext ctx, int cx, int cy, int r, int c) {
        // almond outline approximated by two stacked arcs
        for (int dx = -r; dx <= r; dx++) {
            int hgt = (int) (Math.sqrt((double) r * r - dx * dx) * 0.55);
            ctx.fill(cx + dx, cy - hgt, cx + dx + 1, cy - hgt + 1, c);
            ctx.fill(cx + dx, cy + hgt - 1, cx + dx + 1, cy + hgt, c);
        }
        disc(ctx, cx, cy, Math.max(2, r / 3), c); // pupil
    }

    private static void bolt(DrawContext ctx, int cx, int cy, int r, int c) {
        // lightning bolt as two offset parallelograms
        for (int i = 0; i < r; i++) {
            rect(ctx, cx - i / 2, cy - r + i, Math.max(2, r / 3), 1, c);
        }
        for (int i = 0; i < r; i++) {
            rect(ctx, cx - r / 4 + i / 2, cy + i, Math.max(2, r / 3), 1, c);
        }
    }

    private static void letterBadge(DrawContext ctx, net.minecraft.client.font.TextRenderer tr,
                                    String name, int cx, int cy, int r, int color) {
        int boxW = r * 2, boxH = r * 2;
        RenderUtil.drawRoundedRect(ctx, cx - r, cy - r, boxW, boxH, 6, 0x22FFFFFF);
        RenderUtil.drawOutline(ctx, cx - r, cy - r, boxW, boxH, 1, color);
        String letter = name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase();
        ctx.drawTextWithShadow(tr, letter, cx - tr.getWidth(letter) / 2, cy - 4, color);
    }

    /** Settings gear used on cards. */
    public static void gear(DrawContext ctx, int cx, int cy, int r, int color) {
        ring(ctx, cx, cy, r, 2, color);
        disc(ctx, cx, cy, Math.max(1, r / 3), color);
        // teeth
        rect(ctx, cx - 1, cy - r - 2, 2, 4, color);
        rect(ctx, cx - 1, cy + r - 2, 2, 4, color);
        rect(ctx, cx - r - 2, cy - 1, 4, 2, color);
        rect(ctx, cx + r - 2, cy - 1, 4, 2, color);
    }

    /** Bear-head logo mark for the header. */
    public static void bear(DrawContext ctx, int cx, int cy, int size, int color) {
        int r = size / 2;
        ring(ctx, cx, cy, r, 2, color);              // head
        ring(ctx, cx - r, cy - r, r / 2, 2, color);  // left ear
        ring(ctx, cx + r, cy - r, r / 2, 2, color);  // right ear
        disc(ctx, cx - r / 3, cy - 1, 2, color);     // eyes
        disc(ctx, cx + r / 3, cy - 1, 2, color);
        disc(ctx, cx, cy + r / 3, Math.max(2, r / 4), color); // snout
    }
}
