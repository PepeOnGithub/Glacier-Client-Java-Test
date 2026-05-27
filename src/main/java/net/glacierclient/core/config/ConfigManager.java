package net.glacierclient.core.config;

import com.google.gson.*;
import net.glacierclient.GlacierClient;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.settings.*;

import java.io.*;
import java.nio.file.*;

public class ConfigManager {

    private static final Path CONFIG_DIR = Paths.get("config", "glacierclient");
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("modules.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void save() {
        try {
            Files.createDirectories(CONFIG_DIR);
            JsonObject root = new JsonObject();
            for (GlacierMod mod : GlacierClient.getInstance().getModuleManager().getModules()) {
                JsonObject modObj = new JsonObject();
                modObj.addProperty("enabled", mod.isEnabled());
                modObj.addProperty("keybind", mod.getKeybind());
                JsonObject settingsObj = new JsonObject();
                for (Setting<?> setting : mod.getSettings()) {
                    settingsObj.addProperty(setting.getName(), String.valueOf(setting.getValue()));
                }
                modObj.add("settings", settingsObj);
                root.add(mod.getName(), modObj);
            }
            Files.writeString(CONFIG_FILE, gson.toJson(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (!Files.exists(CONFIG_FILE)) return;
        try {
            String content = Files.readString(CONFIG_FILE);
            JsonObject root = JsonParser.parseString(content).getAsJsonObject();
            for (GlacierMod mod : GlacierClient.getInstance().getModuleManager().getModules()) {
                if (!root.has(mod.getName())) continue;
                JsonObject modObj = root.getAsJsonObject(mod.getName());
                if (modObj.has("enabled")) mod.setEnabled(modObj.get("enabled").getAsBoolean());
                if (modObj.has("keybind")) mod.setKeybind(modObj.get("keybind").getAsInt());
                if (modObj.has("settings")) {
                    JsonObject settingsObj = modObj.getAsJsonObject("settings");
                    for (Setting<?> setting : mod.getSettings()) {
                        if (!settingsObj.has(setting.getName())) continue;
                        applySettingValue(setting, settingsObj.get(setting.getName()).getAsString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void applySettingValue(Setting<?> setting, String value) {
        try {
            if (setting instanceof BooleanSetting s) s.setValue(Boolean.parseBoolean(value));
            else if (setting instanceof NumberSetting s) s.setValue(Double.parseDouble(value));
            else if (setting instanceof ModeSetting s) s.setValue(value);
            else if (setting instanceof StringSetting s) s.setValue(value);
            else if (setting instanceof ColorSetting s) s.setValue(Integer.parseInt(value));
        } catch (Exception ignored) {}
    }
}
