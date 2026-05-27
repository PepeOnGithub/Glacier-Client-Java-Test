package net.glacierclient.modules.engine;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class FastChestRender extends GlacierMod {

    private final BooleanSetting bakedWhenClosed = new BooleanSetting("Baked When Closed", "Replace closed chests with static baked models", true);
    private final BooleanSetting bakedBarrels = new BooleanSetting("Baked Barrels", "Also bake closed barrels", false);
    private final BooleanSetting bakedShulkers = new BooleanSetting("Baked Shulkers", "Also bake closed shulker boxes", false);
    private final NumberSetting bakeDistance = new NumberSetting("Bake Distance", "Maximum distance (blocks) to apply baked models", 32, 8, 64);
    private final BooleanSetting keepLidAnimation = new BooleanSetting("Keep Lid Animation", "Retain lid open/close animation when opening", false);

    public FastChestRender() {
        super("Fast Chest Render", "Replace tile entity chests with static baked models when closed", Category.ENGINE);
        addSettings(bakedWhenClosed, bakedBarrels, bakedShulkers, bakeDistance, keepLidAnimation);
    }

    @Override
    public void onEnable() {
        // Register baked model override for chest tile entities
    }

    @Override
    public void onDisable() {
        // Restore default chest tile entity renderer
    }

    public boolean isBakedWhenClosed() { return bakedWhenClosed.getValue(); }
    public boolean isBakedBarrels() { return bakedBarrels.getValue(); }
    public boolean isBakedShulkers() { return bakedShulkers.getValue(); }
    public int getBakeDistance() { return (int) bakeDistance.getValue(); }
    public boolean isKeepLidAnimation() { return keepLidAnimation.getValue(); }
}
