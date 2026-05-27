package net.glacierclient.core.module;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.event.EventTarget;
import net.glacierclient.core.settings.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class GlacierMod implements EventTarget {

    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;
    private int keybind;
    private final List<Setting<?>> settings = new ArrayList<>();

    protected GlacierMod(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
        this.keybind = -1;
    }

    protected void addSettings(Setting<?>... newSettings) {
        settings.addAll(Arrays.asList(newSettings));
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) {
            GlacierClient.getInstance().getEventBus().subscribe(this);
            onEnable();
        } else {
            GlacierClient.getInstance().getEventBus().unsubscribe(this);
            onDisable();
        }
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public int getKeybind() { return keybind; }
    public void setKeybind(int keybind) { this.keybind = keybind; }
    public List<Setting<?>> getSettings() { return settings; }

    @SuppressWarnings("unchecked")
    public <T> Setting<T> getSetting(String name) {
        return (Setting<T>) settings.stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
