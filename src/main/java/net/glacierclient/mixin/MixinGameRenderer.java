package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.event.events.RenderEvent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderPre(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (GlacierClient.getInstance() != null) {
            GlacierClient.getInstance().getEventBus().post(new RenderEvent(null, tickDelta, RenderEvent.Phase.PRE));
        }
    }
}
