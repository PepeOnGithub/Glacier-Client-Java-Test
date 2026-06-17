package net.glacierclient.modules.advanced.social;

import net.glacierclient.core.event.EventListen;
import net.glacierclient.core.event.events.ChatReceiveEvent;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.settings.StringSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Combined chat enhancement module. Merges what used to be five separate modules into one with
 * per-feature sub-toggles: Timestamps, keyword Filter, Mention highlight, Anti-Spam collapsing and
 * incoming-message Sounds. (Message edits / highlight / cancel are applied by MixinChatHud.)
 */
public final class BetterChatMod extends GlacierMod {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // History / scrolling (cosmetic spine kept from the old BetterChat)
    private final BooleanSetting infiniteHistory = new BooleanSetting("Infinite History", "Keep a very large chat history", true);
    private final NumberSetting history = new NumberSetting("History Size", "Lines of chat history to keep", 200, 50, 1000);
    private final BooleanSetting smooth = new BooleanSetting("Smooth Scroll", "Smooth chat scrolling", true);

    // Timestamps (was ChatTimestamp)
    private final BooleanSetting timestamps = new BooleanSetting("Timestamps", "Prepend a [HH:mm] timestamp", false);
    private final BooleanSetting brackets = new BooleanSetting("Timestamp Brackets", "Wrap the timestamp in brackets", true);

    // Keyword filter (was ChatFilter)
    private final BooleanSetting filter = new BooleanSetting("Filter", "Hide messages containing keywords", false);
    private final StringSetting keywords = new StringSetting("Filter Keywords", "Comma-separated keywords", "spam,ad,buy,sell");

    // Mention highlight (was MentionHighlight)
    private final BooleanSetting mentions = new BooleanSetting("Highlight Mentions", "Highlight messages that mention you", true);
    private final BooleanSetting mentionSound = new BooleanSetting("Mention Sound", "Play a sound on mention", true);

    // Anti-spam (was AntiSpam)
    private final BooleanSetting antiSpam = new BooleanSetting("Anti Spam", "Collapse repeated identical messages", false);
    private final NumberSetting spamThreshold = new NumberSetting("Spam Threshold", "Repeats allowed before hiding", 3, 1, 10);

    // Incoming sound (was ChatSounds)
    private final BooleanSetting chatSounds = new BooleanSetting("Chat Sounds", "Play a sound on each message", false);
    private final NumberSetting soundVolume = new NumberSetting("Sound Volume", "Chat sound volume", 0.5, 0.0, 1.0);

    private final Map<String, Integer> spamCounts = new LinkedHashMap<>();

    public BetterChatMod() {
        super("Better Chat", "All-in-one chat: timestamps, filter, mention highlight, anti-spam & sounds", Category.QOL, -1);
        addSettings(infiniteHistory, history, smooth,
                timestamps, brackets,
                filter, keywords,
                mentions, mentionSound,
                antiSpam, spamThreshold,
                chatSounds, soundVolume);
    }

    @EventListen
    public void onChat(ChatReceiveEvent event) {
        // Keyword filter — drop the message entirely.
        if (filter.get()) {
            String lower = event.getMessage().toLowerCase();
            for (String kw : keywords.get().split(",")) {
                String k = kw.trim().toLowerCase();
                if (!k.isEmpty() && lower.contains(k)) { event.setCancelled(true); return; }
            }
        }

        // Anti-spam — collapse identical repeats past the threshold.
        if (antiSpam.get()) {
            String msg = event.getMessage();
            spamCounts.merge(msg, 1, Integer::sum);
            if (spamCounts.get(msg) > (int) spamThreshold.get()) { event.setCancelled(true); return; }
            if (spamCounts.size() > 64) spamCounts.remove(spamCounts.keySet().iterator().next());
        }

        MinecraftClient mc = MinecraftClient.getInstance();

        // Mention highlight + optional ping.
        if (mentions.get() && mc.player != null) {
            String name = mc.player.getName().getString();
            if (event.getMessage().contains(name)) {
                event.setHighlightColor(GlacierTheme.ACCENT);
                if (mentionSound.get())
                    mc.player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
            }
        }

        // Timestamp prefix.
        if (timestamps.get()) {
            String ts = LocalTime.now().format(TIME_FMT);
            event.setMessage((brackets.get() ? "[" + ts + "] " : ts + " ") + event.getMessage());
        }

        // Incoming chat sound.
        if (chatSounds.get() && mc.player != null) {
            mc.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), (float) soundVolume.get(), 1.0f);
        }
    }

    public boolean isInfiniteHistory() { return infiniteHistory.get(); }
    public int getHistorySize() { return (int) history.get(); }
    public boolean isSmoothScroll() { return smooth.get(); }
}
