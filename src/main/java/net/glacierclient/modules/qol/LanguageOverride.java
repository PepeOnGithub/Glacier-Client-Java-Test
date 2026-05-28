package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.StringSetting;
import net.minecraft.client.MinecraftClient;

public class LanguageOverride extends GlacierMod {

    private final StringSetting language = new StringSetting("Language", "Language code to use (e.g. en_us)", "en_us");
    private final BooleanSetting applyToChat = new BooleanSetting("Apply To Chat", "Apply override to chat messages", false);
    private final BooleanSetting autoDetect = new BooleanSetting("Auto Detect", "Auto-detect system language", false);

    public LanguageOverride() {
        super("Language Override", "Override the game language", Category.QOL);
        addSettings(language, applyToChat, autoDetect);
    }

    @Override
    public void onEnable() {
        if (autoDetect.getValue()) {
            String sysLang = java.util.Locale.getDefault().toString().toLowerCase().replace('_', '_');
            // Language switching via LanguageManager
        }
    }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public String getLanguage() { return language.getValue(); }
    public boolean isApplyToChat() { return applyToChat.getValue(); }
}
