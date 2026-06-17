package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * 360 Perspective (freelook): while the look-around key (Left Alt) is held — or toggled, in toggle
 * mode — the camera turns freely without changing the player's facing. Movement keys still move the
 * player in their original direction. Driven by {@code MixinMouse} (feeds rotation, suppresses the
 * player turn) + {@code MixinCamera} (applies the free camera rotation). Purely client-side.
 */
public class PerspectiveMod extends GlacierMod {

    private final NumberSetting sensitivity = new NumberSetting("Sensitivity", "Camera rotation sensitivity", 0.1, 3.0, 1.0);
    private final BooleanSetting holdMode = new BooleanSetting("Hold Mode", "Hold the key to look around (off = press to toggle)", true);
    private final BooleanSetting smoothRotation = new BooleanSetting("Smooth Rotation", "Ease the free camera instead of snapping", true);

    private static final int KEY = GLFW.GLFW_KEY_LEFT_ALT; // look-around key

    private float perspYaw, perspPitch;     // smoothed camera rotation (read by the camera mixin)
    private float targetYaw, targetPitch;   // raw input target
    private boolean active = false;
    private boolean prevKey = false;

    public PerspectiveMod() {
        super("360 Perspective", "Freely look around without rotating player direction", Category.PVP);
        addSettings(sensitivity, holdMode, smoothRotation);
    }

    @Override
    public void onEnable() {
        syncToPlayer();
        active = false;
        prevKey = false;
    }

    @Override
    public void onDisable() { active = false; }

    private void syncToPlayer() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            perspYaw = targetYaw = mc.player.getYaw();
            perspPitch = targetPitch = mc.player.getPitch();
        }
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) { active = false; return; }

        // Only poll the key in-world (not while a screen/chat is open).
        boolean pressed = mc.currentScreen == null
                && InputUtil.isKeyPressed(mc.getWindow().getHandle(), KEY);

        boolean wasActive = active;
        if (holdMode.getValue()) {
            active = pressed;
        } else if (pressed && !prevKey) {
            active = !active;
        }
        prevKey = pressed;

        if (active && !wasActive) syncToPlayer(); // begin: start from current facing

        if (!active) return;
        // Ease (or snap) the camera toward the input target.
        if (smoothRotation.getValue()) {
            perspYaw += angleDelta(perspYaw, targetYaw) * 0.5f;
            perspPitch += (targetPitch - perspPitch) * 0.5f;
        } else {
            perspYaw = targetYaw;
            perspPitch = targetPitch;
        }
    }

    private static float angleDelta(float from, float to) {
        float d = (to - from) % 360f;
        if (d > 180f) d -= 360f;
        if (d < -180f) d += 360f;
        return d;
    }

    /** Feed a raw mouse delta (already the per-frame cursor delta; we apply the 0.15 look step). */
    public void rotatePerspective(double dx, double dy) {
        float mult = (float) (double) sensitivity.getValue();
        targetYaw += (float) (dx * 0.15 * mult);
        targetPitch = Math.max(-90f, Math.min(90f, targetPitch + (float) (dy * 0.15 * mult)));
    }

    public float getPerspYaw() { return perspYaw; }
    public float getPerspPitch() { return perspPitch; }
    public boolean isActive() { return active && isEnabled(); }
}
