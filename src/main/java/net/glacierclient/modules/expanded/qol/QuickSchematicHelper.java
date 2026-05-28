package net.glacierclient.modules.expanded.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;

public class QuickSchematicHelper extends GlacierMod {

    private final BooleanSetting showGrid = new BooleanSetting("Show Grid", "Render building grid overlay", false);
    private final BooleanSetting snapToGrid = new BooleanSetting("Snap To Grid", "Snap block placement to grid", true);
    private final NumberSetting gridSize = new NumberSetting("Grid Size", "Grid cell size in blocks", 1, 1, 8);
    private final ColorSetting gridColor = new ColorSetting("Grid Color", "Color of grid overlay lines", 0x4A7289DA);

    public QuickSchematicHelper() {
        super("Schematic Helper", "Snap-to-grid building assistant overlay", Category.QOL);
        addSettings(showGrid, snapToGrid, gridSize, gridColor);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public boolean isShowGrid() { return showGrid.getValue(); }
    public boolean isSnapToGrid() { return snapToGrid.getValue(); }
    public int getGridSize() { return (int) gridSize.getValue(); }
    public int getGridColor() { return gridColor.getValue(); }
}
