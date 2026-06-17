package net.glacierclient.core.hud;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.module.GlacierMod;

import java.util.List;

public class HUDEditor {

    private final List<HUDProfile> profiles;
    private HUDProfile activeProfile;
    private boolean undoBufferDirty = false;
    private final java.util.Deque<HUDProfile> undoStack = new java.util.ArrayDeque<>();
    private final java.util.Deque<HUDProfile> redoStack = new java.util.ArrayDeque<>();
    private final List<HUDMod> activeHudMods = new java.util.ArrayList<>();

    public HUDEditor() {
        profiles = new java.util.ArrayList<>();
        activeProfile = new HUDProfile("Default");
        profiles.add(activeProfile);
    }

    public List<HUDMod> getActiveHUDMods() {
        activeHudMods.clear();
        for (GlacierMod mod : GlacierClient.getInstance().getModuleManager().getModules()) {
            if (mod instanceof HUDMod hud && hud.isEnabled()) activeHudMods.add(hud);
        }
        return activeHudMods;
    }

    public void saveSnapshot() {
        undoStack.push(deepCopy(activeProfile));
        redoStack.clear();
        undoBufferDirty = true;
    }

    public void undo() {
        if (undoStack.isEmpty()) return;
        redoStack.push(deepCopy(activeProfile));
        applyProfile(undoStack.pop());
    }

    public void redo() {
        if (redoStack.isEmpty()) return;
        undoStack.push(deepCopy(activeProfile));
        applyProfile(redoStack.pop());
    }

    private void applyProfile(HUDProfile profile) {
        for (GlacierMod mod : GlacierClient.getInstance().getModuleManager().getModules()) {
            if (!(mod instanceof HUDMod hud)) continue;
            HUDProfile.HUDElementData data = profile.elements.get(hud.getName());
            if (data == null) continue;
            hud.setX(data.x);
            hud.setY(data.y);
            hud.setScale(data.scale);
            hud.setVisible(data.visible);
        }
    }

    private HUDProfile deepCopy(HUDProfile source) {
        HUDProfile copy = new HUDProfile(source.name);
        source.elements.forEach((k, v) ->
                copy.elements.put(k, new HUDProfile.HUDElementData(v.x, v.y, v.scale, v.visible)));
        return copy;
    }

    public void addProfile(String name) {
        profiles.add(new HUDProfile(name));
    }

    public void switchProfile(String name) {
        profiles.stream().filter(p -> p.name.equals(name)).findFirst().ifPresent(p -> activeProfile = p);
    }

    public List<HUDProfile> getProfiles() { return profiles; }
    public HUDProfile getActiveProfile() { return activeProfile; }
}
