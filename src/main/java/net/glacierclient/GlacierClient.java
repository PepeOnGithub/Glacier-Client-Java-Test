package net.glacierclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.glacierclient.core.config.ConfigManager;
import net.glacierclient.core.cosmetic.CosmeticManager;
import net.glacierclient.core.event.EventBus;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.ModuleManager;
import net.glacierclient.gui.ClickGUI;
import net.glacierclient.gui.notification.NotificationSystem;
import net.glacierclient.modules.render.Zoom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

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

    private KeyBinding openGuiKey;
    private KeyBinding zoomKey;

    // zoom state
    private boolean zooming;
    private int savedFov;
    // module keybind edge-detection
    private final Set<GlacierMod> keyDown = new HashSet<>();

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

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.glacierclient.open_gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.glacierclient"));
        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.glacierclient.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "category.glacierclient"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) clickGUI.open();
            handleModuleKeybinds(client);
            handleZoom(client);
        });
    }

    /** Toggle modules whose user-assigned key was just pressed (ignored while a screen is open). */
    private void handleModuleKeybinds(MinecraftClient client) {
        if (client.currentScreen != null) { keyDown.clear(); return; }
        long win = client.getWindow().getHandle();
        for (GlacierMod m : moduleManager.getModules()) {
            int k = m.getKeybind();
            if (k < 0) continue;
            boolean down = InputUtil.isKeyPressed(win, k);
            boolean was = keyDown.contains(m);
            if (down && !was) m.toggle();
            if (down) keyDown.add(m); else keyDown.remove(m);
        }
    }

    /** Hold-to-zoom: lower FOV while the zoom key is held (server-safe, restores on release). */
    private void handleZoom(MinecraftClient client) {
        if (client.options == null) return;
        Zoom zoom = moduleManager.getModule(Zoom.class);
        boolean held = zoom != null && zoom.isEnabled() && zoomKey.isPressed() && client.currentScreen == null;
        if (held) {
            if (!zooming) { savedFov = client.options.getFov().getValue(); zooming = true; }
            int target = Math.max(1, (int) Math.round(savedFov / zoom.getDivisor()));
            client.options.getFov().setValue(target);
        } else if (zooming) {
            client.options.getFov().setValue(savedFov);
            zooming = false;
        }
    }

    public static GlacierClient getInstance() { return instance; }
    public EventBus getEventBus() { return eventBus; }
    public ModuleManager getModuleManager() { return moduleManager; }
    public ConfigManager getConfigManager() { return configManager; }
    public CosmeticManager getCosmeticManager() { return cosmeticManager; }
    public ClickGUI getClickGUI() { return clickGUI; }
    public NotificationSystem getNotificationSystem() { return notificationSystem; }
}
