package net.glacierclient.bridge.v1165;

import net.fabricmc.api.ClientModInitializer;
import net.glacierclient.api.bridge.VersionBridgeProvider;

public class GlacierInit1165 implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        VersionBridgeProvider.register(new Bridge1165());
    }
}
