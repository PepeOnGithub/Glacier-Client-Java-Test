package net.glacierclient.modules.expanded.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;

public class ComboRetainerOverlay extends GlacierMod {

    private final NumberSetting minCombo = new NumberSetting("Min Combo", "Minimum hits to trigger combo overlay", 3, 2, 20);
    private final BooleanSetting soundEffect = new BooleanSetting("Sound Effect", "Play sound on combo milestone", false);
    private final ColorSetting iconColor = new ColorSetting("Icon Color", "Color of combo indicator icon", GlacierTheme.ACCENT);
    private final BooleanSetting showFlame = new BooleanSetting("Show Flame", "Animate flame icon on combo streak", false);

    public ComboRetainerOverlay() {
        super("Combo Retainer", "Icon+sound when combo streak beyond 3 hits", Category.PVP);
        addSettings(minCombo, soundEffect, iconColor, showFlame);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public int getMinCombo() { return (int) minCombo.getValue(); }
    public boolean isSoundEffect() { return soundEffect.getValue(); }
    public int getIconColor() { return iconColor.getValue(); }
    public boolean isShowFlame() { return showFlame.getValue(); }
}
