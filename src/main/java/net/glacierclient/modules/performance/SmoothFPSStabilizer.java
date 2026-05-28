package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class SmoothFPSStabilizer extends GlacierMod {

    private final NumberSetting targetFPS = new NumberSetting("Target FPS", "Target frames per second", 30, 300, 60);
    private final BooleanSetting vsync = new BooleanSetting("VSync", "Enable vertical sync", false);
    private final NumberSetting sleepError = new NumberSetting("Sleep Error", "Sleep error tolerance (ms)", 0, 5, 1);

    public SmoothFPSStabilizer() {
        super("Smooth FPS Stabilizer", "Stabilize FPS for smoother gameplay", Category.PERFORMANCE);
        addSettings(targetFPS, vsync, sleepError);
    }

    @Override
    public void onEnable() {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.options != null) mc.options.getEnableVsync().setValue(vsync.getValue());
    }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public int getTargetFPS() { return (int) targetFPS.getValue(); }
    public boolean isVsync() { return vsync.getValue(); }
    public int getSleepError() { return (int) sleepError.getValue(); }
}
