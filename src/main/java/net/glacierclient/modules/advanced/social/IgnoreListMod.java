package net.glacierclient.modules.advanced.social;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import java.util.*;
public final class IgnoreListMod extends GlacierMod {
    private final StringSetting ignored = new StringSetting("Ignored", "");
    public IgnoreListMod() {
        super("IgnoreList", "Silences chat messages from specified players", Category.QOL, -1);
        addSettings(ignored);
    }
    @EventListen
    public void onChat(ChatReceiveEvent event) {
        String msg = event.getMessage();
        for (String name : ignored.get().split(",")) {
            if (!name.isBlank() && msg.startsWith("<" + name.trim() + ">")) { event.setCancelled(true); return; }
        }
    }
}
