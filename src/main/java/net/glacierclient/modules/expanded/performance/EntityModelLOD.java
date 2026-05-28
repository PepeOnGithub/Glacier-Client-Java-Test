package net.glacierclient.modules.expanded.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class EntityModelLOD extends GlacierMod {

    private final NumberSetting lodDistance = new NumberSetting("LOD Distance", "Distance at which to simplify entity models", 32, 8, 128);
    private final NumberSetting lodDetail = new NumberSetting("LOD Detail", "LOD detail level for simplified models", 2, 1, 4);
    private final BooleanSetting affectPlayers = new BooleanSetting("Affect Players", "Simplify distant player models", false);
    private final BooleanSetting affectMobs = new BooleanSetting("Affect Mobs", "Simplify distant mob models", false);
    private final BooleanSetting smoothTransition = new BooleanSetting("Smooth Transition", "Blend between LOD levels", false);

    public EntityModelLOD() {
        super("Entity LOD", "Simplified mob/player models at configurable distance", Category.PERFORMANCE);
        addSettings(lodDistance, lodDetail, affectPlayers, affectMobs, smoothTransition);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public int getLodDistance() { return (int)(double) lodDistance.getValue(); }
    public int getLodDetail() { return (int)(double) lodDetail.getValue(); }
    public boolean isAffectPlayers() { return affectPlayers.getValue(); }
    public boolean isAffectMobs() { return affectMobs.getValue(); }
    public boolean isSmoothTransition() { return smoothTransition.getValue(); }
}
