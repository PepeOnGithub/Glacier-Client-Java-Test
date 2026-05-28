package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;

public class FOVChanger extends GlacierMod {

    private final NumberSetting baseFOV = new NumberSetting("Base FOV", "Base field of view", 30, 170, 90);
    private final NumberSetting sprintFOV = new NumberSetting("Sprint FOV", "FOV while sprinting", 30, 170, 95);
    private final BooleanSetting smoothTransition = new BooleanSetting("Smooth Transition", "Smooth FOV changes", true);
    private final NumberSetting transitionSpeed = new NumberSetting("Transition Speed", "FOV transition speed", 0.1, 5.0, 1.5);
    private final BooleanSetting combatFOV = new BooleanSetting("Combat FOV", "Use separate combat FOV", false);
    private final NumberSetting combatFOVValue = new NumberSetting("Combat FOV Value", "FOV in combat", 30, 170, 85);

    private float currentFOV;
    private boolean inCombat = false;
    private long lastHitTime = 0;

    public FOVChanger() {
        super("FOV Changer", "Dynamic action-based FOV changes", Category.RENDER);
        addSettings(baseFOV, sprintFOV, smoothTransition, transitionSpeed, combatFOV, combatFOVValue);
    }

    @Override
    public void onEnable() {
        currentFOV = (float)(double) baseFOV.getValue();
    }

    @Override public void onDisable() {}

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        inCombat = combatFOV.getValue() && (System.currentTimeMillis() - lastHitTime < 3000);
        float targetFOV;
        if (inCombat) targetFOV = (float)(double) combatFOVValue.getValue();
        else if (mc.player.isSprinting()) targetFOV = (float)(double) sprintFOV.getValue();
        else targetFOV = (float)(double) baseFOV.getValue();
        if (smoothTransition.getValue()) {
            float speed = (float) (transitionSpeed.getValue() * 0.1);
            currentFOV += (targetFOV - currentFOV) * speed;
        } else {
            currentFOV = targetFOV;
        }
    }

    public void onHit() { lastHitTime = System.currentTimeMillis(); }
    public float getCurrentFOV() { return currentFOV; }
}
