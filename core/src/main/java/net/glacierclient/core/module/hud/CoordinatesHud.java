package net.glacierclient.core.module.hud;

import net.glacierclient.api.bridge.VersionBridge;
import net.glacierclient.core.module.CoreCategory;
import net.glacierclient.core.module.CoreModule;

public class CoordinatesHud extends CoreModule {
    public CoordinatesHud() { super("Coordinates", "Player XYZ position", CoreCategory.HUD, true, 4, 16); }

    @Override
    public void render(VersionBridge b, Object ctx) {
        if (!b.isInGame()) return;
        String s = String.format("XYZ: %.1f, %.1f, %.1f", b.getX(), b.getY(), b.getZ());
        text(b, ctx, s, 0xFFFFFFFF);
    }
}
