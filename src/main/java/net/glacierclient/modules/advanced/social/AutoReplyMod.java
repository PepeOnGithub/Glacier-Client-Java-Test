package net.glacierclient.modules.advanced.social;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
import net.glacierclient.core.event.*;
import net.minecraft.client.MinecraftClient;
public final class AutoReplyMod extends GlacierMod {
    private final BooleanSetting onlyWhisper = new BooleanSetting("OnlyWhispers", true);
    private final StringSetting message = new StringSetting("Message", "AFK right now");
    public AutoReplyMod() {
        super("AutoReply", "Automatically replies to whispers when AFK", Category.QOL, -1);
        addSettings(onlyWhisper, message);
    }
    @EventListen
    public void onChat(EventChat event) {
        if (onlyWhisper.get() && !event.getMessage().startsWith("/msg")) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) mc.player.networkHandler.sendChatMessage(message.get());
    }
}
