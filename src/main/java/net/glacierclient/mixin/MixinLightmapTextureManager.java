package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.modules.render.Fullbright;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Real fullbright: when the Fullbright module is active in Gamma mode, the lightmap reads our high
 * brightness value instead of the option's clamped 0..1 gamma — fully lighting caves and night.
 */
@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {

    @Redirect(method = "update", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;"), require = 0)
    private Object glacier$fullbrightGamma(SimpleOption<?> option) {
        Object real = option.getValue();
        GlacierClient gc = GlacierClient.getInstance();
        MinecraftClient mc = MinecraftClient.getInstance();
        if (gc != null && mc.options != null && option == mc.options.getGamma()) {
            Fullbright fb = gc.getModuleManager().getModule(Fullbright.class);
            if (fb != null && fb.isEnabled() && fb.isGammaMode()) return fb.getBrightness();
        }
        return real;
    }
}
