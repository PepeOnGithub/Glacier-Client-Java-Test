package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.StringSetting;
import net.minecraft.client.MinecraftClient;

public class NameProtect extends GlacierMod {

    private final StringSetting replacement = new StringSetting("Replacement", "Name to show instead of yours", "You");
    private final BooleanSetting hideInChat = new BooleanSetting("Hide In Chat", "Replace name in chat", true);
    private final BooleanSetting hideNametag = new BooleanSetting("Hide Nametag", "Replace nametag above player", true);

    public NameProtect() {
        super("Name Protect", "Hide your username in chat and world for recording", Category.RENDER);
        addSettings(replacement, hideInChat, hideNametag);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public String process(String text) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return text;
        String name = mc.player.getName().getString();
        return text.replace(name, replacement.getValue());
    }

    public boolean isHideInChat() { return hideInChat.getValue(); }
    public boolean isHideNametag() { return hideNametag.getValue(); }
}
