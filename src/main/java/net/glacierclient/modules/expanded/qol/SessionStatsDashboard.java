package net.glacierclient.modules.expanded.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;

public class SessionStatsDashboard extends GlacierMod {

    private final BooleanSetting showKills = new BooleanSetting("Show Kills", "Display session kill count", false);
    private final BooleanSetting showDeaths = new BooleanSetting("Show Deaths", "Display session death count", false);
    private final BooleanSetting showMining = new BooleanSetting("Show Mining", "Display blocks mined this session", false);
    private final BooleanSetting showChat = new BooleanSetting("Show Chat", "Display messages sent this session", false);
    private final BooleanSetting resetOnDisconnect = new BooleanSetting("Reset On Disconnect", "Reset all stats on server disconnect", true);

    public SessionStatsDashboard() {
        super("Session Stats", "Expandable overlay of session statistics", Category.QOL);
        addSettings(showKills, showDeaths, showMining, showChat, resetOnDisconnect);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public boolean isShowKills() { return showKills.getValue(); }
    public boolean isShowDeaths() { return showDeaths.getValue(); }
    public boolean isShowMining() { return showMining.getValue(); }
    public boolean isShowChat() { return showChat.getValue(); }
    public boolean isResetOnDisconnect() { return resetOnDisconnect.getValue(); }
}
