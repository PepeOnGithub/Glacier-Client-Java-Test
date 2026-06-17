package net.glacierclient.mixin;

import net.glacierclient.gui.screens.GlacierPauseScreen;
import net.glacierclient.gui.screens.GlacierTitleScreen;
import net.glacierclient.web.GlacierWebScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Redirects the vanilla Title and Pause screens to their Glacier-styled equivalents. Uses exact
 * class matching (not instanceof) so our own subclasses/replacements never trigger recursion.
 */
@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void glacier$redirectScreen(Screen screen, CallbackInfo ci) {
        if (screen == null) return;
        MinecraftClient mc = (MinecraftClient) (Object) this;
        if (screen.getClass() == TitleScreen.class) {
            ci.cancel();
            // Web UI first (shows a brief loading state while natives unpack on first launch); the
            // native Glacier screen is the fallback if the engine can't start on this machine.
            mc.setScreen(GlacierWebScreen.forPage("title.html", new GlacierTitleScreen()));
        } else if (screen.getClass() == GameMenuScreen.class) {
            ci.cancel();
            mc.setScreen(GlacierWebScreen.forPage("pause.html", new GlacierPauseScreen()));
        }
    }
}
