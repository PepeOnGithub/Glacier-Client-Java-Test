package net.glacierclient.core.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Nine-slice GUI backgrounds loaded from Glacier texture assets. Metadata is declared in logical UI
 * pixels while PNGs may be higher-resolution, so source UVs are scaled to the actual texture size.
 */
public final class GuiTextures {

    private GuiTextures() {}

    private record Insets(int left, int top, int right, int bottom) {}
    private record Spec(Insets slice, int baseW, int baseH, int texW, int texH) {}

    private static final Map<String, Identifier> IDS = new HashMap<>();
    private static final Map<String, Identifier> JSON_IDS = new HashMap<>();
    private static final Map<String, Boolean> PRESENT = new HashMap<>();
    private static final Map<String, Spec> SPECS = new HashMap<>();

    private static Identifier id(String name) {
        return IDS.computeIfAbsent(name, GuiTextures::resolveTextureId);
    }

    private static Identifier jsonId(String name) {
        return JSON_IDS.computeIfAbsent(name, n -> {
            String path = id(n).getPath();
            return new Identifier("glacierclient", path.substring(0, path.length() - 4) + ".json");
        });
    }

    private static Identifier resolveTextureId(String name) {
        String key = name.toLowerCase(Locale.ROOT);
        String[] paths;
        if ("bg".equals(key)) {
            paths = new String[]{"textures/common/bg.png", "textures/common/bg/bg.png", "textures/gui/bg.png"};
        } else {
            paths = new String[]{"textures/common/bg/" + key + ".png", "textures/gui/" + key + ".png", "textures/common/" + key + ".png"};
        }
        MinecraftClient client = MinecraftClient.getInstance();
        for (String path : paths) {
            Identifier candidate = new Identifier("glacierclient", path);
            if (client.getResourceManager().getResource(candidate).isPresent()) return candidate;
        }
        return new Identifier("glacierclient", paths[0]);
    }

    public static boolean has(String name) {
        Boolean p = PRESENT.get(name);
        if (p != null && p) return true;
        boolean present = MinecraftClient.getInstance().getResourceManager().getResource(id(name)).isPresent();
        if (present) PRESENT.put(name, true);
        return present;
    }

