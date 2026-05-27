package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.event.events.RenderEvent;
import net.glacierclient.core.hud.HUDMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderHud(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (GlacierClient.getInstance() == null) return;
        GlacierClient.getInstance().getEventBus().post(new RenderEvent(context, tickDelta, RenderEvent.Phase.POST));
        for (var mod : GlacierClient.getInstance().getModuleManager().getModules()) {
            if (mod instanceof HUDMod hudMod && hudMod.isEnabled() && hudMod.isVisible()) {
                hudMod.render(context, tickDelta);
            }
        }
        GlacierClient.getInstance().getNotificationSystem().render(context,
                context.getScaledWindowWidth(), context.getScaledWindowHeight());
    }
}
