package net.glacierclient.core.module.hud;

import net.glacierclient.api.bridge.VersionBridge;
import net.glacierclient.core.module.CoreCategory;
import net.glacierclient.core.module.CoreModule;

public class MemoryHud extends CoreModule {
    public MemoryHud() { super("Memory", "JVM heap usage", CoreCategory.HUD, false, 4, 64); }

    @Override
    public void render(VersionBridge b, Object ctx) {
        long used = b.getUsedMemoryBytes() / (1024 * 1024);
        long max = b.getMaxMemoryBytes() / (1024 * 1024);
        int pct = max > 0 ? (int) (used * 100 / max) : 0;
        text(b, ctx, "Mem: " + used + "/" + max + "MB (" + pct + "%)", 0xFFFFFFFF);
    }
}
