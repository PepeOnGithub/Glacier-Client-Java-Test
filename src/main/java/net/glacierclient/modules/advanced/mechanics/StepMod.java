package net.glacierclient.modules.advanced.mechanics;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
import net.glacierclient.core.event.*;
import net.minecraft.client.MinecraftClient;
public final class StepMod extends GlacierMod {
    private final NumberSetting height = new NumberSetting("Height", 1.0f, 0.5f, 2.5f);
    public StepMod() {
        super("Step", "Allows stepping over blocks higher than 0.5", Category.PVP, -1);
        addSettings(height);
    }
    @Override public void onEnable() { setStepHeight((float) height.get()); }
    @Override public void onDisable() { setStepHeight(0.6f); }
    @EventListen
    public void onUpdate(EventUpdate event) { setStepHeight((float) height.get()); }
    private void setStepHeight(float h) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) mc.player.stepHeight = h;
    }
}
