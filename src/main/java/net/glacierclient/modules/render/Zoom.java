package net.glacierclient.modules.render;

import net.glacierclient.core.module.Category;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

/**
 * Hold-to-zoom (OptiFine-style). The actual FOV change + key handling lives in
 * {@link net.glacierclient.GlacierClient} (it owns the dedicated, rebindable zoom key); this module
 * exposes the toggle, zoom amount and optional scroll-wheel adjustment. Fully server-safe (only
 * changes client FOV).
 */
public final class Zoom extends GlacierMod {

    public static final double MIN = 1.5, MAX = 50.0;

    private final NumberSetting level = new NumberSetting("Zoom Level", "FOV divisor while the zoom key is held", MIN, MAX, 4.0, 0.5);
    private final BooleanSetting scroll = new BooleanSetting("Scroll Adjust", "Adjust zoom with the scroll wheel while zooming", true);
    private final NumberSetting scrollStep = new NumberSetting("Scroll Step", "Zoom change per scroll notch", 0.25, 5.0, 1.0, 0.25);
    private final BooleanSetting smooth = new BooleanSetting("Smooth", "Ease the FOV in/out instead of snapping", true);

    private double dynamic = 4.0; // live divisor while scroll-adjusting

    public Zoom() {
        super("Zoom", "Hold the zoom key (default C) to zoom in", Category.RENDER);
        addSettings(level, scroll, scrollStep, smooth);
    }

    public boolean isScrollAdjust() { return scroll.getValue(); }
    public boolean isSmooth() { return smooth.getValue(); }

    /** Reset the dynamic divisor back to the configured level (called when a zoom begins). */
    public void resetDynamic() { dynamic = level.get(); }

    /** Effective divisor applied to FOV (higher = closer). */
    public double getDivisor() { return scroll.getValue() ? dynamic : level.get(); }

    /** Feed a scroll notch (positive = scroll up = zoom in further). Returns true if it was consumed. */
    public boolean applyScroll(double notches) {
        if (!scroll.getValue()) return false;
        dynamic = Math.max(MIN, Math.min(MAX, dynamic + notches * scrollStep.get()));
        return true;
    }
}
