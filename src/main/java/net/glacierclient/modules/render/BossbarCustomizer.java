package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class BossbarCustomizer extends GlacierMod {

    private final BooleanSetting hideBossbar = new BooleanSetting("Hide Bossbar", "Completely hide bossbar", false);
    private final NumberSetting posX = new NumberSetting("Position X", "Horizontal position %", 0, 100, 50);
    private final NumberSetting posY = new NumberSetting("Position Y", "Vertical position %", 0, 50, 5);
    private final NumberSetting scale = new NumberSetting("Scale", "Bossbar scale", 0.5, 2.0, 1.0);
    private final BooleanSetting showOnlyText = new BooleanSetting("Show Only Text", "Hide bar, show only title", false);

    public BossbarCustomizer() {
        super("Bossbar Customizer", "Customize bossbar position and visibility", Category.RENDER);
        addSettings(hideBossbar, posX, posY, scale, showOnlyText);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isBossbarHidden() { return hideBossbar.getValue(); }
    public float getPosX(int width) { return width * (float) posX.getValue() / 100f; }
    public float getPosY(int height) { return height * (float) posY.getValue() / 100f; }
    public float getScale() { return (float) scale.getValue(); }
    public boolean isOnlyText() { return showOnlyText.getValue(); }
}
