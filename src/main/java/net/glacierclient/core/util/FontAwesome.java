package net.glacierclient.core.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Font Awesome (free) glyph icons rendered through Minecraft's own text pipeline — no PNGs, tiny,
 * and GPU-cheap. Glyph lookups come from the generated {@link FaIcons} map; the TTFs ship as font
 * providers ({@code fa_solid.json} / {@code fa_brands.json}).
 *
 * <p>Keys are module slugs (e.g. {@code "armorstatus"}) or UI keys (e.g. {@code "ui_close"},
 * {@code "cat_hud"}). {@link #drawName} slugifies a display name first.</p>
 */
public final class FontAwesome {

    private FontAwesome() {}

    private static final Identifier SOLID  = new Identifier("glacierclient", "fa_solid");
    private static final Identifier BRANDS = new Identifier("glacierclient", "fa_brands");
    private static final Identifier REGULAR = new Identifier("glacierclient", "fa_solid"); // regular set not shipped; fall back to solid

    // Cache built Text per key so we don't rebuild Text+Style every frame.
    private static final java.util.Map<String, Text> CACHE = new java.util.HashMap<>();

    public static boolean has(String key) {
        return resolve(key) != null;
    }

    private static String resolve(String key) {
        if (key == null) return null;
        String v = FaIcons.M.get(key);
        if (v == null) v = FaIcons.M.get("ui_" + key); // chrome keys may be passed without the ui_ prefix
        return v;
    }

    private static Text text(String key) {
        return CACHE.computeIfAbsent(key, k -> {
            String v = resolve(k);
            if (v == null) return null;
            char flag = v.charAt(v.length() - 1);
            int cp = Integer.parseInt(v.substring(0, v.length() - 1), 16);
            Identifier font = flag == 'b' ? BRANDS : flag == 'r' ? REGULAR : SOLID;
            return Text.literal(new String(Character.toChars(cp))).setStyle(Style.EMPTY.withFont(font));
        });
    }

    private static String slug(String name) {
        return name == null ? "" : name.toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    /** Draw the icon for a module display name, centred on (cx,cy) at {@code px} pixels. */
    public static boolean drawName(DrawContext ctx, TextRenderer tr, String name, int cx, int cy, int px, int color) {
        return draw(ctx, tr, slug(name), cx, cy, px, color);
    }

    /** Draw the icon for a slug/UI key, centred on (cx,cy) at roughly {@code px} pixels tall. */
    public static boolean draw(DrawContext ctx, TextRenderer tr, String key, int cx, int cy, int px, int color) {
        Text t = text(key);
        if (t == null) return false;
        // The provider renders glyphs ~8px tall at its configured size; scale to the requested size.
        float base = 8f;
        float scale = Math.max(0.1f, px / base);
        int w = tr.getWidth(t);
        var ms = ctx.getMatrices();
        ms.push();
        ms.translate(cx, cy, 0);
        ms.scale(scale, scale, 1f);
        ctx.drawText(tr, t, -w / 2, -4, color, false);
        ms.pop();
        return true;
    }
}
