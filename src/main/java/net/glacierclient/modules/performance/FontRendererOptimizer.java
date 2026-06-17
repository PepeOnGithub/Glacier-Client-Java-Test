package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class FontRendererOptimizer extends GlacierMod {

    private final BooleanSetting useCache = new BooleanSetting("Use Cache", "Cache rendered glyphs", true);
    private final NumberSetting cacheSize = new NumberSetting("Cache Size", "Glyph cache size", 64, 4096, 512);
    private final BooleanSetting antiAlias = new BooleanSetting("Anti-Alias", "Enable font anti-aliasing", true);
    private final BooleanSetting smoothRendering = new BooleanSetting("Smooth Rendering", "Smooth font rendering", true);

    private boolean savedUnicode = false;

    public FontRendererOptimizer() {
        super("Font Renderer Optimizer", "Disables the heavy unicode font for faster text rendering", Category.PERFORMANCE);
        addSettings(useCache, cacheSize, antiAlias, smoothRendering);
    }

    @Override
    public void onEnable() {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.options != null) {
            savedUnicode = mc.options.getForceUnicodeFont().getValue();
            if (smoothRendering.getValue()) mc.options.getForceUnicodeFont().setValue(false);
        }
    }

    @Override
    public void onDisable() {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.options != null) mc.options.getForceUnicodeFont().setValue(savedUnicode);
    }

    @Override
    public void onTick() {}

    public boolean isUseCache() { return useCache.getValue(); }
    public int getCacheSize() { return (int)(double) cacheSize.getValue(); }
    public boolean isAntiAlias() { return antiAlias.getValue(); }
    public boolean isSmoothRendering() { return smoothRendering.getValue(); }
}
