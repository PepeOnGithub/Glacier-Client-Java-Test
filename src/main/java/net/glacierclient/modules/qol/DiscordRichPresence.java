package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.StringSetting;
import net.minecraft.client.MinecraftClient;

public class DiscordRichPresence extends GlacierMod {

    private final BooleanSetting showServer = new BooleanSetting("Show Server", "Show current server in Discord", true);
    private final BooleanSetting showWorld = new BooleanSetting("Show World", "Show world name in Discord", false);
    private final BooleanSetting showTime = new BooleanSetting("Show Time", "Show elapsed time in Discord", true);
    private final BooleanSetting showParty = new BooleanSetting("Show Party", "Show party info in Discord", false);
    private final StringSetting customStatus = new StringSetting("Custom Status", "Custom Discord status", "");

    private long startTime;

    public DiscordRichPresence() {
        super("Discord Rich Presence", "Show Minecraft status in Discord", Category.QOL);
        addSettings(showServer, showWorld, showTime, showParty, customStatus);
    }

    @Override
    public void onEnable() {
        startTime = System.currentTimeMillis();
        updatePresence();
    }

    @Override
    public void onDisable() {
        // Clear Discord presence
    }

    @Override
    public void onTick() {
        updatePresence();
    }

    private void updatePresence() {
        MinecraftClient mc = MinecraftClient.getInstance();
        StringBuilder state = new StringBuilder();
        if (!customStatus.getValue().isEmpty()) {
            state.append(customStatus.getValue());
        } else {
            state.append("Playing Minecraft");
            if (showServer.getValue() && mc.getCurrentServerEntry() != null) {
                state.append(" on ").append(mc.getCurrentServerEntry().address);
            }
        }
        // Discord IPC update handled via native library
    }
}
