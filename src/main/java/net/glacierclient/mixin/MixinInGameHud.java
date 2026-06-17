package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.event.events.RenderEvent;
import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.modules.qol.NotificationToastSystem;
import net.glacierclient.modules.render.CustomCrosshair;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void glacier$crosshair(DrawContext context, CallbackInfo ci) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return;
        CustomCrosshair cc = gc.getModuleManager().getModule(CustomCrosshair.class);
        if (cc != null && cc.isEnabled() && cc.replacesVanilla()) {
            cc.render(context);
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderHud(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (GlacierClient.getInstance() == null) return;
        GlacierClient.getInstance().getEventBus().post(new RenderEvent(context, tickDelta, RenderEvent.Phase.POST));
        for (var mod : GlacierClient.getInstance().getModuleManager().getModules()) {
            if (mod instanceof HUDMod hudMod && hudMod.isEnabled() && hudMod.isVisible()) {
                // Apply the element's Scale setting by scaling about its own top-left corner. Subclasses
                // draw at absolute getX()/getY() (which already account for scaled size in positioning),
                // so scaling about that point keeps the anchor fixed while resizing the content.
                float s = hudMod.getScale();
                if (s != 1f) {
                    int hx = hudMod.getX(), hy = hudMod.getY();
                    var ms = context.getMatrices();
                    ms.push();
                    ms.translate(hx, hy, 0);
                    ms.scale(s, s, 1f);
                    ms.translate(-hx, -hy, 0);
                    hudMod.render(context, tickDelta);
                    ms.pop();
                } else {
                    hudMod.render(context, tickDelta);
                }
            }
        }
        GlacierClient.getInstance().getNotificationSystem().render(context,
                context.getScaledWindowWidth(), context.getScaledWindowHeight());

        NotificationToastSystem toasts = GlacierClient.getInstance().getModuleManager().getModule(NotificationToastSystem.class);
        if (toasts != null && toasts.isEnabled()) toasts.render(context);
    }
}
