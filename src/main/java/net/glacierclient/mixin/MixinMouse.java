package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.modules.hud.CPS;
import net.glacierclient.modules.pvp.PerspectiveMod;
import net.glacierclient.modules.qol.ScrollSpeedMultiplier;
import net.glacierclient.modules.render.Zoom;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Feeds raw mouse-button presses to the CPS display (legitimate: read-only click counting). */
@Mixin(Mouse.class)
public class MixinMouse {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void glacier$onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (action != GLFW.GLFW_PRESS) return;
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return;
        boolean left = button == GLFW.GLFW_MOUSE_BUTTON_LEFT;
        boolean right = button == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
        if (!left && !right) return;

        CPS cps = gc.getModuleManager().getModule(CPS.class);
        if (cps != null && cps.isEnabled()) cps.registerClick(left);
    }

    /**
     * 360 Perspective: while freelook is active, the mouse turns the camera (via PerspectiveMod)
     * instead of the player — so the player keeps facing their original direction.
     */
    @Redirect(method = "updateMouse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"),
            require = 0)
    private void glacier$freelook(ClientPlayerEntity player, double dx, double dy) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc != null) {
            PerspectiveMod p = gc.getModuleManager().getModule(PerspectiveMod.class);
            if (p != null && p.isActive()) { p.rotatePerspective(dx, dy); return; }
        }
        player.changeLookDirection(dx, dy);
    }

    /** Scales the vertical scroll amount by the Scroll Speed Multiplier module (menus & hotbar). */
    @ModifyVariable(method = "onMouseScroll", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private double glacier$scrollSpeed(double vertical) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return vertical;
        ScrollSpeedMultiplier mod = gc.getModuleManager().getModule(ScrollSpeedMultiplier.class);
        if (mod == null || !mod.isEnabled()) return vertical;
        return mod.applyToScroll(vertical);
    }

    /** While zooming with Scroll Adjust on, the wheel changes the zoom level instead of the hotbar. */
    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void glacier$zoomScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null || !gc.isZooming()) return;
        Zoom zoom = gc.getModuleManager().getModule(Zoom.class);
        if (zoom != null && zoom.isEnabled() && zoom.isScrollAdjust() && zoom.applyScroll(vertical)) {
            ci.cancel();
        }
    }
}
