package net.glacierclient.core.setting;

public class StringSetting extends net.glacierclient.core.settings.StringSetting {
    public StringSetting(String name, String description, String defaultValue) { super(name, description, defaultValue); }
    public StringSetting(String name, String description, String defaultValue, int maxLength) { super(name, description, defaultValue, maxLength); }
}
