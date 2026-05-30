package net.glacierclient.core.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Optional PNG backgrounds for GUI elements, drawn as a nine-slice so corners stay crisp at any size.
 *
 * <p>Drop square textures in {@code assets/glacierclient/textures/gui/<name>.png} (default 32×32 with
 * an 8px corner). Names used by the ClickGUI: {@code panel}, {@code card}, {@code card_hover},
 * {@code card_active}, {@code button}, {@code button_hover}, {@code tab}, {@code tab_active},
 * {@code popup}. When a texture is absent the caller falls back to the drawn rounded shapes, so the
 * GUI always works — it just upgrades automatically once you add art.</p>
 */
public final class GuiTextures {

    private GuiTextures() {}

    private static final int TEX = 32;     // assumed texture size (square)
    private static final int CORNER = 8;   // nine-slice corner size

    private static final Map<String, Identifier> IDS = new HashMap<>();
    private static final Map<String, Boolean> PRESENT = new HashMap<>();

    private static Identifier id(String name) {
        return IDS.computeIfAbsent(name, n -> new Identifier("glacierclient", "textures/gui/" + n + ".png"));
    }

    /** Whether a texture exists (cached). */
    public static boolean has(String name) {
        Boolean p = PRESENT.get(name);
        if (p == null) {
            p = MinecraftClient.getInstance().getResourceManager().getResource(id(name)).isPresent();
            PRESENT.put(name, p);
        }
        return p;
    }

    /** Nine-slice draw of {@code <name>.png} into the given rect (uses default 32px/8px corners). */
    public static void nineSlice(DrawContext ctx, String name, int x, int y, int w, int h) {
        nineSlice(ctx, name, x, y, w, h, TEX, CORNER);
    }

    public static void nineSlice(DrawContext ctx, String name, int x, int y, int w, int h, int tex, int corner) {
        if (w <= 0 || h <= 0) return;
        Identifier t = id(name);
        int c = Math.min(corner, Math.min(w, h) / 2);
        int far = tex - corner;            // far edge start (texture space)
        int midT = tex - 2 * corner;       // middle strip size (texture space)
        int midW = w - 2 * c;              // middle strip (screen space)
        int midH = h - 2 * c;

        // corners
        blit(ctx, t, x,        y,        c, c, 0,   0,   corner, corner, tex);
        blit(ctx, t, x + w - c, y,        c, c, far, 0,   corner, corner, tex);
        blit(ctx, t, x,        y + h - c, c, c, 0,   far, corner, corner, tex);
        blit(ctx, t, x + w - c, y + h - c, c, c, far, far, corner, corner, tex);
        // edges
        blit(ctx, t, x + c,     y,        midW, c,   corner, 0,   midT, corner, tex); // top
        blit(ctx, t, x + c,     y + h - c, midW, c,   corner, far, midT, corner, tex); // bottom
        blit(ctx, t, x,        y + c,     c, midH,    0,   corner, corner, midT, tex); // left
        blit(ctx, t, x + w - c, y + c,     c, midH,    far, corner, corner, midT, tex); // right
        // center
        blit(ctx, t, x + c,     y + c,     midW, midH, corner, corner, midT, midT, tex);
    }

    private static void blit(DrawContext ctx, Identifier t, int dx, int dy, int dw, int dh,
                             int u, int v, int rw, int rh, int tex) {
        if (dw <= 0 || dh <= 0) return;
        ctx.drawTexture(t, dx, dy, dw, dh, (float) u, (float) v, rw, rh, tex, tex);
    }
}
