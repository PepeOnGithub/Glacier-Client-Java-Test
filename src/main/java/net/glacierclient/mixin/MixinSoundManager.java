package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.modules.pvp.SoundLocker;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Mutes sounds matched by the Sound Locker module before they are played. */
@Mixin(SoundManager.class)
public class MixinSoundManager {

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void glacier$filterSound(SoundInstance sound, CallbackInfo ci) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null || sound == null) return;
        SoundLocker mod = gc.getModuleManager().getModule(SoundLocker.class);
        if (mod == null || !mod.isEnabled()) return;
        Identifier id = sound.getId();
        if (id == null) return;
        if (mod.isMuted(id.getPath())) ci.cancel();
    }
}
