package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class SoundPan3D extends GlacierMod {

    private final NumberSetting stereoWidth = new NumberSetting("Stereo Width", "Stereo widening factor", 0.5, 3.0, 1.5);
    private final BooleanSetting enhancedSpatial = new BooleanSetting("Enhanced Spatial", "Enhanced 3D spatial audio", true);
    private final NumberSetting rolloffFactor = new NumberSetting("Rolloff", "Sound distance rolloff factor", 0.5, 5.0, 1.0);

    public SoundPan3D() {
        super("3D Sound", "Enhanced stereo positioning for game sounds", Category.RENDER);
        addSettings(stereoWidth, enhancedSpatial, rolloffFactor);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
    @Override public void onTick() {}

    public float getStereoWidth() { return (float)(double) stereoWidth.getValue(); }
    public float getRolloffFactor() { return (float)(double) rolloffFactor.getValue(); }
    public boolean isEnhancedSpatial() { return enhancedSpatial.getValue(); }

    public float[] processStereo(float left, float right) {
        float width = (float)(double) stereoWidth.getValue();
        float mid = (left + right) * 0.5f;
        float side = (right - left) * 0.5f * width;
        return new float[]{mid - side, mid + side};
    }
}
