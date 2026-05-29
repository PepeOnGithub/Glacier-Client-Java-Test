package net.glacierclient.core.module.hud;

import net.glacierclient.api.bridge.VersionBridge;
import net.glacierclient.core.module.CoreCategory;
import net.glacierclient.core.module.CoreModule;

public class SpeedHud extends CoreModule {
    public SpeedHud() { super("Speed", "Horizontal movement speed (blocks/s)", CoreCategory.HUD, true, 4, 40); }

    @Override
    public void render(VersionBridge b, Object ctx) {
        if (!b.isInGame()) return;
        double bps = b.getHorizontalSpeed() * 20.0; // per-tick → per-second
        text(b, ctx, String.format("Speed: %.2f m/s", bps), 0xFFFFFFFF);
    }
}
