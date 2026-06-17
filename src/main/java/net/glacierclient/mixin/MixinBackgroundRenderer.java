package net.glacierclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.glacierclient.GlacierClient;
import net.glacierclient.modules.advanced.atmosphere.CustomFogDensity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fog Control: overrides shader fog distance/density (and optionally colour) after vanilla has set
 * it up, so the Fog Control module ("Fog Control") actually drives what the player sees. Strictly
 * gated on the module being enabled; otherwise vanilla fog is left untouched.
 */
@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {

    private static CustomFogDensity glacier$mod() {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return null;
        CustomFogDensity mod = gc.getModuleManager().getModule(CustomFogDensity.class);
        return (mod != null && mod.isEnabled()) ? mod : null;
    }

    @Inject(method = "applyFog", at = @At("TAIL"), require = 0)
    private static void glacier$applyFog(Camera camera, BackgroundRenderer.FogType fogType,
                                         float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        CustomFogDensity mod = glacier$mod();
        if (mod == null) return;

        // Keep Atmosphere: leave the distant sky fog as vanilla, only reshape terrain fog.
        if (mod.isKeepAtmosphere() && fogType == BackgroundRenderer.FogType.FOG_SKY) return;

        // Remove Cave Fog: push fog far away while blinded/in darkness so caves stay clear.
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mod.isRemoveCaveFog() && mc.player != null
                && (mc.player.hasStatusEffect(StatusEffects.BLINDNESS) || mc.player.hasStatusEffect(StatusEffects.DARKNESS))) {
            RenderSystem.setShaderFogStart(viewDistance * 0.5f);
            RenderSystem.setShaderFogEnd(viewDistance);
            return;
        }

        float start = mod.getFogStart();
        float end = mod.getFogEnd();
        float density = mod.getDensity(); // 0..1, higher = closer/denser fog
        if (density > 0.001f) {
            end = end * (1.0f - density * 0.9f);
            start = Math.min(start, end - 1f);
        }
        if (end <= start) end = start + 1f;
        RenderSystem.setShaderFogStart(start);
        RenderSystem.setShaderFogEnd(end);
    }

    @Inject(method = "applyFogColor", at = @At("TAIL"), require = 0)
    private static void glacier$applyFogColor(CallbackInfo ci) {
        CustomFogDensity mod = glacier$mod();
        if (mod == null) return;
        int c = mod.getFogColor();
        RenderSystem.setShaderFogColor(((c >> 16) & 0xFF) / 255f, ((c >> 8) & 0xFF) / 255f, (c & 0xFF) / 255f);
    }
}
