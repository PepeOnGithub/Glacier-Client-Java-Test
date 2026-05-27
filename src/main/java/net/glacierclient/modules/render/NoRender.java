package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;

public class NoRender extends GlacierMod {

    private final BooleanSetting noExplosions = new BooleanSetting("No Explosions", "Hide explosion particles", false);
    private final BooleanSetting noParticles = new BooleanSetting("No Particles", "Hide all particles", false);
    private final BooleanSetting noFirework = new BooleanSetting("No Firework", "Hide firework particles", true);
    private final BooleanSetting noPotionParticles = new BooleanSetting("No Potion Particles", "Hide potion effect particles", false);
    private final BooleanSetting noBlockBreak = new BooleanSetting("No Block Break", "Hide block break particles", false);
    private final BooleanSetting noWeather = new BooleanSetting("No Weather", "Hide rain and snow", false);

    public NoRender() {
        super("No Render", "Toggle various visual elements off", Category.RENDER);
        addSettings(noExplosions, noParticles, noFirework, noPotionParticles, noBlockBreak, noWeather);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isNoExplosions() { return noExplosions.getValue(); }
    public boolean isNoParticles() { return noParticles.getValue(); }
    public boolean isNoFirework() { return noFirework.getValue(); }
    public boolean isNoPotionParticles() { return noPotionParticles.getValue(); }
    public boolean isNoBlockBreak() { return noBlockBreak.getValue(); }
    public boolean isNoWeather() { return noWeather.getValue(); }
}
