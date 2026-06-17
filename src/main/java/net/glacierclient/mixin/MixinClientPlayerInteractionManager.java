package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.event.events.AttackEntityEvent;
import net.glacierclient.modules.hud.ComboCounter;
import net.glacierclient.modules.hud.ReachDisplay;
import net.glacierclient.modules.render.CustomCrosshair;
import net.glacierclient.modules.render.FOVModule;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Feeds melee attacks to the Combo Counter and Reach Display, and posts {@link AttackEntityEvent}.
 * Read-only: it observes attacks the player already initiated, never modifies combat behaviour.
 */
@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void glacier$onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null || player == null || target == null) return;

        double reach = player.getEyePos().distanceTo(target.getPos());

        ComboCounter combo = gc.getModuleManager().getModule(ComboCounter.class);
        if (combo != null && combo.isEnabled()) combo.onHit();

        ReachDisplay reachMod = gc.getModuleManager().getModule(ReachDisplay.class);
        if (reachMod != null && reachMod.isEnabled()) reachMod.onHit(reach);

        FOVModule fov = gc.getModuleManager().getModule(FOVModule.class);
        if (fov != null && fov.isEnabled()) fov.onHit();

        CustomCrosshair crosshair = gc.getModuleManager().getModule(CustomCrosshair.class);
        if (crosshair != null && crosshair.isEnabled()) crosshair.onAttack();

        gc.getEventBus().post(new AttackEntityEvent(target, (float) reach));
    }
}
