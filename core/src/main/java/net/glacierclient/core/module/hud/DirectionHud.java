package net.glacierclient.core.module.hud;

import net.glacierclient.api.bridge.VersionBridge;
import net.glacierclient.core.module.CoreCategory;
import net.glacierclient.core.module.CoreModule;

public class DirectionHud extends CoreModule {
    private static final String[] DIRS = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};

    public DirectionHud() { super("Direction", "Facing direction", CoreCategory.HUD, true, 4, 28); }

    @Override
    public void render(VersionBridge b, Object ctx) {
        if (!b.isInGame()) return;
        float yaw = b.getYaw() % 360f;
        if (yaw < 0) yaw += 360f;
        String dir = DIRS[Math.round(yaw / 45f) & 7];
        text(b, ctx, "Facing: " + dir, 0xFFFFFFFF);
    }
}
