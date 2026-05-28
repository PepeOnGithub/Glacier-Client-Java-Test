package net.glacierclient.core.setting;

public class NumberSetting extends net.glacierclient.core.settings.NumberSetting {
    public NumberSetting(String name, String description, double min, double max, double defaultValue) { super(name, description, min, max, defaultValue); }
    public NumberSetting(String name, double defaultValue, double min, double max) { super(name, defaultValue, min, max); }
    public NumberSetting(String name, String description, double defaultValue) { super(name, description, defaultValue); }
    public NumberSetting(String name, double defaultValue) { super(name, defaultValue); }
}
