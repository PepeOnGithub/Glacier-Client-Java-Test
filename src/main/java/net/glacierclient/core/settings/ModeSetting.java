package net.glacierclient.core.settings;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {

    private final List<String> modes;

    public ModeSetting(String name, String description, String defaultMode, String... modes) {
        super(name, description, defaultMode);
        this.modes = new java.util.ArrayList<>(Arrays.asList(modes));
        if (!this.modes.contains(defaultMode)) {
            this.modes.add(0, defaultMode);
        }
    }


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
