package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.StringSetting;
import net.minecraft.client.MinecraftClient;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class ScreenshotFolderShortcut extends GlacierMod {

    private final BooleanSetting openOnScreenshot = new BooleanSetting("Open On Screenshot", "Open folder after screenshot", true);
    private final StringSetting customPath = new StringSetting("Custom Path", "Custom screenshot folder path", "");
    private final BooleanSetting autoOrganize = new BooleanSetting("Auto Organize", "Auto-organize screenshots by date", false);
    private final ModeSetting sortBy = new ModeSetting("Sort By", "Organization method", "Date", "Date", "World", "Server");

    public ScreenshotFolderShortcut() {
        super("Screenshot Folder Shortcut", "Quick access to screenshot folder", Category.QOL);
        addSettings(openOnScreenshot, customPath, autoOrganize, sortBy);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public void openFolder() {
        File folder;
        String custom = customPath.getValue();
        if (!custom.isEmpty()) {
            folder = new File(custom);
        } else {
            folder = new File(MinecraftClient.getInstance().runDirectory, "screenshots");
        }
        if (Desktop.isDesktopSupported()) {
            try { Desktop.getDesktop().open(folder); } catch (IOException e) { /* ignore */ }
        }
    }

    public boolean isOpenOnScreenshot() { return openOnScreenshot.getValue(); }
    public boolean isAutoOrganize() { return autoOrganize.getValue(); }
    public String getSortBy() { return sortBy.getValue(); }
}
