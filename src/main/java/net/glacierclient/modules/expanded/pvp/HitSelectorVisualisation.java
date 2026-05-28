package net.glacierclient.modules.expanded.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;

public class HitSelectorVisualisation extends GlacierMod {

    private final ColorSetting circleColor = new ColorSetting("Circle Color", "Color of the hit prediction circle", 0x887289DA);
    private final NumberSetting radius = new NumberSetting("Radius", "Radius of prediction circle in pixels", 12, 4, 32);
    private final BooleanSetting showOnEnemy = new BooleanSetting("Show On Enemy", "Only display when targeting an enemy", false);
    private final NumberSetting thickness = new NumberSetting("Thickness", "Thickness of circle outline", 2.0, 1.0, 4.0);

    public HitSelectorVisualisation() {
        super("Hit Selector", "Ping-based prediction circle near crosshair", Category.PVP);
        addSettings(circleColor, radius, showOnEnemy, thickness);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public int getCircleColor() { return circleColor.getValue(); }
    public int getRadius() { return (int)(double) radius.getValue(); }
    public boolean isShowOnEnemy() { return showOnEnemy.getValue(); }
    public double getThickness() { return thickness.getValue(); }
}
