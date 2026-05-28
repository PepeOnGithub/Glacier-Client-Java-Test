package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;

public class WeaponTrails extends GlacierMod {

    private final ModeSetting style = new ModeSetting("Style", "Trail style", "Particles", "Particles", "Line", "Glow");
    private final ColorSetting color = new ColorSetting("Color", "Trail color", 0xFF7289DA);
    private final NumberSetting duration = new NumberSetting("Duration", "Trail duration (seconds)", 0.1, 1.0, 0.3);
    private final BooleanSetting rainbowMode = new BooleanSetting("Rainbow Mode", "Rainbow cycling trail", false);

    private float hue = 0f;

    public WeaponTrails() {
        super("Weapon Trails", "Show particle trails when swinging weapons", Category.PVP);
        addSettings(style, color, duration, rainbowMode);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        if (rainbowMode.getValue()) hue = (hue + 0.02f) % 1f;
    }

    public int getTrailColor() {
        if (rainbowMode.getValue()) return 0xFF000000 | java.awt.Color.HSBtoRGB(hue, 1f, 1f);
        return color.getValue();
    }

    public String getStyle() { return style.getValue(); }
    public float getDuration() { return (float)(double) duration.getValue(); }
}
