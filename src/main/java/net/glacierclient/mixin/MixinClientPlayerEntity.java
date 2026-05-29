package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.event.events.ChatSendEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChat(String message, net.minecraft.text.Text preview, CallbackInfo ci) {
        if (GlacierClient.getInstance() == null) return;
        ChatSendEvent event = new ChatSendEvent(message);
        GlacierClient.getInstance().getEventBus().post(event);
        if (event.isCancelled()) ci.cancel();
    }
}
