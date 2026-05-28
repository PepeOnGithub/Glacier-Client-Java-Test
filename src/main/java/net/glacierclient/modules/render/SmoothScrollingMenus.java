package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.NumberSetting;

public class SmoothScrollingMenus extends GlacierMod {

    private final NumberSetting friction = new NumberSetting("Friction", "Scroll deceleration factor", 0.1, 0.9, 0.85);
    private final NumberSetting sensitivity = new NumberSetting("Sensitivity", "Scroll input sensitivity", 0.5, 3.0, 1.5);

    private double scrollVelocity = 0.0;
    private double scrollOffset = 0.0;

    public SmoothScrollingMenus() {
        super("Smooth Scrolling", "Add momentum-based smooth scrolling to menus", Category.RENDER);
        addSettings(friction, sensitivity);
    }

    @Override
    public void onEnable() {
        scrollVelocity = 0.0;
        scrollOffset = 0.0;
    }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        scrollVelocity *= friction.getValue();
        scrollOffset += scrollVelocity;
        if (Math.abs(scrollVelocity) < 0.001) scrollVelocity = 0.0;
    }

    public void applyScroll(double amount) {
        scrollVelocity += amount * sensitivity.getValue();
    }

    public double getScrollOffset() { return scrollOffset; }
}
