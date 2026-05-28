package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;

public class CustomBlockBreakParticles extends GlacierMod {

    private final BooleanSetting removeParticles = new BooleanSetting("Remove Particles", "Completely remove block break particles", false);
    private final ModeSetting style = new ModeSetting("Style", "Particle style", "Minimal", "Default", "Minimal", "Big", "Colored");
    private final ColorSetting color = new ColorSetting("Color", "Custom particle color", GlacierTheme.ACCENT);
    private final NumberSetting count = new NumberSetting("Count", "Number of particles", 0, 100, 10);

    public CustomBlockBreakParticles() {
        super("Block Break Particles", "Customize block break particle effects", Category.PVP);
        addSettings(removeParticles, style, color, count);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isRemoved() { return removeParticles.getValue(); }
    public String getStyle() { return style.getValue(); }
    public int getParticleColor() { return color.getValue(); }
    public int getParticleCount() { return (int)(double) count.getValue(); }
}
