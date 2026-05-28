package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;

public class WeatherController extends GlacierMod {

    private final ModeSetting weather = new ModeSetting("Weather", "Client-side weather override", "Clear", "Clear", "Rain", "Snow", "Thunder");
    private final BooleanSetting permanentClear = new BooleanSetting("Permanent Clear", "Always force clear weather", false);

    public WeatherController() {
        super("Weather Controller", "Control client-side weather appearance", Category.RENDER);
        addSettings(weather, permanentClear);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public String getWeather() {
        if (permanentClear.getValue()) return "Clear";
        return weather.getValue();
    }
}
