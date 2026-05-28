package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class CapePhysicsEditor extends GlacierMod {

    private final NumberSetting gravity = new NumberSetting("Gravity", "Cape gravity multiplier", 0.0, 3.0, 1.0);
    private final NumberSetting wind = new NumberSetting("Wind", "Cape wind effect", 0.0, 3.0, 1.0);
    private final NumberSetting stiffness = new NumberSetting("Stiffness", "Cape cloth stiffness", 0.0, 1.0, 0.3);
    private final BooleanSetting fancyPhysics = new BooleanSetting("Fancy Physics", "Enable fancy cape simulation", true);

    private float[] capeVelocities = new float[18]; // 6 joints x 3 axes
    private float[] capePositions = new float[18];

    public CapePhysicsEditor() {
        super("Cape Physics", "Customize cape cloth simulation settings", Category.RENDER);
        addSettings(gravity, wind, stiffness, fancyPhysics);
    }

    @Override public void onEnable() {
        capeVelocities = new float[18];
        capePositions = new float[18];
    }

    @Override public void onDisable() {}

    @Override
    public void onTick() {
        if (!fancyPhysics.getValue()) return;
        float g = (float)(double) gravity.getValue();
        float w = (float)(double) wind.getValue();
        float s = (float)(double) stiffness.getValue();
        for (int i = 0; i < 6; i++) {
            capeVelocities[i * 3 + 1] -= g * 0.01f;
            capeVelocities[i * 3] += (float) (Math.sin(System.currentTimeMillis() * 0.001) * w * 0.005);
            for (int j = 0; j < 3; j++) {
                capeVelocities[i * 3 + j] *= (1 - s);
                capePositions[i * 3 + j] += capeVelocities[i * 3 + j];
            }
        }
    }

    public float[] getCapePositions() { return capePositions; }
    public float getGravity() { return (float)(double) gravity.getValue(); }
    public float getWind() { return (float)(double) wind.getValue(); }
}
