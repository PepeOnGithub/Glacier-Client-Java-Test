package net.glacierclient.modules.render;

import net.glacierclient.core.module.Category;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;

public class FOVModule extends GlacierMod {

    private final ModeSetting mode = new ModeSetting("Mode", "How the field of view is controlled",
            "Dynamic", "Static", "Dynamic", "Multiplier");
    private final NumberSetting baseFOV = new NumberSetting("Base FOV", "Field of view at rest", 30, 170, 90);
    private final NumberSetting sprintFOV = new NumberSetting("Sprint FOV", "Field of view while sprinting", 30, 170, 95);
    private final BooleanSetting combatFOV = new BooleanSetting("Combat FOV", "Use a separate FOV in combat", false);
    private final NumberSetting combatFOVValue = new NumberSetting("Combat FOV Value", "Field of view in combat", 30, 170, 85);
    private final BooleanSetting smoothTransition = new BooleanSetting("Smooth Transition", "Ease between FOV targets", true);
    private final NumberSetting transitionSpeed = new NumberSetting("Transition Speed", "Speed of smooth transitions", 0.1, 5.0, 1.5);
    private final NumberSetting multiplier = new NumberSetting("Multiplier", "Scales the game's FOV in Multiplier mode", 0.5, 2.0, 1.0);
    private final NumberSetting combatHold = new NumberSetting("Combat Hold", "Seconds combat FOV stays active after a hit", 0.5, 10.0, 3.0);

    private float currentFOV;
    private long lastHitTime = 0;

    public FOVModule() {
        super("FOV", "Unified field-of-view control: lock, dynamic sprint/combat, or multiplier", Category.RENDER);
        addSettings(mode, baseFOV, sprintFOV, combatFOV, combatFOVValue, smoothTransition, transitionSpeed, multiplier, combatHold);
    }

    @Override
    public void onEnable() {
        currentFOV = baseFOV.getValue().floatValue();
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        float target;
        if (mode.is("Static")) {
            target = baseFOV.getValue().floatValue();
        } else if (mode.is("Multiplier")) {
            return;
        } else {
            if (combatFOV.getValue() && System.currentTimeMillis() - lastHitTime < (long) (combatHold.getValue() * 1000)) {
                target = combatFOVValue.getValue().floatValue();
            } else if (mc.player.isSprinting()) {
                target = sprintFOV.getValue().floatValue();
            } else {
                target = baseFOV.getValue().floatValue();
            }
        }

        if (smoothTransition.getValue() && currentFOV > 0) {
            currentFOV += (target - currentFOV) * (float) (transitionSpeed.getValue() * 0.1);
            if (Math.abs(target - currentFOV) < 0.1f) currentFOV = target;
        } else {
            currentFOV = target;
        }
    }

    public double applyFov(double vanillaFov) {
        if (mode.is("Multiplier")) {
            return vanillaFov * multiplier.getValue();
        }
        if (currentFOV <= 0) currentFOV = baseFOV.getValue().floatValue();
        return currentFOV;
    }

    public void onHit() {
        lastHitTime = System.currentTimeMillis();
    }
}
