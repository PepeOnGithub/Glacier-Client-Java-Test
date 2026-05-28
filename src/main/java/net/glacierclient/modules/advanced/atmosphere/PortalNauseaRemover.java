package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffects;

public class PortalNauseaRemover extends GlacierMod {

    private final BooleanSetting removeNausea = new BooleanSetting("Remove Nausea", "Remove portal nausea", true);
    private final BooleanSetting removePortalOverlay = new BooleanSetting("Remove Overlay", "Remove portal overlay texture", false);
    private final NumberSetting intensity = new NumberSetting("Intensity", "Nausea intensity override (0=remove)", 0.0, 1.0, 0.0);

    public PortalNauseaRemover() {
        super("Portal Nausea", "Remove or reduce nether/end portal nausea effect", Category.RENDER);
        addSettings(removeNausea, removePortalOverlay, intensity);
    }

    @Override
    public void onEnable() {
        applyEffect();
    }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        applyEffect();
    }

    private void applyEffect() {
        if (!removeNausea.getValue()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (mc.player.hasStatusEffect(StatusEffects.NAUSEA) && intensity.getValue() == 0.0) {
            mc.player.removeStatusEffect(StatusEffects.NAUSEA);
        }
    }

    public boolean shouldRemoveOverlay() { return removePortalOverlay.getValue(); }
    public float getIntensityOverride() { return (float) intensity.getValue(); }
}
