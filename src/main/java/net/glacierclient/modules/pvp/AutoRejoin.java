package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;

public class AutoRejoin extends GlacierMod {

    private final BooleanSetting onKick = new BooleanSetting("On Kick", "Rejoin on kick", true);
    private final BooleanSetting onTimeout = new BooleanSetting("On Timeout", "Rejoin on timeout", true);
    private final NumberSetting delay = new NumberSetting("Delay", "Reconnect delay (ms)", 1000, 30000, 5000);
    private final NumberSetting maxAttempts = new NumberSetting("Max Attempts", "Max reconnect attempts", 1, 10, 3);

    private int attempts = 0;
    private long reconnectAt = 0;
    private boolean pending = false;
    private ServerInfo lastServer;

    public AutoRejoin() {
        super("Auto Rejoin", "Automatically rejoin last server on disconnect", Category.PVP);
        addSettings(onKick, onTimeout, delay, maxAttempts);
    }

    @Override
    public void onEnable() { attempts = 0; }

    @Override
    public void onDisable() { pending = false; }

    @Override
    public void onTick() {
        if (pending && System.currentTimeMillis() >= reconnectAt) {
            pending = false;
            // Reconnect logic handled by mixin/event hook
        }
    }

    public void onDisconnect(boolean kicked) {
        if (kicked && !onKick.getValue()) return;
        if (!kicked && !onTimeout.getValue()) return;
        if (attempts >= (int)(double) maxAttempts.getValue()) return;
        attempts++;
        pending = true;
        reconnectAt = System.currentTimeMillis() + (long) delay.getValue();
        lastServer = MinecraftClient.getInstance().getCurrentServerEntry();
    }

    public ServerInfo getLastServer() { return lastServer; }
    public boolean isPending() { return pending; }
}
