package net.glacierclient.core.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Optional PNG icons for module cards, from {@code assets/glacierclient/textures/icons/<file>.png}.
 *
 * <p>Resolution order for a module name: (1) an explicit {@link #ALIAS} mapping, (2) the
 * alphanumeric slug of the name (lower-cased, non-alphanumerics stripped, e.g. "FPS Display" ->
 * "fpsdisplay"). If neither texture exists the caller falls back to the vector {@link Icons}.</p>
 */
public final class IconTextures {

    private IconTextures() {}

    /** Module display name (alnum slug) -> icon file name (without extension). */
    // Every module now ships a dedicated <slug>.png (one unique icon per module), so resolution is
    // simply slug -> same-named file. This alias map stays empty unless a module needs to point at a
    // differently-named shared icon.
    private static final Map<String, String> ALIAS = new HashMap<>();

    private static final Map<String, Identifier> CACHE = new HashMap<>();
    private static final Map<String, Boolean> PRESENT = new HashMap<>();

    private static String slug(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    private static boolean present(String file, Identifier id) {
        Boolean p = PRESENT.get(file);
        if (p == null) {
            p = MinecraftClient.getInstance().getResourceManager().getResource(id).isPresent();
            PRESENT.put(file, p);
        }
        return p;
    }

    private static Identifier id(String file) {
        return CACHE.computeIfAbsent(file, f -> new Identifier("glacierclient", "textures/icons/" + f + ".png"));
    }

    /** @return true if an image icon exists for this module and was drawn (centred on cx,cy). */
    public static boolean draw(DrawContext ctx, String name, int cx, int cy, int size) {
        String slug = slug(name);
        String file = ALIAS.getOrDefault(slug, slug);
        Identifier id = id(file);
        if (!present(file, id)) return false;
        ctx.drawTexture(id, cx - size / 2, cy - size / 2, 0, 0, size, size, size, size);
        return true;
    }
}
