package net.glacierclient.modules.advanced.social;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import java.util.*;
public final class AntiSpamMod extends GlacierMod {
    private final NumberSetting threshold = new NumberSetting("Threshold", 3, 1, 10);
    private final Map<String, Integer> counts = new LinkedHashMap<>();
    public AntiSpamMod() {
        super("AntiSpam", "Collapses repeated identical chat messages", Category.QOL, -1);
        addSettings(threshold);
    }
    @EventListen
    public void onChat(ChatReceiveEvent event) {
        String msg = event.getMessage();
        counts.merge(msg, 1, Integer::sum);
        if (counts.get(msg) > (int) threshold.get()) event.setCancelled(true);
        if (counts.size() > 64) counts.remove(counts.keySet().iterator().next());
    }
}
