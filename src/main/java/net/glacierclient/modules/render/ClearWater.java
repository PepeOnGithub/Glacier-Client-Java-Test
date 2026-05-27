package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class ClearWater extends GlacierMod {

    private final NumberSetting visibility = new NumberSetting("Visibility", "Underwater visibility range", 1, 100, 100);
    private final BooleanSetting removeParticles = new BooleanSetting("Remove Particles", "Remove underwater particles", false);

    public ClearWater() {
        super("Clear Water", "Removes water fog for better underwater visibility", Category.RENDER);
        addSettings(visibility, removeParticles);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public float getVisibility() { return (float) visibility.getValue(); }
    public boolean shouldRemoveParticles() { return removeParticles.getValue(); }
}
