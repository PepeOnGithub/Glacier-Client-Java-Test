package net.glacierclient.modules.render;

import net.glacierclient.core.module.Category;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.settings.NumberSetting;

/**
 * Hold-to-zoom (OptiFine-style). The actual FOV change + key handling lives in
 * {@link net.glacierclient.GlacierClient} (it owns the dedicated, rebindable zoom key); this module
 * exposes the toggle + zoom amount and is fully server-safe (only changes client FOV).
 */
public final class Zoom extends GlacierMod {

    private final NumberSetting level = new NumberSetting("Zoom Level", "FOV divisor while the zoom key is held", 2.0, 10.0, 4.0, 0.5);

    public Zoom() {
        super("Zoom", "Hold the zoom key (default C) to zoom in", Category.RENDER);
        addSettings(level);
    }

    /** Divisor applied to the player's FOV while zooming (higher = closer). */
    public double getDivisor() { return level.get(); }
}
