package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.modules.render.TimeChanger;
import net.glacierclient.modules.render.WeatherController;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Client-side visual overrides for Time Changer and Weather Controller. Only applies on the client
 * world so the integrated server's real time/weather are never altered.
 */
@Mixin(World.class)
public class MixinWorld {

    @Inject(method = "getTimeOfDay", at = @At("HEAD"), cancellable = true)
    private void glacier$visualTime(CallbackInfoReturnable<Long> cir) {
        if (!((Object) this instanceof ClientWorld)) return;
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return;
        TimeChanger tc = gc.getModuleManager().getModule(TimeChanger.class);
        if (tc != null && tc.isEnabled()) cir.setReturnValue(tc.getVisualTime());
    }

    @Inject(method = "getRainGradient", at = @At("HEAD"), cancellable = true)
    private void glacier$rain(float delta, CallbackInfoReturnable<Float> cir) {
        if (!((Object) this instanceof ClientWorld)) return;
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return;
        WeatherController wc = gc.getModuleManager().getModule(WeatherController.class);
        if (wc == null || !wc.isEnabled()) return;
        cir.setReturnValue("Clear".equals(wc.getWeather()) ? 0f : 1f);
    }

    @Inject(method = "getThunderGradient", at = @At("HEAD"), cancellable = true)
    private void glacier$thunder(float delta, CallbackInfoReturnable<Float> cir) {
        if (!((Object) this instanceof ClientWorld)) return;
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return;
        WeatherController wc = gc.getModuleManager().getModule(WeatherController.class);
        if (wc == null || !wc.isEnabled()) return;
        cir.setReturnValue("Thunder".equals(wc.getWeather()) ? 1f : 0f);
    }
}
