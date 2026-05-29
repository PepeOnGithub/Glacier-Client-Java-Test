package net.glacierclient.bridge.v12111;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.glacierclient.api.bridge.VersionBridgeProvider;
import net.glacierclient.core.client.GlacierCore;

public class GlacierInit12111 implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        VersionBridgeProvider.register(new Bridge12111());
        GlacierCore.get().init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> GlacierCore.get().onClientTick());
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> GlacierCore.get().onRenderHud(drawContext));
    }
}