    private static Spec spec(String name) {
        Spec s = SPECS.get(name);
        if (s != null) return s;

        Insets slice = new Insets(8, 8, 8, 8);
        int baseW = 32, baseH = 32, texW = 32, texH = 32;
        try {
            Optional<Resource> tex = MinecraftClient.getInstance().getResourceManager().getResource(id(name));
            if (tex.isPresent()) {
                try (InputStream in = tex.get().getInputStream(); NativeImage image = NativeImage.read(in)) {
                    texW = image.getWidth();
                    texH = image.getHeight();
                    baseW = texW;
                    baseH = texH;
                }
            }

            Optional<Resource> json = MinecraftClient.getInstance().getResourceManager().getResource(jsonId(name));
            if (json.isPresent()) {
                try (InputStream in = json.get().getInputStream()) {
                    JsonObject o = JsonParser.parseString(new String(in.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
                    if (o.has("nineslice_size")) slice = parseInsets(o.get("nineslice_size"));
                    if (o.has("base_size")) {
                        JsonArray base = o.getAsJsonArray("base_size");
                        baseW = Math.max(1, rounded(base.get(0)));
                        baseH = Math.max(1, rounded(base.get(1)));
                    }
                }
            }
        } catch (Exception ignored) {}

        s = new Spec(slice, baseW, baseH, texW, texH);
        SPECS.put(name, s);
        return s;
    }

    private static Insets parseInsets(JsonElement element) {
        if (element.isJsonArray()) {
            JsonArray a = element.getAsJsonArray();
            if (a.size() >= 4) return new Insets(rounded(a.get(0)), rounded(a.get(1)), rounded(a.get(2)), rounded(a.get(3)));
            if (a.size() >= 2) return new Insets(rounded(a.get(0)), rounded(a.get(1)), rounded(a.get(0)), rounded(a.get(1)));
            if (a.size() == 1) return uniform(rounded(a.get(0)));
        }
        return uniform(rounded(element));
    }

    private static Insets uniform(int value) {
        int v = Math.max(0, value);
        return new Insets(v, v, v, v);
    }

    private static int rounded(JsonElement element) {
        return Math.max(0, Math.round(element.getAsFloat()));
    }

    /** Stretch the whole named texture across the given area, e.g. a full-screen background. */
    public static void fullscreen(DrawContext ctx, String name, int w, int h) {
        if (w <= 0 || h <= 0 || !has(name)) return;
        Spec s = spec(name);
        ctx.drawTexture(id(name), 0, 0, w, h, 0f, 0f, s.texW, s.texH, s.texW, s.texH);
    }

    /** Nine-slice the named texture if it exists, otherwise draw a rounded rect of {@code fallback}. */
    public static void rect(DrawContext ctx, String name, int x, int y, int w, int h, int fallback) {
        if (has(name)) nineSlice(ctx, name, x, y, w, h);
        else RenderUtil.drawRoundedRect(ctx, x, y, w, h, 6, fallback);
    }

    public static void nineSlice(DrawContext ctx, String name, int x, int y, int w, int h) {
        if (w <= 0 || h <= 0) return;
        // Background panels/cards render translucent for a glassy, layered look.
        float alpha = name.endsWith("_bg") ? 0.65f : 1f;
        if (alpha < 1f) { RenderSystem.enableBlend(); RenderSystem.setShaderColor(1f, 1f, 1f, alpha); }
        try {
        Spec s = spec(name);
        Identifier t = id(name);

        int dl = s.slice.left;
        int dt = s.slice.top;
        int dr = s.slice.right;
        int db = s.slice.bottom;
        int sl = scaled(dl, s.texW, s.baseW);
        int st = scaled(dt, s.texH, s.baseH);
        int sr = scaled(dr, s.texW, s.baseW);
        int sb = scaled(db, s.texH, s.baseH);
        int midTexW = s.texW - sl - sr;
        int midTexH = s.texH - st - sb;

        if (midTexW <= 0 || midTexH <= 0) {
            blit(ctx, t, x, y, w, h, 0, 0, s.texW, s.texH, s.texW, s.texH);
            return;
        }

        int[] hs = fitInsets(dl, dr, w);
        int[] vs = fitInsets(dt, db, h);
        dl = hs[0];
        dr = hs[1];
        dt = vs[0];
        db = vs[1];

        int midWs = w - dl - dr;
        int midHs = h - dt - db;
        int farXt = s.texW - sr;
        int farYt = s.texH - sb;
        int farXs = x + w - dr;
        int farYs = y + h - db;

        blit(ctx, t, x,     y,     dl, dt, 0,     0,     sl, st, s.texW, s.texH);
        blit(ctx, t, farXs, y,     dr, dt, farXt, 0,     sr, st, s.texW, s.texH);
        blit(ctx, t, x,     farYs, dl, db, 0,     farYt, sl, sb, s.texW, s.texH);
        blit(ctx, t, farXs, farYs, dr, db, farXt, farYt, sr, sb, s.texW, s.texH);

        blit(ctx, t, x + dl, y,      midWs, dt, sl,    0,     midTexW, st, s.texW, s.texH);
        blit(ctx, t, x + dl, farYs,  midWs, db, sl,    farYt, midTexW, sb, s.texW, s.texH);
        blit(ctx, t, x,      y + dt, dl, midHs, 0,     st,    sl, midTexH, s.texW, s.texH);
        blit(ctx, t, farXs,  y + dt, dr, midHs, farXt, st,    sr, midTexH, s.texW, s.texH);
        blit(ctx, t, x + dl, y + dt, midWs, midHs, sl, st, midTexW, midTexH, s.texW, s.texH);
        } finally {
            if (alpha < 1f) RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
    }

    private static int scaled(int value, int textureSize, int baseSize) {
        if (value <= 0) return 0;
        return Math.min(textureSize / 2, Math.max(1, Math.round(value * (textureSize / (float) Math.max(1, baseSize)))));
    }

    private static int[] fitInsets(int start, int end, int available) {
        start = Math.max(0, start);
        end = Math.max(0, end);
        if (start + end <= available) return new int[]{start, end};
        if (start + end == 0) return new int[]{0, 0};
        float scale = available / (float) (start + end);
        int fittedStart = Math.max(0, Math.round(start * scale));
        return new int[]{fittedStart, Math.max(0, available - fittedStart)};
    }

    private static void blit(DrawContext ctx, Identifier t, int dx, int dy, int dw, int dh,
                             int u, int v, int rw, int rh, int tw, int th) {
        if (dw <= 0 || dh <= 0 || rw <= 0 || rh <= 0) return;
        ctx.drawTexture(t, dx, dy, dw, dh, (float) u, (float) v, rw, rh, tw, th);
    }
}
