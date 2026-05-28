package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class CinematicCameraTweak extends GlacierMod {

    private final NumberSetting smoothness = new NumberSetting("Smoothness", "Camera smoothness factor", 1.0, 15.0, 4.0);
    private final BooleanSetting horizontal = new BooleanSetting("Horizontal", "Apply horizontal smoothing", true);
    private final BooleanSetting vertical = new BooleanSetting("Vertical", "Apply vertical smoothing", true);
    private final NumberSetting maxSpeed = new NumberSetting("Max Speed", "Maximum camera speed", 1.0, 30.0, 10.0);

    private float smoothYaw = 0;
    private float smoothPitch = 0;

    public CinematicCameraTweak() {
        super("Cinematic Camera", "Smooth out cinematic camera with custom settings", Category.RENDER);
        addSettings(smoothness, horizontal, vertical, maxSpeed);
    }

    @Override
    public void onEnable() {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.player != null) {
            smoothYaw = mc.player.getYaw();
            smoothPitch = mc.player.getPitch();
        }
    }

    @Override public void onDisable() {}

    @Override
    public void onTick() {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.player == null) return;
        float factor = (float) (1.0 / smoothness.getValue());
        if (horizontal.getValue()) {
            float yawDiff = mc.player.getYaw() - smoothYaw;
            float capped = Math.max(-(float)(double) maxSpeed.getValue(), Math.min((float)(double) maxSpeed.getValue(), yawDiff));
            smoothYaw += capped * factor;
        }
        if (vertical.getValue()) {
            float pitchDiff = mc.player.getPitch() - smoothPitch;
            float capped = Math.max(-(float)(double) maxSpeed.getValue(), Math.min((float)(double) maxSpeed.getValue(), pitchDiff));
            smoothPitch += capped * factor;
        }
    }

    public float getSmoothYaw() { return smoothYaw; }
    public float getSmoothPitch() { return smoothPitch; }
}
