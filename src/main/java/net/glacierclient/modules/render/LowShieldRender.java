package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class LowShieldRender extends GlacierMod {

    private final NumberSetting scale = new NumberSetting("Scale", "Shield render scale", 0.1, 1.0, 0.3);
    private final NumberSetting posY = new NumberSetting("Position Y", "Shield Y offset", 0.0, 1.0, 0.8);
    private final BooleanSetting hideCompletely = new BooleanSetting("Hide Completely", "Completely hide shield in hand", false);

    public LowShieldRender() {
        super("Low Shield Render", "Reduce shield visual obstruction in hand", Category.RENDER);
        addSettings(scale, posY, hideCompletely);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isHidden() { return hideCompletely.getValue(); }
    public float getScale() { return (float)(double) scale.getValue(); }
    public float getPosY() { return (float)(double) posY.getValue(); }
}
