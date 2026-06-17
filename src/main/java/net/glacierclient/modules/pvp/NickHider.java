package net.glacierclient.modules.pvp;

import net.glacierclient.core.event.EventListen;
import net.glacierclient.core.event.events.ChatReceiveEvent;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.StringSetting;
import net.minecraft.client.MinecraftClient;

import java.util.Random;

public class NickHider extends GlacierMod {

    private final StringSetting nickname = new StringSetting("Nickname", "Display nickname", "GlacierPlayer");
    private final BooleanSetting hideInTablist = new BooleanSetting("Hide In Tablist", "Hide real name in tab list", true);
    private final BooleanSetting hideInChat = new BooleanSetting("Hide In Chat", "Hide real name in chat", true);
    private final BooleanSetting randomize = new BooleanSetting("Randomize", "Use random nickname", false);

    private static final String[] RANDOM_NAMES = {"Steve", "Alex", "Notch", "Herobrine", "Player"};
    private String currentNick;

    public NickHider() {
        super("Nick Hider", "Display a custom username locally", Category.PVP);
        addSettings(nickname, hideInTablist, hideInChat, randomize);
    }

    @Override
    public void onEnable() {
        if (randomize.getValue()) {
            currentNick = RANDOM_NAMES[new Random().nextInt(RANDOM_NAMES.length)];
        } else {
            currentNick = nickname.getValue();
        }
    }

    @Override
    public void onDisable() { currentNick = null; }

    @Override
    public void onTick() {}

    /** Replaces the player's real name with the nickname in incoming chat (applied by MixinChatHud). */
    @EventListen
    public void onChat(ChatReceiveEvent event) {
        if (!hideInChat.getValue()) return;
        String real = MinecraftClient.getInstance().getSession().getUsername();
        String nick = getDisplayName();
        if (real == null || real.isEmpty() || nick == null || nick.isEmpty() || nick.equals(real)) return;
        if (event.getMessage().contains(real)) {
            event.setMessage(event.getMessage().replace(real, nick));
        }
    }

    public String getDisplayName() { return currentNick != null ? currentNick : nickname.getValue(); }
    public boolean isHideInTablist() { return hideInTablist.getValue(); }
    public boolean isHideInChat() { return hideInChat.getValue(); }
}
