package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

import java.util.ArrayList;
import java.util.List;

public class ProfileManagerMod extends GlacierMod {

    private final NumberSetting maxProfiles = new NumberSetting("Max Profiles", "Maximum number of profiles", 1, 20, 10);
    private final BooleanSetting autoSave = new BooleanSetting("Auto Save", "Automatically save profile changes", true);
    private final BooleanSetting cloudSync = new BooleanSetting("Cloud Sync", "Sync profiles to cloud", false);

    private final List<String> profiles = new ArrayList<>();
    private String currentProfile = "Default";

    public ProfileManagerMod() {
        super("Profile Manager", "Manage multiple configuration profiles", Category.QOL);
        addSettings(maxProfiles, autoSave, cloudSync);
    }

    @Override
    public void onEnable() {
        if (profiles.isEmpty()) profiles.add("Default");
    }

    @Override
    public void onDisable() {
        if (autoSave.getValue()) saveCurrentProfile();
    }

    @Override
    public void onTick() {}

    public void saveCurrentProfile() {
        // Profile save logic handled by config system
    }

    public void loadProfile(String name) {
        if (profiles.contains(name)) currentProfile = name;
    }

    public void addProfile(String name) {
        if (profiles.size() < (int) maxProfiles.getValue() && !profiles.contains(name)) {
            profiles.add(name);
        }
    }

    public List<String> getProfiles() { return profiles; }
    public String getCurrentProfile() { return currentProfile; }
}
