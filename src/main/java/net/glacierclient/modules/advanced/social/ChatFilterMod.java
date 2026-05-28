package net.glacierclient.modules.advanced.social;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
import net.glacierclient.core.event.*;
public final class ChatFilterMod extends GlacierMod {
    private final StringSetting keywords = new StringSetting("Keywords", "spam,ad,buy,sell");
    private final BooleanSetting hideFiltered = new BooleanSetting("HideFiltered", true);
    public ChatFilterMod() {
        super("ChatFilter", "Filters unwanted chat messages by keyword", Category.QOL, -1);
        addSettings(keywords, hideFiltered);
    }
    @EventListen
    public void onChat(EventChat event) {
        String msg = event.getMessage().toLowerCase();
        for (String kw : keywords.get().split(",")) {
            if (msg.contains(kw.trim())) { event.setCancelled(hideFiltered.get()); return; }
        }
    }
}
