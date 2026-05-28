package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class ItemScaleModifier extends GlacierMod {

    private final NumberSetting firstPersonScale = new NumberSetting("1st Person Scale", "Item scale in first person", 0.1, 3.0, 1.0);
    private final NumberSetting thirdPersonScale = new NumberSetting("3rd Person Scale", "Item scale in third person", 0.1, 3.0, 1.0);
    private final BooleanSetting separateHands = new BooleanSetting("Separate Hands", "Apply separate scale per hand", false);

    public ItemScaleModifier() {
        super("Item Scale", "Scale held items in first/third person view", Category.RENDER);
        addSettings(firstPersonScale, thirdPersonScale, separateHands);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
    @Override public void onTick() {}

    public float getFirstPersonScale() { return (float)(double) firstPersonScale.getValue(); }
    public float getThirdPersonScale() { return (float)(double) thirdPersonScale.getValue(); }
    public boolean isSeparateHands() { return separateHands.getValue(); }
}
