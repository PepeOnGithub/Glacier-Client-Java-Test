package net.glacierclient.modules.expanded.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class LODRenderer extends GlacierMod {

    private final NumberSetting lodDistance = new NumberSetting("LOD Distance", "Distance (blocks) at which LOD activates", 64, 32, 256);
    private final NumberSetting lodQuality = new NumberSetting("LOD Quality", "LOD detail level (1=lowest, 4=highest)", 2, 1, 4);
    private final BooleanSetting terrain = new BooleanSetting("Terrain", "Apply LOD to distant terrain chunks", false);
    private final BooleanSetting entities = new BooleanSetting("Entities", "Apply LOD to distant entities", false);
    private final NumberSetting fadeRange = new NumberSetting("Fade Range", "Transition range in blocks between LOD levels", 16, 8, 64);

    public LODRenderer() {
        super("LOD Renderer", "Low-poly distant terrain for reduced triangle count", Category.PERFORMANCE);
        addSettings(lodDistance, lodQuality, terrain, entities, fadeRange);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public int getLodDistance() { return (int) lodDistance.getValue(); }
    public int getLodQuality() { return (int) lodQuality.getValue(); }
    public boolean isTerrain() { return terrain.getValue(); }
    public boolean isEntities() { return entities.getValue(); }
    public int getFadeRange() { return (int) fadeRange.getValue(); }
}
