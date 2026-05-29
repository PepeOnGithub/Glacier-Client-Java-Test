package net.glacierclient.core.client;

import net.glacierclient.api.bridge.VersionBridge;
import net.glacierclient.api.bridge.VersionBridgeProvider;
import net.glacierclient.common.GlacierLogger;
import net.glacierclient.core.module.CoreModule;
import net.glacierclient.core.module.hud.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Version-agnostic client core. A version adapter registers its {@link VersionBridge}, then calls
 * {@link #init()} once and forwards per-tick / per-frame callbacks here. All module logic and
 * rendering go through the bridge, so the same core runs on every supported Minecraft version.
 */
public final class GlacierCore {

    private static GlacierCore instance;
    public static GlacierCore get() {
        if (instance == null) instance = new GlacierCore();
        return instance;
    }

    private final List<CoreModule> modules = new ArrayList<>();
    private boolean initialized;

    // core-measured FPS (fallback when the bridge can't report it)
    private int frameCounter;
    private long lastFpsSample = System.currentTimeMillis();
    private int fps;

    private GlacierCore() {}

    public void init() {
        if (initialized) return;
        initialized = true;
        modules.add(new WatermarkHud());
        modules.add(new CoordinatesHud());
        modules.add(new DirectionHud());
        modules.add(new SpeedHud());
        modules.add(new FpsHud());
        modules.add(new MemoryHud());
        modules.add(new PingHud());
        GlacierLogger.info("GlacierCore initialized with " + modules.size() + " modules");
    }

    public List<CoreModule> getModules() { return Collections.unmodifiableList(modules); }

    public int getFps() { return fps; }

    /** Forward from the adapter's client-tick event. */
    public void onClientTick() {
        if (!VersionBridgeProvider.isReady()) return;
        VersionBridge b = VersionBridgeProvider.get();
        for (CoreModule m : modules) if (m.isEnabled()) m.onTick(b);
    }

    /** Forward from the adapter's HUD-render event; {@code ctx} is the version draw context. */
    public void onRenderHud(Object ctx) {
        if (!VersionBridgeProvider.isReady()) return;
        // sample FPS
        frameCounter++;
        long now = System.currentTimeMillis();
        if (now - lastFpsSample >= 1000) {
            fps = frameCounter;
            frameCounter = 0;
            lastFpsSample = now;
        }
        VersionBridge b = VersionBridgeProvider.get();
        for (CoreModule m : modules) if (m.isEnabled()) m.render(b, ctx);
    }
}
