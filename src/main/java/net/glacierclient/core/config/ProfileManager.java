package net.glacierclient.core.config;

import java.util.ArrayList;
import java.util.List;

public class ProfileManager {

    private final List<String> profiles = new ArrayList<>();
    private String activeProfile = "Default";

    public ProfileManager() {
        profiles.add("Default");
        profiles.add("PvP");
        profiles.add("Recording");
        profiles.add("Performance");
    }

    public List<String> getProfiles() { return profiles; }
    public String getActiveProfile() { return activeProfile; }

    public void createProfile(String name) {
        if (!profiles.contains(name)) profiles.add(name);
    }

    public void deleteProfile(String name) {
        if (name.equals("Default")) return;
        profiles.remove(name);
        if (activeProfile.equals(name)) activeProfile = "Default";
    }

    public void switchProfile(String name) {
        if (profiles.contains(name)) activeProfile = name;
    }

    public void duplicateProfile(String source, String target) {
        if (profiles.contains(source) && !profiles.contains(target)) {
            profiles.add(target);
        }
    }
}
