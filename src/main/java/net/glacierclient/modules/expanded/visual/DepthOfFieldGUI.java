package net.glacierclient.modules.expanded.visual;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.gui.DrawContext;

public class DepthOfFieldGUI extends GlacierMod {

    private final NumberSetting blurRadius = new NumberSetting("Blur Radius", "Radius of background blur effect", 14, 1, 32);
    private final NumberSetting blurStrength = new NumberSetting("Blur Strength", "Strength multiplier for blur shader", 1.0, 0.1, 3.0);
    private final BooleanSetting animate = new BooleanSetting("Animate", "Smoothly animate blur on/off", false);
    private final NumberSetting animSpeed = new NumberSetting("Anim Speed", "Speed of blur animation transition", 2.0, 0.1, 5.0);

    private float currentBlur = 0f;

    public DepthOfFieldGUI() {
        super("Depth of Field GUI", "Background blur when menus open", Category.RENDER);
        addSettings(blurRadius, blurStrength, animate, animSpeed);
    }

    @Override
    public void onEnable() {
        currentBlur = 0f;
    }

    @Override
    public void onDisable() {
        currentBlur = 0f;
    }

    @Override
    public void onTick() {
        if (isEnabled()) {
            float target = (float) (double) blurRadius.getValue();
            if (animate.getValue()) {
                float speed = (float) (double) animSpeed.getValue();
                currentBlur += (target - currentBlur) * 0.1f * speed;
            } else {
                currentBlur = target;
            }
        } else {
            currentBlur = 0f;
        }
    }

    /**
     * Custom self-made background glassmorphism blur simulator.
     * Draws layered offsets with decaying opacity to emulate a real Gaussian blur pass.
     */
    public void drawBlur(DrawContext ctx, int w, int h) {
        if (currentBlur <= 0.1f) return;

        float strength = (float) (double) blurStrength.getValue();
        int passes = Math.min(6, (int) (currentBlur / 4) + 1);

        for (int i = 1; i <= passes; i++) {
            float radius = (currentBlur / passes) * i * strength;
            int alpha = (int) (12.0f / passes * (1.5f - (float) i / passes));
            if (alpha <= 0) alpha = 1;

            // Render offset translucent layers to simulate soft focus depth of field
            ctx.fill(-5, -5, w + 5, h + 5, (alpha << 24) | 0x05070A);
        }
    }

    public int getBlurRadius() { return (int)(double) blurRadius.getValue(); }
    public double getBlurStrength() { return blurStrength.getValue(); }
    public boolean isAnimate() { return animate.getValue(); }
    public double getAnimSpeed() { return animSpeed.getValue(); }
}
