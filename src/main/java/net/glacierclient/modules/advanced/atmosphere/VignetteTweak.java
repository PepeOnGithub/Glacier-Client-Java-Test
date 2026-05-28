package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class VignetteTweak extends GlacierMod {

    private final NumberSetting intensity = new NumberSetting("Intensity", "Vignette intensity", 0.0, 2.0, 0.5);
    private final ColorSetting color = new ColorSetting("Color", "Vignette color", 0xFF000000);
    private final BooleanSetting removeOnHighHealth = new BooleanSetting("Remove on Health", "Remove at full health", false);
    private final BooleanSetting pulseOnLowHealth = new BooleanSetting("Pulse on Low", "Pulse when health is low", true);

    private float pulseOffset = 0;

    public VignetteTweak() {
        super("Vignette Tweak", "Modify screen vignette darkness and color", Category.RENDER);
        addSettings(intensity, color, removeOnHighHealth, pulseOnLowHealth);
    }

    @Override public void onEnable() { pulseOffset = 0; }
    @Override public void onDisable() {}

    @Override
    public void onTick() {
        pulseOffset += 0.05f;
        if (pulseOffset > (float) Math.PI * 2) pulseOffset -= (float) Math.PI * 2;
    }

    public void renderVignette(DrawContext context, int w, int h) {
        MinecraftClient mc = MinecraftClient.getInstance();
        float health = mc.player != null ? mc.player.getHealth() / mc.player.getMaxHealth() : 1f;
        if (removeOnHighHealth.getValue() && health > 0.99f) return;
        float pulseBoost = (pulseOnLowHealth.getValue() && health < 0.25f)
            ? (float) (0.3 * Math.sin(pulseOffset)) : 0;
        float currentIntensity = (float)(double) intensity.getValue() + pulseBoost;
        int steps = 30;
        int base = color.getValue();
        for (int i = steps; i > 0; i--) {
            float alpha = currentIntensity * (1f - i / (float) steps);
            int a = Math.max(0, Math.min(255, (int) (alpha * 255)));
            int c = (a << 24) | (base & 0x00FFFFFF);
            int pad = i * 3;
            context.fill(0, 0, w, pad, c);
            context.fill(0, h - pad, w, h, c);
            context.fill(0, pad, pad, h - pad, c);
            context.fill(w - pad, pad, w, h - pad, c);
        }
    }
}
