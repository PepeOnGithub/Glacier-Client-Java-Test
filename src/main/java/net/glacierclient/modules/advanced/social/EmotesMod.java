package net.glacierclient.modules.advanced.social;
import net.glacierclient.core.module.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import java.util.*;
public final class EmotesMod extends GlacierMod {
    private static final Map<String, String> EMOTES = Map.of(
            ":)", "☺", ":(", "☹", "<3", "♥", ":D", "😄", ":wave:", "👋",
            ":gg:", "🎮", ":fire:", "🔥", ":ice:", "❄", ":star:", "⭐"
    );
    public EmotesMod() { super("Emotes", "Converts text emotes to unicode characters in chat", Category.QOL, -1); }
    @EventListen
    public void onChatSend(ChatSendEvent event) {
        String msg = event.getMessage();
        for (Map.Entry<String, String> e : EMOTES.entrySet()) msg = msg.replace(e.getKey(), e.getValue());
        event.setMessage(msg);
    }
}
