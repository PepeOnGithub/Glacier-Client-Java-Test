package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class ParticlesMultiplier extends GlacierMod {

    private final NumberSetting multiplier = new NumberSetting("Multiplier", "Particle density multiplier", 0.0, 5.0, 1.0);
    private final BooleanSetting affectCrit = new BooleanSetting("Affect Crit", "Apply to critical hit particles", true);
    private final BooleanSetting affectSweep = new BooleanSetting("Affect Sweep", "Apply to sweep attack particles", true);
    private final BooleanSetting affectBlock = new BooleanSetting("Affect Block", "Apply to block break particles", true);

    public ParticlesMultiplier() {
        super("Particles Multiplier", "Control particle density in the world", Category.PVP);
        addSettings(multiplier, affectCrit, affectSweep, affectBlock);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public float getMultiplier(String type) {
        if ("crit".equals(type) && !affectCrit.getValue()) return 1f;
        if ("sweep".equals(type) && !affectSweep.getValue()) return 1f;
        if ("block".equals(type) && !affectBlock.getValue()) return 1f;
        return (float) multiplier.getValue();
    }
}
