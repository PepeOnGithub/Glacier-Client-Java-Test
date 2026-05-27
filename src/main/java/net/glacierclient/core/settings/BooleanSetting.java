package net.glacierclient.core.settings;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, String description, boolean defaultValue) {
        super(name, description, defaultValue);
    }

    public void toggle() { setValue(!value); }

    @Override
    public String getTypeName() { return "boolean"; }
}
