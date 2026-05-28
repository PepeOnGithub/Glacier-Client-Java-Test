package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.minecraft.client.MinecraftClient;

public class QuickPlay extends GlacierMod {

    private final BooleanSetting closeOnJoin = new BooleanSetting("Close On Join", "Close menu when joining game", true);
    private final ModeSetting defaultGame = new ModeSetting("Default Game", "Default Hypixel game mode", "BedWars", "BedWars", "SkyWars", "Duels", "MurderMystery");

    private static final java.util.Map<String, String> COMMANDS = new java.util.HashMap<>();
    static {
        COMMANDS.put("BedWars", "/play bedwars_eight_one");
        COMMANDS.put("SkyWars", "/play skywars_solo_normal");
        COMMANDS.put("Duels", "/play duels_bridge_duel");
        COMMANDS.put("MurderMystery", "/play murder_classic");
    }

    public QuickPlay() {
        super("Quick Play", "Overlay for quickly joining Hypixel game modes", Category.PVP);
        addSettings(closeOnJoin, defaultGame);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public void joinGame(String game) {
        String cmd = COMMANDS.getOrDefault(game, "/lobby");
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) mc.player.networkHandler.sendChatMessage(cmd);
        if (closeOnJoin.getValue() && mc.currentScreen != null) mc.setScreen(null);
    }

    public String getDefaultGame() { return defaultGame.getValue(); }
}
