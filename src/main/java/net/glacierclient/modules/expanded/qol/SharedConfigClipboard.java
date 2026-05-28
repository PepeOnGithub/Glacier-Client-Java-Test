package net.glacierclient.modules.expanded.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;

public class SharedConfigClipboard extends GlacierMod {

    private final BooleanSetting includeKeybind = new BooleanSetting("Include Keybind", "Include keybind in exported config string", false);
    private final BooleanSetting copyOnExport = new BooleanSetting("Copy On Export", "Automatically copy config string to clipboard", true);
    private final BooleanSetting confirmOnImport = new BooleanSetting("Confirm On Import", "Ask for confirmation before applying imported config", true);

    public SharedConfigClipboard() {
        super("Config Clipboard", "Export/import single mod settings as share string", Category.QOL);
        addSettings(includeKeybind, copyOnExport, confirmOnImport);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public boolean isIncludeKeybind() { return includeKeybind.getValue(); }
    public boolean isCopyOnExport() { return copyOnExport.getValue(); }
    public boolean isConfirmOnImport() { return confirmOnImport.getValue(); }
}
