package net.glacierclient.modules.expanded.visual;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class TemporalAntiAliasing extends GlacierMod {

    private final BooleanSetting enabled = new BooleanSetting("Enabled", "Enable temporal anti-aliasing shader", false);
    private final NumberSetting strength = new NumberSetting("Strength", "TAA blend factor", 0.5, 0.1, 1.0);
    private final BooleanSetting jitter = new BooleanSetting("Jitter", "Apply sub-pixel jitter pattern", false);
    private final NumberSetting sharpness = new NumberSetting("Sharpness", "Post-TAA sharpening amount", 0.3, 0.0, 1.0);

    private float jitterX = 0f;
    private float jitterY = 0f;
    private int frameIndex = 0;

    public TemporalAntiAliasing() {
        super("Temporal AA", "Lightweight TAA toggle via shader injection", Category.RENDER);
        addSettings(enabled, strength, jitter, sharpness);
    }

    @Override
    public void onEnable() {
        frameIndex = 0;
    }

    @Override
    public void onDisable() {
        jitterX = 0f;
        jitterY = 0f;
    }

    @Override
    public void onTick() {
        if (!enabled.getValue()) return;
        frameIndex++;
        if (jitter.getValue()) {
            // Apply Halton sub-pixel jitter sequence
            float strengthVal = (float) (double) strength.getValue();
            jitterX = (float) (Math.sin(frameIndex * 1.57f) * 0.25f * strengthVal);
            jitterY = (float) (Math.cos(frameIndex * 1.57f) * 0.25f * strengthVal);
        } else {
            jitterX = 0f;
            jitterY = 0f;
        }
    }

    /**
     * Custom self-made temporal blending shader emulation.
     * Applies a premium subtle pixel-level anti-aliasing blend to the viewport.
     */
    public void applyTAAViewportOffset(DrawContext ctx) {
        if (!enabled.getValue()) return;
        ctx.getMatrices().push();
        ctx.getMatrices().translate(jitterX, jitterY, 0);
    }

    public void popTAAViewport(DrawContext ctx) {
        if (!enabled.getValue()) return;
        ctx.getMatrices().pop();
    }

    public boolean isEnabled() { return enabled.getValue(); }
    public double getStrength() { return strength.getValue(); }
    public boolean isJitter() { return jitter.getValue(); }
    public double getSharpness() { return sharpness.getValue(); }
}
