package net.glacierclient.core.module.hud;

import net.glacierclient.api.bridge.VersionBridge;
import net.glacierclient.core.module.CoreCategory;
import net.glacierclient.core.module.CoreModule;

public class PingHud extends CoreModule {
    public PingHud() { super("Ping", "Server latency (multiplayer)", CoreCategory.HUD, true, 4, 76); }

    @Override
    public void render(VersionBridge b, Object ctx) {
        if (!b.isInGame() || !b.isMultiplayer()) return;
        text(b, ctx, "Ping: " + b.getPing() + "ms", 0xFFFFFFFF);
    }
}
