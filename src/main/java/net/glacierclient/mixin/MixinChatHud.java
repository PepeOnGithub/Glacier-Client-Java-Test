package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.event.events.ChatReceiveEvent;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class MixinChatHud {

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
            at = @At("HEAD"), cancellable = true)
    private void onAddMessage(Text message, net.minecraft.network.message.MessageSignatureData signature,
                               net.minecraft.client.gui.hud.MessageIndicator indicator, CallbackInfo ci) {
        if (GlacierClient.getInstance() == null) return;
        ChatReceiveEvent event = new ChatReceiveEvent(message.getString());
        GlacierClient.getInstance().getEventBus().post(event);
        if (event.isCancelled()) ci.cancel();
    }
}
