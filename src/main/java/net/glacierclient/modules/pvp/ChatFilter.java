package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.StringSetting;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ChatFilter extends GlacierMod {

    private final BooleanSetting filterProfanity = new BooleanSetting("Filter Profanity", "Block common profanity", true);
    private final BooleanSetting filterPromotion = new BooleanSetting("Filter Promotion", "Block server/store promotions", false);
    private final StringSetting customKeywords = new StringSetting("Custom Keywords", "Comma-separated blocked words", "");
    private final BooleanSetting hideFiltered = new BooleanSetting("Hide Filtered", "Hide messages with blocked words", true);
    private final StringSetting replacement = new StringSetting("Replacement", "Replace blocked words with", "[filtered]");

    private static final Set<String> PROFANITY = new HashSet<>(Arrays.asList(
        "badword1", "badword2" // placeholder - real list would be populated
    ));

    public ChatFilter() {
        super("Chat Filter", "Block or replace toxic messages in chat", Category.PVP);
        addSettings(filterProfanity, filterPromotion, customKeywords, hideFiltered, replacement);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public String processMessage(String msg) {
        String lower = msg.toLowerCase();
        if (filterProfanity.getValue()) {
            for (String word : PROFANITY) {
                if (lower.contains(word)) {
                    if (hideFiltered.getValue()) return null;
                    msg = msg.replaceAll("(?i)" + word, replacement.getValue());
                }
            }
        }
        String custom = customKeywords.getValue();
        if (!custom.isEmpty()) {
            for (String kw : custom.split(",")) {
                kw = kw.trim();
                if (!kw.isEmpty() && lower.contains(kw.toLowerCase())) {
                    if (hideFiltered.getValue()) return null;
                    msg = msg.replaceAll("(?i)" + kw, replacement.getValue());
                }
            }
        }
        return msg;
    }
}
