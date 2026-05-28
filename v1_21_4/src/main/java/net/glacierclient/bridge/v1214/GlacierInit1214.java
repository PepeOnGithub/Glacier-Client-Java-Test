package net.glacierclient.bridge.v1214;

import net.fabricmc.api.ClientModInitializer;
import net.glacierclient.api.bridge.VersionBridgeProvider;

public class GlacierInit1214 implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        VersionBridgeProvider.register(new Bridge1214());
    }
}
