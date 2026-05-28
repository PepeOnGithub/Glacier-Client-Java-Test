package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;

import java.util.HashMap;
import java.util.Map;

public class KeybindConfigurationMatrix extends GlacierMod {

    private final BooleanSetting showConflicts = new BooleanSetting("Show Conflicts", "Highlight conflicting keybinds", true);
    private final BooleanSetting allowMultiple = new BooleanSetting("Allow Multiple", "Allow multiple mods on one key", false);
    private final BooleanSetting showInGUI = new BooleanSetting("Show In GUI", "Show keybinds in module GUI", true);

    private final Map<Integer, String> keyMap = new HashMap<>();

    public KeybindConfigurationMatrix() {
        super("Keybind Matrix", "Manage and visualize all keybindings", Category.QOL);
        addSettings(showConflicts, allowMultiple, showInGUI);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean hasConflict(int keyCode) {
        if (!showConflicts.getValue()) return false;
        return keyMap.containsKey(keyCode) && !allowMultiple.getValue();
    }

    public void registerKey(int keyCode, String modName) {
        keyMap.put(keyCode, modName);
    }

    public Map<Integer, String> getKeyMap() { return keyMap; }
}
