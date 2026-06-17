package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayDeque;
import java.util.Deque;

public class ResourcePackQuickSwitch extends GlacierMod {

    private final BooleanSetting showInHUD = new BooleanSetting("Show In HUD", "Show recent packs in HUD", false);
    private final NumberSetting maxRecent = new NumberSetting("Max Recent", "Max recent packs to remember", 1, 10, 5);
    private final BooleanSetting rememberPerServer = new BooleanSetting("Remember Per Server", "Remember pack per server", false);

    private final Deque<String> recentPacks = new ArrayDeque<>();

    public ResourcePackQuickSwitch() {
        super("Resource Pack Quick Switch", "Quickly switch between resource packs", Category.QOL);
        addSettings(showInHUD, maxRecent, rememberPerServer);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public void addRecent(String packName) {
        // Remember Per Server: namespace the entry by current server so packs are tracked per-server.
        String key = packName;
        if (rememberPerServer.getValue()) {
            var entry = MinecraftClient.getInstance().getCurrentServerEntry();
            if (entry != null) key = entry.address + "/" + packName;
        }
        recentPacks.remove(key);
        recentPacks.addFirst(key);
        while (recentPacks.size() > (int)(double) maxRecent.getValue()) recentPacks.removeLast();
    }

    public Deque<String> getRecentPacks() { return recentPacks; }
    public boolean isShowInHUD() { return showInHUD.getValue(); }
}
