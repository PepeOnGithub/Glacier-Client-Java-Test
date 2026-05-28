package net.glacierclient.modules.engine;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.settings.ColorSetting;

public class LegitSchematicOutline extends GlacierMod {

    private final BooleanSetting showGhostBlocks = new BooleanSetting("Show Ghost Blocks", "Render ghost block overlays from schematic", true);
    private final NumberSetting opacity = new NumberSetting("Opacity", "Ghost block overlay opacity", 120, 20, 200);
    private final ColorSetting ghostColor = new ColorSetting("Ghost Color", "Tint color for ghost block overlay", 0x7F7289DA);
    private final BooleanSetting showMissingOnly = new BooleanSetting("Show Missing Only", "Only show blocks not yet placed", false);
    private final BooleanSetting snapToGrid = new BooleanSetting("Snap To Grid", "Snap schematic placement to block grid", false);

    public LegitSchematicOutline() {
        super("Schematic Outline", "Renders .litematica/.schem files as ghost block overlays", Category.ENGINE);
        addSettings(showGhostBlocks, opacity, ghostColor, showMissingOnly, snapToGrid);
    }

    @Override
    public void onEnable() {
        // Load and parse active schematic file
    }

    @Override
    public void onDisable() {
        // Clear ghost block overlays
    }

    public boolean isShowGhostBlocks() { return showGhostBlocks.getValue(); }
    public int getOpacity() { return (int) opacity.getValue(); }
    public int getGhostColor() { return ghostColor.getValue(); }
    public boolean isShowMissingOnly() { return showMissingOnly.getValue(); }
    public boolean isSnapToGrid() { return snapToGrid.getValue(); }
}
