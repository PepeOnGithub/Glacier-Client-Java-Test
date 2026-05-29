package net.glacierclient.core.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Optional PNG icons for module cards. Drop a texture at
 * {@code assets/glacierclient/textures/icons/<slug>.png} (slug = lower-cased module name with
 * non-alphanumerics replaced by '_') and it will be used automatically; otherwise the caller falls
 * back to the vector {@link Icons}. Presence is resolved once per slug and cached.
 */
public final class IconTextures {

    private IconTextures() {}

    private static final Map<String, Identifier> CACHE = new HashMap<>();
    private static final Map<String, Boolean> PRESENT = new HashMap<>();

    private static String slug(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]+", "_").replaceAll("^_|_$", "");
    }

    /** @return true if an image icon exists and was drawn (centred on cx,cy at the given size). */
    public static boolean draw(DrawContext ctx, String name, int cx, int cy, int size) {
        String slug = slug(name);
        Boolean present = PRESENT.get(slug);
        Identifier id = CACHE.get(slug);
        if (present == null) {
            id = new Identifier("glacierclient", "textures/icons/" + slug + ".png");
            MinecraftClient mc = MinecraftClient.getInstance();
            present = mc.getResourceManager().getResource(id).isPresent();
            CACHE.put(slug, id);
            PRESENT.put(slug, present);
        }
        if (!present) return false;
        ctx.drawTexture(id, cx - size / 2, cy - size / 2, 0, 0, size, size, size, size);
        return true;
    }
}
