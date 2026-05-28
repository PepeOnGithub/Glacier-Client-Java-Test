package net.glacierclient.core.settings;

public class StringSetting extends Setting<String> {

    private final int maxLength;

    public StringSetting(String name, String description, String defaultValue) {
        this(name, description, defaultValue, 256);
    }

    public StringSetting(String name, String description, String defaultValue, int maxLength) {
        super(name, description, defaultValue);
        this.maxLength = maxLength;
    }

    @Override
    public void setValue(String value) {
        if (value != null && value.length() > maxLength) {
            super.setValue(value.substring(0, maxLength));
        } else {
            super.setValue(value);
        }
    }

    public int getMaxLength() { return maxLength; }

    @Override
    public String getTypeName() { return "string"; }
}
