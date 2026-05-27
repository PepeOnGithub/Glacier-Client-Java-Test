package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;

public class RefreshRateSelector extends GlacierMod {

    private final NumberSetting targetRate = new NumberSetting("Target Rate", "Target refresh rate", 30, 360, 144);
    private final BooleanSetting allowHigher = new BooleanSetting("Allow Higher", "Allow FPS above target", false);
    private final BooleanSetting adaptiveSync = new BooleanSetting("Adaptive Sync", "Enable adaptive sync (G-Sync/FreeSync)", false);

    public RefreshRateSelector() {
        super("Refresh Rate Selector", "Control target refresh rate and sync options", Category.PERFORMANCE);
        addSettings(targetRate, allowHigher, adaptiveSync);
    }

    @Override
    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options != null) {
            mc.options.getMaxFps().setValue((int) targetRate.getValue());
        }
    }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public int getTargetRate() { return (int) targetRate.getValue(); }
    public boolean isAllowHigher() { return allowHigher.getValue(); }
    public boolean isAdaptiveSync() { return adaptiveSync.getValue(); }
}
