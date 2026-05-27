package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.ModeSetting;

public class ColorPaletteSelector extends GlacierMod {

    private final ModeSetting palette = new ModeSetting("Palette", "Color palette preset", "Glacier", "Glacier", "Neon", "Pastel", "Custom");
    private final BooleanSetting syncWithTheme = new BooleanSetting("Sync With Theme", "Sync palette with theme", true);
    private final ColorSetting customAccent = new ColorSetting("Custom Accent", "Custom accent color", 0xFF7289DA);

    private static final int[][] PALETTES = {
        {0xFF7289DA, 0xFFFFFFFF, 0xFF23272A}, // Glacier
        {0xFF00FFFF, 0xFFFF00FF, 0xFF1A1A2E}, // Neon
        {0xFFFFB3BA, 0xFFFFDFBA, 0xFFFFFFBA}, // Pastel
    };

    public ColorPaletteSelector() {
        super("Color Palette Selector", "Choose and apply a color palette", Category.QOL);
        addSettings(palette, syncWithTheme, customAccent);
    }

    @Override
    public void onEnable() { applyPalette(); }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    private void applyPalette() {
        // Palette application handled by theme system
    }

    public int getAccentColor() {
        if ("Custom".equals(palette.getValue())) return customAccent.getValue();
        return PALETTES[Math.max(0, java.util.Arrays.asList("Glacier","Neon","Pastel").indexOf(palette.getValue()))][0];
    }
}
