package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.event.events.PlayerMoveEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fires {@link PlayerMoveEvent} once per local-player movement tick so movement modules
 * (Speed, HighJump, LongJump, AirStrafe, SafeWalk, ElytraFlight, NoSlow) can adjust velocity.
 */
@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerTick {

    @Inject(method = "tickMovement", at = @At("HEAD"), require = 0)
    private void glacier$onMovementTick(CallbackInfo ci) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return;
        ClientPlayerEntity p = (ClientPlayerEntity) (Object) this;
        gc.getEventBus().post(new PlayerMoveEvent(
            p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch()));
    }
}
