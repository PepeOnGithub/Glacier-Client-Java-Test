package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.StringSetting;

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

    public String getDisplayName() { return currentNick != null ? currentNick : nickname.getValue(); }
    public boolean isHideInTablist() { return hideInTablist.getValue(); }
    public boolean isHideInChat() { return hideInChat.getValue(); }
}
