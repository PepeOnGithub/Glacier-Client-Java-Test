package net.glacierclient.core.module.hud;

import net.glacierclient.api.bridge.VersionBridge;
import net.glacierclient.core.module.CoreCategory;
import net.glacierclient.core.module.CoreModule;

public class WatermarkHud extends CoreModule {
    public WatermarkHud() { super("Watermark", "Glacier brand + version", CoreCategory.HUD, true, 4, 4); }

    @Override
    public void render(VersionBridge b, Object ctx) {
        b.drawText(ctx, "Glacier", x, y, 0xFF7289DA, true);
        int w = (int) b.getTextWidth("Glacier ");
        b.drawText(ctx, b.getMinecraftVersion(), x + w, y, 0xFFFFFFFF, true);
    }
}
