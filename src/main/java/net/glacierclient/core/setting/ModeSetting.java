package net.glacierclient.core.setting;

public class ModeSetting extends net.glacierclient.core.settings.ModeSetting {
    public ModeSetting(String name, String description, String defaultMode, String... modes) { super(name, description, defaultMode, modes); }
    public ModeSetting(String name, String defaultMode, String... modes) { super(name, defaultMode, modes); }
}
