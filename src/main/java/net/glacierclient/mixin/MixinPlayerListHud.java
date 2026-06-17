package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.modules.pvp.NickHider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Nick Hider: replaces the local player's name in the tab list with the configured nickname. */
@Mixin(PlayerListHud.class)
public class MixinPlayerListHud {

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true, require = 0)
    private void glacier$nickTablist(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return;
        NickHider nh = gc.getModuleManager().getModule(NickHider.class);
        if (nh == null || !nh.isEnabled() || !nh.isHideInTablist()) return;
        String real = MinecraftClient.getInstance().getSession().getUsername();
        if (real == null || entry.getProfile() == null) return;
        if (real.equals(entry.getProfile().getName())) {
            cir.setReturnValue(Text.literal(nh.getDisplayName()));
        }
    }
}
