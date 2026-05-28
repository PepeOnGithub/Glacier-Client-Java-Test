package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;

public class CrosshairDynamicGap extends GlacierMod {

    private final NumberSetting baseGap = new NumberSetting("Base Gap", "Crosshair gap at rest", 0, 20, 4);
    private final NumberSetting movingGap = new NumberSetting("Moving Gap", "Crosshair gap while moving", 0, 40, 12);
    private final NumberSetting attackGap = new NumberSetting("Attack Gap", "Crosshair gap on attack", 0, 20, 8);
    private final NumberSetting transitionSpeed = new NumberSetting("Transition Speed", "Gap transition speed", 0.1, 5.0, 2.0);

    private float currentGap;
    private long lastAttackTime = 0;

    public CrosshairDynamicGap() {
        super("Dynamic Crosshair", "Crosshair gap changes when moving/attacking", Category.RENDER);
        addSettings(baseGap, movingGap, attackGap, transitionSpeed);
    }

    @Override
    public void onEnable() { currentGap = (float)(double) baseGap.getValue(); }
    @Override public void onDisable() {}

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        boolean isMoving = mc.player.isSprinting() || mc.player.input != null &&
            (mc.player.input.movementForward != 0 || mc.player.input.movementSideways != 0);
        boolean isAttacking = System.currentTimeMillis() - lastAttackTime < 300;
        float targetGap;
        if (isAttacking) targetGap = (float)(double) attackGap.getValue();
        else if (isMoving) targetGap = (float)(double) movingGap.getValue();
        else targetGap = (float)(double) baseGap.getValue();
        float speed = (float) (transitionSpeed.getValue() * 0.15);
        currentGap += (targetGap - currentGap) * speed;
    }

    public void onAttack() { lastAttackTime = System.currentTimeMillis(); }
    public int getCurrentGap() { return Math.round(currentGap); }
}
