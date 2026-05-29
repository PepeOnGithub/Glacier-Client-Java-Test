package net.glacierclient.core.settings;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {

    private final List<String> modes;

    public ModeSetting(String name, String description, String defaultMode, String... modes) {
        super(name, description, defaultMode);
        this.modes = Arrays.asList(modes);
        if (!this.modes.contains(defaultMode)) {
            throw new IllegalArgumentException("Default mode must be in the modes list");
        }
    }

    public ModeSetting(String name, String description, String[] modes, String defaultMode) {
        this(name, description, defaultMode, modes);
    }

    public ModeSetting(String name, List<String> modes, String defaultMode) {
        this(name, "", defaultMode, modes.toArray(new String[0]));
    }

    public String get() { return value; }

    public void cycle() {
        int idx = modes.indexOf(value);
        setValue(modes.get((idx + 1) % modes.size()));
    }

    public void cyclePrev() {
        int idx = modes.indexOf(value);
        setValue(modes.get((idx - 1 + modes.size()) % modes.size()));
    }

    public boolean is(String mode) { return value.equalsIgnoreCase(mode); }
    public List<String> getModes() { return modes; }

    @Override
    public String getTypeName() { return "mode"; }
}
