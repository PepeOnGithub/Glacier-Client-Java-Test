package net.glacierclient.core.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Draws whole PNG sprites (e.g. the {@code common/} UI glyphs) scaled to any size, regardless of the
 * source resolution. Texture dimensions are read once from the PNG header (IHDR) and cached; absent
 * textures return false so callers can fall back.
 *
 * <p>{@code path} is relative to {@code assets/glacierclient/textures/} without the extension,
 * e.g. {@code "common/settings"} or {@code "common/arrow/arrow_left"}.</p>
 */
public final class Sprites {

    private Sprites() {}

    private static final Map<String, int[]> DIMS = new HashMap<>();   // path -> {w,h}, or absent entry == not loaded
    private static final Map<String, Identifier> IDS = new HashMap<>();

    private static Identifier id(String path) {
        return IDS.computeIfAbsent(path, p -> new Identifier("glacierclient", "textures/" + p + ".png"));
    }

    private static int[] dims(String path) {
        if (DIMS.containsKey(path)) return DIMS.get(path);
        int[] d = null;
        try {
            Optional<Resource> r = MinecraftClient.getInstance().getResourceManager().getResource(id(path));
            if (r.isPresent()) {
                try (InputStream in = r.get().getInputStream()) {
                    byte[] b = in.readNBytes(24); // 8 sig + 4 len + 4 "IHDR" + 4 w + 4 h
                    if (b.length >= 24) {
                        int w = ((b[16] & 0xFF) << 24) | ((b[17] & 0xFF) << 16) | ((b[18] & 0xFF) << 8) | (b[19] & 0xFF);
                        int h = ((b[20] & 0xFF) << 24) | ((b[21] & 0xFF) << 16) | ((b[22] & 0xFF) << 8) | (b[23] & 0xFF);
                        if (w > 0 && h > 0 && w < 10000 && h < 10000) d = new int[]{w, h};
                    }
                }
            }
        } catch (Exception ignored) {}
        DIMS.put(path, d);
        return d;
    }

    public static boolean has(String path) { return dims(path) != null; }

    /** Draw the whole texture scaled into the rect. Returns false (drawing nothing) if absent. */
    public static boolean draw(DrawContext ctx, String path, int x, int y, int w, int h) {
        int[] d = dims(path);
        if (d == null) return false;
        ctx.drawTexture(id(path), x, y, w, h, 0f, 0f, d[0], d[1], d[0], d[1]);
        return true;
    }

    public static boolean drawCentered(DrawContext ctx, String path, int cx, int cy, int size) {
        return draw(ctx, path, cx - size / 2, cy - size / 2, size, size);
    }
}
