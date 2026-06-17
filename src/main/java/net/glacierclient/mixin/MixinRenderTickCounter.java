package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.modules.advanced.mechanics.TimerMod;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Timer module: scales the number of game ticks processed per frame by the configured speed.
 * Works purely on the public return value of beginRenderTick (no internal field access), with a
 * fractional accumulator so non-integer speeds stay accurate over time.
 */
@Mixin(RenderTickCounter.class)
public class MixinRenderTickCounter {

    private float glacier$accumulator = 0f;

    @Inject(method = "beginRenderTick", at = @At("RETURN"), cancellable = true, require = 0)
    private void glacier$timer(long timeMillis, CallbackInfoReturnable<Integer> cir) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return;
        TimerMod mod = gc.getModuleManager().getModule(TimerMod.class);
        if (mod == null || !mod.isEnabled()) return;
        float speed = mod.getSpeed();
        if (speed == 1f) return;

        float scaled = cir.getReturnValueI() * speed + glacier$accumulator;
        int ticks = (int) scaled;
        glacier$accumulator = scaled - ticks;
        cir.setReturnValue(ticks);
    }
}
