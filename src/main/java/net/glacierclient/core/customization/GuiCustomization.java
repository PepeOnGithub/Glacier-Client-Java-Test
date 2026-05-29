package net.glacierclient.core.customization;

import com.google.gson.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds per-module {@link CardStyle} customization plus a global accent colour, and persists it to
 * {@code config/glacierclient/gui.json}. Singleton — access via {@link #get()}.
 */
public final class GuiCustomization {

    private static final Path FILE = Paths.get("config", "glacierclient", "gui.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Lazily created so the static FILE/GSON constants are guaranteed initialized first.
    private static GuiCustomization instance;
    public static GuiCustomization get() {
        if (instance == null) instance = new GuiCustomization();
        return instance;
    }

    private final Map<String, CardStyle> styles = new HashMap<>();
    public int globalAccent = 0xFF7289DA;

    private GuiCustomization() { load(); }

    /** Returns the style for a module, creating a default one seeded with the global accent if absent. */
    public CardStyle styleFor(String moduleName) {
        return styles.computeIfAbsent(moduleName, k -> {
            CardStyle s = new CardStyle();
            s.accentColor = globalAccent;
            return s;
        });
    }

    public void resetStyle(String moduleName) { styles.remove(moduleName); }

    public void load() {
        if (!Files.exists(FILE)) return;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();
            if (root.has("globalAccent")) globalAccent = root.get("globalAccent").getAsInt();
            if (root.has("cards")) {
                JsonObject cards = root.getAsJsonObject("cards");
                for (String key : cards.keySet()) {
                    JsonObject o = cards.getAsJsonObject(key);
                    CardStyle s = new CardStyle();
                    if (o.has("bgColor")) s.bgColor = o.get("bgColor").getAsInt();
                    if (o.has("bgColor2")) s.bgColor2 = o.get("bgColor2").getAsInt();
                    if (o.has("accentColor")) s.accentColor = o.get("accentColor").getAsInt();
                    if (o.has("radius")) s.radius = o.get("radius").getAsInt();
                    if (o.has("style")) {
                        try { s.style = CardStyle.Style.valueOf(o.get("style").getAsString()); }
                        catch (IllegalArgumentException ignored) {}
                    }
                    styles.put(key, s);
                }
            }
        } catch (Exception ignored) {}
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            JsonObject root = new JsonObject();
            root.addProperty("globalAccent", globalAccent);
            JsonObject cards = new JsonObject();
            for (Map.Entry<String, CardStyle> e : styles.entrySet()) {
                CardStyle s = e.getValue();
                JsonObject o = new JsonObject();
                o.addProperty("bgColor", s.bgColor);
                o.addProperty("bgColor2", s.bgColor2);
                o.addProperty("accentColor", s.accentColor);
                o.addProperty("radius", s.radius);
                o.addProperty("style", s.style.name());
                cards.add(e.getKey(), o);
            }
            root.add("cards", cards);
            Files.writeString(FILE, GSON.toJson(root));
        } catch (IOException ignored) {}
    }
}
