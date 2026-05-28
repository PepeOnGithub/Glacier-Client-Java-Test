package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class DynamicTextureUnloader extends GlacierMod {

    private final NumberSetting unloadDelay = new NumberSetting("Unload Delay", "Seconds before unloading unused textures", 5, 300, 30);
    private final BooleanSetting unloadOnDimension = new BooleanSetting("Unload On Dimension", "Unload textures on dimension change", true);
    private final NumberSetting maxTextureCache = new NumberSetting("Max Texture Cache", "Max cached textures", 64, 2048, 256);

    private long lastUnload = 0;

    public DynamicTextureUnloader() {
        super("Dynamic Texture Unloader", "Unload unused textures to free VRAM", Category.PERFORMANCE);
        addSettings(unloadDelay, unloadOnDimension, maxTextureCache);
    }

    @Override
    public void onEnable() { lastUnload = System.currentTimeMillis(); }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        long now = System.currentTimeMillis();
        if (now - lastUnload > unloadDelay.getValue() * 1000L) {
            lastUnload = now;
            // Texture unloading handled via mixin
        }
    }

    public boolean isUnloadOnDimension() { return unloadOnDimension.getValue(); }
    public int getMaxTextureCache() { return (int)(double) maxTextureCache.getValue(); }
}
