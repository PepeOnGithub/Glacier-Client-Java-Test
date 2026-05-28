package net.glacierclient.bridge.v12111;

import net.fabricmc.api.ClientModInitializer;
import net.glacierclient.api.bridge.VersionBridgeProvider;

public class GlacierInit12111 implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        VersionBridgeProvider.register(new Bridge12111());
    }
}
