package net.glacierclient.core.settings;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, String description, boolean defaultValue) {
        super(name, description, defaultValue);
    }

    public BooleanSetting(String name, boolean defaultValue) {
        super(name, "", defaultValue);
    }

    public void toggle() { setValue(!value); }
    public boolean get() { return value; }

    @Override
    public String getTypeName() { return "boolean"; }
}
