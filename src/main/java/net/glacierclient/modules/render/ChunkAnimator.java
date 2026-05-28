package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;

public class ChunkAnimator extends GlacierMod {

    private final ModeSetting style = new ModeSetting("Style", "Animation style", "Up", "Up", "Down", "Fade");
    private final NumberSetting speed = new NumberSetting("Speed", "Animation speed", 0.1, 5.0, 1.0);

    public ChunkAnimator() {
        super("Chunk Animator", "Animate chunks sliding in when loading", Category.RENDER);
        addSettings(style, speed);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public String getAnimationStyle() { return style.getValue(); }
    public float getAnimationSpeed() { return (float)(double) speed.getValue(); }
}
