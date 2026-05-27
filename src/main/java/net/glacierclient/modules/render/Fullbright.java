package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.minecraft.client.MinecraftClient;

public class Fullbright extends GlacierMod {

    private final NumberSetting gamma = new NumberSetting("Gamma", "Gamma brightness level", 1, 15, 15);
    private final ModeSetting mode = new ModeSetting("Mode", "Fullbright implementation", "Gamma", "Gamma", "Night Vision");

    private double savedGamma = 1.0;

    public Fullbright() {
        super("Fullbright", "Sets gamma to maximum for full visibility", Category.RENDER);
        addSettings(gamma, mode);
    }

    @Override
    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if ("Gamma".equals(mode.getValue()) && mc.options != null) {
            savedGamma = mc.options.getGamma().getValue();
            mc.options.getGamma().setValue(gamma.getValue());
        }
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if ("Gamma".equals(mode.getValue()) && mc.options != null) {
            mc.options.getGamma().setValue(savedGamma);
        }
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if ("Gamma".equals(mode.getValue()) && mc.options != null) {
            mc.options.getGamma().setValue(gamma.getValue());
        }
    }
}
