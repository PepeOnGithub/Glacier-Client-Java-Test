package net.glacierclient;

import net.fabricmc.api.ClientModInitializer;
import net.glacierclient.core.config.ConfigManager;
import net.glacierclient.core.cosmetic.CosmeticManager;
import net.glacierclient.core.event.EventBus;
import net.glacierclient.core.module.ModuleManager;
import net.glacierclient.gui.ClickGUI;
import net.glacierclient.gui.notification.NotificationSystem;

public class GlacierClient implements ClientModInitializer {

    public static final String MOD_ID = "glacierclient";
    public static final String NAME = "Glacier Client";
    public static final String VERSION = "1.0.0";

    private static GlacierClient instance;

    private EventBus eventBus;
    private ModuleManager moduleManager;
    private ConfigManager configManager;
    private CosmeticManager cosmeticManager;
    private ClickGUI clickGUI;
    private NotificationSystem notificationSystem;

    @Override
    public void onInitializeClient() {
        instance = this;
        eventBus = new EventBus();
        configManager = new ConfigManager();
        moduleManager = new ModuleManager(eventBus);
        cosmeticManager = new CosmeticManager();
        notificationSystem = new NotificationSystem();
        clickGUI = new ClickGUI();
        configManager.load();
    }

    public static GlacierClient getInstance() { return instance; }
    public EventBus getEventBus() { return eventBus; }
    public ModuleManager getModuleManager() { return moduleManager; }
    public ConfigManager getConfigManager() { return configManager; }
    public CosmeticManager getCosmeticManager() { return cosmeticManager; }
    public ClickGUI getClickGUI() { return clickGUI; }
    public NotificationSystem getNotificationSystem() { return notificationSystem; }
}
