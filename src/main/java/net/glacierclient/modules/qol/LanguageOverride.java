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

    private String previous;

    public LanguageOverride() {
        super("Language Override", "Override the game language", Category.QOL);
        addSettings(language, applyToChat, autoDetect);
    }

    private void applyLanguage(String code) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.getLanguageManager() == null || code == null || code.isEmpty()) return;
        try {
            mc.getLanguageManager().setLanguage(code);
            mc.options.language = code;
            mc.reloadResources();
        } catch (Throwable ignored) {}
    }

    @Override
    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.getLanguageManager() == null) return;
        previous = mc.getLanguageManager().getLanguage();
        String code = autoDetect.getValue()
            ? java.util.Locale.getDefault().toString().toLowerCase()
            : language.getValue();
        applyLanguage(code);
    }

    @Override
    public void onDisable() {
        if (previous != null) applyLanguage(previous);
    }

    @Override
    public void onTick() {}

    public String getLanguage() { return language.getValue(); }
    public boolean isApplyToChat() { return applyToChat.getValue(); }
}
