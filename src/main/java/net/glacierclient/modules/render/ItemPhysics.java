package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class ItemPhysics extends GlacierMod {

    private final BooleanSetting rotation = new BooleanSetting("Rotation", "Enable item rotation", true);
    private final BooleanSetting bounce = new BooleanSetting("Bounce", "Enable item bouncing", true);
    private final NumberSetting rotationSpeed = new NumberSetting("Rotation Speed", "Speed of item rotation", 0.1, 5.0, 1.0);

    public ItemPhysics() {
        super("Item Physics", "Makes dropped items rotate and bounce realistically", Category.RENDER);
        addSettings(rotation, bounce, rotationSpeed);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isRotationEnabled() { return rotation.getValue(); }
    public boolean isBounceEnabled() { return bounce.getValue(); }
    public float getRotationSpeed() { return (float) rotationSpeed.getValue(); }
}
