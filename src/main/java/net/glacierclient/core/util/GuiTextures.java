package net.glacierclient.core.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Nine-slice GUI backgrounds loaded from {@code assets/glacierclient/textures/gui/<name>.png}, with
 * border metadata read from a sibling {@code <name>.json} ({@code nineslice_size} + {@code base_size}
 * = [w,h]). Corners are drawn unscaled; edges/center are stretched. Tiny flat fills (e.g. 2x2 with a
 * 1px border, where there is no middle texel) are stretched whole. Falls back gracefully — callers
 * use the drawn rounded shapes whenever a texture is absent.
 */
public final class GuiTextures {

    private GuiTextures() {}

    private record Spec(int corner, int texW, int texH) {}

    private static final Map<String, Identifier> IDS = new HashMap<>();
    private static final Map<String, Boolean> PRESENT = new HashMap<>();
    private static final Map<String, Spec> SPECS = new HashMap<>();

    private static Identifier id(String name) {
        return IDS.computeIfAbsent(name, n -> new Identifier("glacierclient", "textures/gui/" + n + ".png"));
    }

    public static boolean has(String name) {
        Boolean p = PRESENT.get(name);
        if (p == null) {
            p = MinecraftClient.getInstance().getResourceManager().getResource(id(name)).isPresent();
            PRESENT.put(name, p);
        }
        return p;
    }

    private static Spec spec(String name) {
        Spec s = SPECS.get(name);
        if (s != null) return s;
        int corner = 8, w = 32, h = 32;
        try {
            Identifier jid = new Identifier("glacierclient", "textures/gui/" + name + ".json");
            Optional<Resource> r = MinecraftClient.getInstance().getResourceManager().getResource(jid);
            if (r.isPresent()) {
                try (InputStream in = r.get().getInputStream()) {
                    JsonObject o = JsonParser.parseString(new String(in.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
                    if (o.has("nineslice_size")) corner = o.get("nineslice_size").getAsInt();
                    if (o.has("base_size")) {
                        w = o.getAsJsonArray("base_size").get(0).getAsInt();
                        h = o.getAsJsonArray("base_size").get(1).getAsInt();
                    }
                }
            }
        } catch (Exception ignored) {}
        s = new Spec(corner, w, h);
        SPECS.put(name, s);
        return s;
    }

    public static void nineSlice(DrawContext ctx, String name, int x, int y, int w, int h) {
        if (w <= 0 || h <= 0) return;
        Spec s = spec(name);
        Identifier t = id(name);
        int cw = Math.min(s.corner, s.texW / 2);   // texture-space corner
        int ch = Math.min(s.corner, s.texH / 2);
        int midTexW = s.texW - 2 * cw;
        int midTexH = s.texH - 2 * ch;

        // flat fill (no middle texel) -> stretch whole texture
        if (midTexW <= 0 || midTexH <= 0) {
            blit(ctx, t, x, y, w, h, 0, 0, s.texW, s.texH, s.texW, s.texH);
            return;
        }

        int c = Math.min(cw, Math.min(w, h) / 2);   // screen-space corner
        int farXt = s.texW - cw, farYt = s.texH - ch;
        int farXs = x + w - c, farYs = y + h - c;
        int midWs = w - 2 * c, midHs = h - 2 * c;

        // corners
        blit(ctx, t, x,     y,     c, c, 0,     0,     cw, ch, s.texW, s.texH);
        blit(ctx, t, farXs, y,     c, c, farXt, 0,     cw, ch, s.texW, s.texH);
        blit(ctx, t, x,     farYs, c, c, 0,     farYt, cw, ch, s.texW, s.texH);
        blit(ctx, t, farXs, farYs, c, c, farXt, farYt, cw, ch, s.texW, s.texH);
        // edges
        blit(ctx, t, x + c, y,     midWs, c, cw,    0,     midTexW, ch, s.texW, s.texH);
        blit(ctx, t, x + c, farYs, midWs, c, cw,    farYt, midTexW, ch, s.texW, s.texH);
        blit(ctx, t, x,     y + c, c, midHs, 0,     ch,    cw, midTexH, s.texW, s.texH);
        blit(ctx, t, farXs, y + c, c, midHs, farXt, ch,    cw, midTexH, s.texW, s.texH);
        // center
        blit(ctx, t, x + c, y + c, midWs, midHs, cw, ch, midTexW, midTexH, s.texW, s.texH);
    }

    private static void blit(DrawContext ctx, Identifier t, int dx, int dy, int dw, int dh,
                             int u, int v, int rw, int rh, int tw, int th) {
        if (dw <= 0 || dh <= 0) return;
        ctx.drawTexture(t, dx, dy, dw, dh, (float) u, (float) v, rw, rh, tw, th);
    }
}
