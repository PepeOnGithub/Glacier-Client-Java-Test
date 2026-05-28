package net.glacierclient.api.bridge;

import java.util.ServiceLoader;

public final class VersionBridgeProvider {

    private static VersionBridge instance;

    private VersionBridgeProvider() {}

    public static void register(VersionBridge bridge) {
        if (instance != null) {
            throw new IllegalStateException("VersionBridge already registered");
        }
        instance = bridge;
    }

    public static VersionBridge get() {
        if (instance == null) {
            throw new IllegalStateException("VersionBridge not yet registered");
        }
        return instance;
    }

    public static boolean isReady() {
        return instance != null;
    }

    public static void reset() {
        instance = null;
    }
}
