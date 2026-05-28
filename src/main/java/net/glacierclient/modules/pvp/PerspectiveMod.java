package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;

public class PerspectiveMod extends GlacierMod {

    private final NumberSetting sensitivity = new NumberSetting("Sensitivity", "Camera rotation sensitivity", 0.1, 3.0, 1.0);
    private final BooleanSetting holdMode = new BooleanSetting("Hold Mode", "Hold key to activate", true);
    private final BooleanSetting smoothRotation = new BooleanSetting("Smooth Rotation", "Smooth camera rotation", true);

    private float perspYaw, perspPitch;
    private boolean active = false;

    public PerspectiveMod() {
        super("360 Perspective", "Freely look around without rotating player direction", Category.PVP);
        addSettings(sensitivity, holdMode, smoothRotation);
    }

    @Override
    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            perspYaw = mc.player.getYaw();
            perspPitch = mc.player.getPitch();
        }
    }

    @Override
    public void onDisable() { active = false; }

    @Override
    public void onTick() {}

    public void rotatePerspective(double dx, double dy) {
        float mult = (float)(double) sensitivity.getValue();
        perspYaw += (float) (dx * 0.15 * mult);
        perspPitch = Math.max(-90f, Math.min(90f, perspPitch + (float)(dy * 0.15 * mult)));
    }

    public float getPerspYaw() { return perspYaw; }
    public float getPerspPitch() { return perspPitch; }
    public boolean isActive() { return active; }
    public void setActive(boolean v) { active = v; }
}
