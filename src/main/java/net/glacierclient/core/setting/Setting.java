package net.glacierclient.core.setting;

// Alias for legacy import path. New code should use net.glacierclient.core.settings.Setting.
public abstract class Setting<T> extends net.glacierclient.core.settings.Setting<T> {
    protected Setting(String name, String description, T defaultValue) { super(name, description, defaultValue); }
}
