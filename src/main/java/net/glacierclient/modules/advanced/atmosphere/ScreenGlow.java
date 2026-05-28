package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.gui.DrawContext;

public class ScreenGlow extends GlacierMod {

    private final ColorSetting color = new ColorSetting("Color", "Glow color", 0xFF7289DA);
    private final NumberSetting intensity = new NumberSetting("Intensity", "Glow intensity", 0.0, 1.0, 0.3);
    private final NumberSetting radius = new NumberSetting("Radius", "Glow radius in pixels", 10, 100, 40);
    private final BooleanSetting pulse = new BooleanSetting("Pulse", "Animate glow pulsing", false);

    private float pulseOffset = 0;

    public ScreenGlow() {
        super("Screen Glow", "Add ambient glow effect around screen edges", Category.RENDER);
        addSettings(color, intensity, radius, pulse);
    }

    @Override public void onEnable() { pulseOffset = 0; }
    @Override public void onDisable() {}

    @Override
    public void onTick() {
        if (pulse.getValue()) {
            pulseOffset += 0.05f;
            if (pulseOffset > Math.PI * 2) pulseOffset -= (float) (Math.PI * 2);
        }
    }

    public void renderGlow(DrawContext context, int screenW, int screenH) {
        float pulseMultiplier = pulse.getValue() ? (0.7f + 0.3f * (float) Math.sin(pulseOffset)) : 1.0f;
        float currentIntensity = (float)(double) intensity.getValue() * pulseMultiplier;
        int r = (int)(double) radius.getValue();
        int baseColor = color.getValue();
        int steps = r / 2;
        for (int i = steps; i > 0; i--) {
            float alpha = currentIntensity * (1f - i / (float) steps);
            int a = Math.max(0, Math.min(255, (int) (alpha * 128)));
            int c = (a << 24) | (baseColor & 0x00FFFFFF);
            int pad = i * 2;
            // Top
            context.fill(0, 0, screenW, pad, c);
            // Bottom
            context.fill(0, screenH - pad, screenW, screenH, c);
            // Left
            context.fill(0, pad, pad, screenH - pad, c);
            // Right
            context.fill(screenW - pad, pad, screenW, screenH - pad, c);
        }
    }
}
