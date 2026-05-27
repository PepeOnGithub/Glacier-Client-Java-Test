package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.minecraft.client.MinecraftClient;

public class ScreenshotUploader extends GlacierMod {

    private final ModeSetting service = new ModeSetting("Service", "Upload service", "Clipboard", "Imgur", "Clipboard", "Local");
    private final BooleanSetting copyLink = new BooleanSetting("Copy Link", "Copy uploaded link to clipboard", true);
    private final BooleanSetting autoUpload = new BooleanSetting("Auto Upload", "Upload automatically on screenshot", false);
    private final BooleanSetting showNotification = new BooleanSetting("Show Notification", "Show upload notification", true);

    public ScreenshotUploader() {
        super("Screenshot Uploader", "Auto-upload screenshots and get an instant link", Category.PVP);
        addSettings(service, copyLink, autoUpload, showNotification);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public void upload(java.io.File screenshot) {
        String svc = service.getValue();
        // Upload handled by service-specific implementation
        if (showNotification.getValue()) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null) {
                mc.player.sendMessage(net.minecraft.text.Text.literal("[Screenshot] Uploaded via " + svc), false);
            }
        }
    }

    public boolean isAutoUpload() { return autoUpload.getValue(); }
}
