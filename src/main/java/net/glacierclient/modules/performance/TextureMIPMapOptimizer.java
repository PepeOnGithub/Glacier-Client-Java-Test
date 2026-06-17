package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class TextureMIPMapOptimizer extends GlacierMod {

    private final NumberSetting mipMapLevel = new NumberSetting("MipMap Level", "Texture mipmap level", 0, 4, 4);
    private final BooleanSetting anisotropicFiltering = new BooleanSetting("Anisotropic Filtering", "Enable anisotropic filtering", false);
    private final NumberSetting anisoLevel = new NumberSetting("Aniso Level", "Anisotropic filtering level", 2, 16, 4);

    public TextureMIPMapOptimizer() {
        super("Texture MipMap Optimizer", "Optimize texture mipmap settings", Category.PERFORMANCE);
        addSettings(mipMapLevel, anisotropicFiltering, anisoLevel);
    }

    private int savedMip = 4;

    @Override
    public void onEnable() {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.options != null) savedMip = mc.options.getMipmapLevels().getValue();
        applySettings();
    }

    @Override
    public void onDisable() {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.options != null) mc.options.getMipmapLevels().setValue(savedMip);
    }

    @Override
    public void onTick() { applySettings(); }

    private void applySettings() {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.options != null && mc.options.getMipmapLevels().getValue() != (int)(double) mipMapLevel.getValue()) {
            mc.options.getMipmapLevels().setValue((int)(double) mipMapLevel.getValue());
        }
    }

    public int getMipMapLevel() { return (int)(double) mipMapLevel.getValue(); }
    public boolean isAF() { return anisotropicFiltering.getValue(); }
    public int getAFLevel() { return (int)(double) anisoLevel.getValue(); }
}
