package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class CameraClipTweak extends GlacierMod {

    private final NumberSetting nearClip = new NumberSetting("Near Clip", "Near clip distance", 0.05, 1.0, 0.1);
    private final BooleanSetting smoothTransitions = new BooleanSetting("Smooth Transitions", "Smooth camera transitions", true);
    private final NumberSetting transitionSpeed = new NumberSetting("Transition Speed", "Camera transition speed", 0.1, 5.0, 1.5);

    private float currentNearClip;
    private float targetNearClip;

    public CameraClipTweak() {
        super("Camera Clip", "Adjust camera clip distance and behavior", Category.RENDER);
        addSettings(nearClip, smoothTransitions, transitionSpeed);
    }

    @Override
    public void onEnable() {
        currentNearClip = (float)(double) nearClip.getValue();
        targetNearClip = currentNearClip;
    }

    @Override public void onDisable() {}

    @Override
    public void onTick() {
        targetNearClip = (float)(double) nearClip.getValue();
        if (smoothTransitions.getValue()) {
            float lerpSpeed = (float) (transitionSpeed.getValue() * 0.1);
            currentNearClip += (targetNearClip - currentNearClip) * lerpSpeed;
        } else {
            currentNearClip = targetNearClip;
        }
    }

    public float getCurrentNearClip() { return currentNearClip; }
}
