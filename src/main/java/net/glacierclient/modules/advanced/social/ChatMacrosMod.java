package net.glacierclient.modules.advanced.social;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;
public final class ChatMacrosMod extends GlacierMod {
    private final StringSetting macro1 = new StringSetting("Macro1", "/home");
    private final StringSetting macro2 = new StringSetting("Macro2", "/spawn");
    private final StringSetting macro3 = new StringSetting("Macro3", "gg");
    public ChatMacrosMod() {
        super("ChatMacros", "Binds chat messages to configurable hotkeys", Category.QOL, -1);
        addSettings(macro1, macro2, macro3);
    }
    @EventListen
    public void onKey(KeyInputEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.currentScreen != null) return;
        if (event.getKey() == GLFW.GLFW_KEY_F6) send(mc, macro1.get());
        else if (event.getKey() == GLFW.GLFW_KEY_F7) send(mc, macro2.get());
        else if (event.getKey() == GLFW.GLFW_KEY_F8) send(mc, macro3.get());
    }
    private void send(MinecraftClient mc, String msg) {
        if (msg.startsWith("/")) mc.player.networkHandler.sendChatCommand(msg.substring(1));
        else mc.player.networkHandler.sendChatMessage(msg);
    }
}
