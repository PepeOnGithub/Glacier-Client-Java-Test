package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;

public class StreamingMode extends GlacierMod {

    private final BooleanSetting hideServer = new BooleanSetting("Hide Server", "Hide server IP when streaming", true);
    private final BooleanSetting hideCoords = new BooleanSetting("Hide Coords", "Hide coordinates when streaming", true);
    private final BooleanSetting hideInventory = new BooleanSetting("Hide Inventory", "Blur inventory when streaming", false);
    private final BooleanSetting hideFriends = new BooleanSetting("Hide Friends", "Hide friend list when streaming", true);
    private final BooleanSetting muteVoice = new BooleanSetting("Mute Voice", "Mute voice chat when streaming", false);

    public StreamingMode() {
        super("Streaming Mode", "Hide sensitive info for streams/recordings", Category.QOL);
        addSettings(hideServer, hideCoords, hideInventory, hideFriends, muteVoice);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isHideServer() { return hideServer.getValue(); }
    public boolean isHideCoords() { return hideCoords.getValue(); }
    public boolean isHideInventory() { return hideInventory.getValue(); }
    public boolean isHideFriends() { return hideFriends.getValue(); }
    public boolean isMuteVoice() { return muteVoice.getValue(); }
}
