package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class FastRenderHook extends GlacierMod {

    private final BooleanSetting fastEntity = new BooleanSetting("Fast Entity", "Optimize entity rendering", true);
    private final BooleanSetting fastBlock = new BooleanSetting("Fast Block", "Optimize block rendering", true);
    private final BooleanSetting fastParticle = new BooleanSetting("Fast Particle", "Optimize particle rendering", true);
    private final BooleanSetting fastCloud = new BooleanSetting("Fast Cloud", "Optimize cloud rendering", true);
    private final NumberSetting renderBatchSize = new NumberSetting("Render Batch Size", "Entities per render batch", 1, 64, 8);

    public FastRenderHook() {
        super("Fast Render Hook", "Optimize rendering pipeline for better FPS", Category.PERFORMANCE);
        addSettings(fastEntity, fastBlock, fastParticle, fastCloud, renderBatchSize);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isFastEntity() { return fastEntity.getValue(); }
    public boolean isFastBlock() { return fastBlock.getValue(); }
    public boolean isFastParticle() { return fastParticle.getValue(); }
    public boolean isFastCloud() { return fastCloud.getValue(); }
    public int getRenderBatchSize() { return (int)(double) renderBatchSize.getValue(); }
}
