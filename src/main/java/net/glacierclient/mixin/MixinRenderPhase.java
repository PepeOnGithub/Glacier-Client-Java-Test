package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.modules.pvp.GlintScaleModifier;
import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Glint Scale module: multiplies the enchantment-glint texture scale, changing how large/dense the
 * animated glint appears on items and armor.
 */
@Mixin(RenderPhase.class)
public class MixinRenderPhase {

    @ModifyVariable(method = "setupGlintTexturing(F)V", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private static float glacier$glintScale(float scale) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return scale;
        GlintScaleModifier mod = gc.getModuleManager().getModule(GlintScaleModifier.class);
        if (mod == null || !mod.isEnabled()) return scale;
        return scale * mod.getEffectiveScale();
    }
}
