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

    @Override
    public void onEnable() { applySettings(); }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    private void applySettings() {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.options != null) {
            mc.options.getMipmapLevels().setValue((int) mipMapLevel.getValue());
        }
    }

    public int getMipMapLevel() { return (int) mipMapLevel.getValue(); }
    public boolean isAF() { return anisotropicFiltering.getValue(); }
    public int getAFLevel() { return (int) anisoLevel.getValue(); }
}
