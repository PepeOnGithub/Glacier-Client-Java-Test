package net.glacierclient.core.profile;

import com.google.gson.*;
import net.glacierclient.core.config.ConfigManager;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class ProfileAutoSync {
    private static final Path PROFILES_DIR = Path.of("config/glacierclient/profiles");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final ConfigManager config;
    private String activeProfile = "default";

    public ProfileAutoSync(ConfigManager config) {
        this.config = config;
        try { Files.createDirectories(PROFILES_DIR); } catch (IOException ignored) {}
    }

    public List<String> listProfiles() {
        try {
            return Files.list(PROFILES_DIR)
                .filter(p -> p.toString().endsWith(".json"))
                .map(p -> p.getFileName().toString().replace(".json", ""))
                .sorted()
                .toList();
        } catch (IOException e) { return List.of(); }
    }

    public void saveProfile(String name) {
        try {
            Path out = PROFILES_DIR.resolve(name + ".json");
            config.save();
            Path src = Path.of("config/glacierclient/modules.json");
            if (Files.exists(src)) Files.copy(src, out, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) { System.err.println("[Glacier] Failed to save profile: " + name); }
    }

    public void loadProfile(String name) {
        try {
            Path src = PROFILES_DIR.resolve(name + ".json");
            if (!Files.exists(src)) return;
            Files.copy(src, Path.of("config/glacierclient/modules.json"), StandardCopyOption.REPLACE_EXISTING);
            config.load();
            this.activeProfile = name;
        } catch (IOException e) { System.err.println("[Glacier] Failed to load profile: " + name); }
    }

    public void deleteProfile(String name) {
        try { Files.deleteIfExists(PROFILES_DIR.resolve(name + ".json")); } catch (IOException ignored) {}
    }

    public String getActiveProfile() { return activeProfile; }
}
