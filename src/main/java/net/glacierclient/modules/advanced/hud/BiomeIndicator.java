package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

public class BiomeIndicator extends HUDMod {

    private final BooleanSetting showTemperature = new BooleanSetting("Show Temp", "Show biome temperature", false);
    private final BooleanSetting showHumidity = new BooleanSetting("Show Humidity", "Show biome humidity", false);
    private final ModeSetting style = new ModeSetting("Style", "Display style", new String[]{"Simple", "Full"}, "Simple");

    private String biomeName = "Unknown";
    private float temperature = 0;
    private float humidity = 0;

    public BiomeIndicator() {
        super("Biome Indicator", "Shows current biome name", 160, 20);
        addSettings(showTemperature, showHumidity, style);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        RegistryEntry<Biome> biomeEntry = mc.world.getBiome(mc.player.getBlockPos());
        biomeEntry.getKey().ifPresent(key -> {
            String path = key.getValue().getPath();
            biomeName = path.replace('_', ' ');
            // capitalize
            if (!biomeName.isEmpty()) {
                biomeName = Character.toUpperCase(biomeName.charAt(0)) + biomeName.substring(1);
            }
        });
        temperature = biomeEntry.value().getTemperature();
        humidity = biomeEntry.value().getDownfall();
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        var tr = MinecraftClient.getInstance().textRenderer;
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        String text = biomeName;
        if ("Full".equals(style.getValue())) {
            if (showTemperature.getValue()) text += " T:" + String.format("%.1f", temperature);
            if (showHumidity.getValue()) text += " H:" + String.format("%.1f", humidity);
        }
        context.drawText(tr, text, x + 4, y + 6, GlacierTheme.TEXT, false);
    }
}
