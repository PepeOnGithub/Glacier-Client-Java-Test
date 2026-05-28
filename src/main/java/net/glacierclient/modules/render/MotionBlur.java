package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;

public class MotionBlur extends GlacierMod {

    private final NumberSetting strength = new NumberSetting("Strength", "Motion blur intensity", 0.1, 0.9, 0.5);
    private final BooleanSetting onlyOnMove = new BooleanSetting("Only On Move", "Apply blur only when moving camera", false);

    private double lastYaw, lastPitch;
    private boolean isMoving = false;

    public MotionBlur() {
        super("Motion Blur", "Adds motion blur to camera movement", Category.RENDER);
        addSettings(strength, onlyOnMove);
    }

    @Override
    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            lastYaw = mc.player.getYaw();
            lastPitch = mc.player.getPitch();
        }
    }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        double dy = mc.player.getYaw() - lastYaw;
        double dp = mc.player.getPitch() - lastPitch;
        isMoving = Math.abs(dy) > 0.01 || Math.abs(dp) > 0.01;
        lastYaw = mc.player.getYaw();
        lastPitch = mc.player.getPitch();
    }

    public float getEffectiveStrength() {
        if (onlyOnMove.getValue() && !isMoving) return 0f;
        return (float)(double) strength.getValue();
    }
}
