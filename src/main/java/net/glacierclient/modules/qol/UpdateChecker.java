package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class UpdateChecker extends GlacierMod {

    private final BooleanSetting autoCheck = new BooleanSetting("Auto Check", "Automatically check for updates", true);
    private final NumberSetting checkInterval = new NumberSetting("Check Interval", "Hours between checks", 1, 168, 24);
    private final BooleanSetting showNotification = new BooleanSetting("Show Notification", "Show update notification", true);
    private final BooleanSetting autoDownload = new BooleanSetting("Auto Download", "Auto-download updates", false);

    private long lastCheck = 0;
    private String latestVersion = null;

    public UpdateChecker() {
        super("Update Checker", "Check for Glacier Client updates", Category.QOL);
        addSettings(autoCheck, checkInterval, showNotification, autoDownload);
    }

    @Override
    public void onEnable() {
        if (autoCheck.getValue()) checkForUpdates();
    }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        if (!autoCheck.getValue()) return;
        long now = System.currentTimeMillis();
        if (now - lastCheck > checkInterval.getValue() * 3_600_000L) {
            lastCheck = now;
            checkForUpdates();
        }
    }

    public void checkForUpdates() {
        // HTTP check via thread to avoid blocking main thread
        new Thread(() -> {
            try {
                // Fetch latest version from API
                lastCheck = System.currentTimeMillis();
                // Notify player if update found
            } catch (Exception ignored) {}
        }, "GlacierUpdateChecker").start();
    }
}
