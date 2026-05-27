package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class ModelRenderingCache extends GlacierMod {

    private final BooleanSetting cachePlayerModels = new BooleanSetting("Cache Player Models", "Cache player model renders", true);
    private final BooleanSetting cacheMobModels = new BooleanSetting("Cache Mob Models", "Cache mob model renders", true);
    private final NumberSetting cacheLifetime = new NumberSetting("Cache Lifetime", "Cache entry lifetime (seconds)", 1, 300, 30);

    public ModelRenderingCache() {
        super("Model Rendering Cache", "Cache entity model renders for performance", Category.PERFORMANCE);
        addSettings(cachePlayerModels, cacheMobModels, cacheLifetime);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isCachePlayerModels() { return cachePlayerModels.getValue(); }
    public boolean isCacheMobModels() { return cacheMobModels.getValue(); }
    public int getCacheLifetime() { return (int) cacheLifetime.getValue(); }
}
