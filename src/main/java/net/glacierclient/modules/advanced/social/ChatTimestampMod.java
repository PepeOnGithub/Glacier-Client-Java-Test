package net.glacierclient.modules.advanced.social;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
public final class ChatTimestampMod extends GlacierMod {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");
    private final BooleanSetting brackets = new BooleanSetting("Brackets", true);
    public ChatTimestampMod() {
        super("ChatTimestamp", "Prepends timestamp to chat messages", Category.QOL, -1);
        addSettings(brackets);
    }
    @EventListen
    public void onChat(ChatReceiveEvent event) {
        String ts = LocalTime.now().format(FMT);
        String prefix = brackets.get() ? "[" + ts + "] " : ts + " ";
        event.setMessage(prefix + event.getMessage());
    }
}
