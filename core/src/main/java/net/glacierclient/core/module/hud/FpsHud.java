package net.glacierclient.core.module.hud;

import net.glacierclient.api.bridge.VersionBridge;
import net.glacierclient.core.client.GlacierCore;
import net.glacierclient.core.module.CoreCategory;
import net.glacierclient.core.module.CoreModule;

public class FpsHud extends CoreModule {
    public FpsHud() { super("FPS", "Frames per second", CoreCategory.HUD, true, 4, 52); }

    @Override
    public void render(VersionBridge b, Object ctx) {
        int fps = b.getCurrentFPS();
        if (fps <= 0) fps = GlacierCore.get().getFps(); // fallback: core-measured
        text(b, ctx, "FPS: " + fps, 0xFFFFFFFF);
    }
}
