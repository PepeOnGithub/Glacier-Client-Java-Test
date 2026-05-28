package net.glacierclient.modules.advanced.social;
import net.glacierclient.core.module.*;
import net.glacierclient.core.event.*;
import net.minecraft.client.MinecraftClient;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
public final class CopyMod extends GlacierMod {
    public CopyMod() { super("ChatCopy", "Shift+click to copy chat messages to clipboard", Category.QOL, -1); }
    @EventListen
    public void onChatClick(EventChatClick event) {
        if (!event.isShiftHeld()) return;
        String msg = event.getMessage();
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(msg), null);
        MinecraftClient.getInstance().player.sendMessage(net.minecraft.text.Text.literal("[Glacier] Copied to clipboard"), false);
    }
}
