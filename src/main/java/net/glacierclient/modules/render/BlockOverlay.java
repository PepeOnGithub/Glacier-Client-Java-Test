package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;

public class BlockOverlay extends GlacierMod {

    private final ColorSetting color = new ColorSetting("Color", "Block selection outline color", 0xFF7289DA);
    private final NumberSetting lineWidth = new NumberSetting("Line Width", "Outline line width", 1.0, 4.0, 2.0);
    private final BooleanSetting fill = new BooleanSetting("Fill", "Fill selected block", false);
    private final ColorSetting fillColor = new ColorSetting("Fill Color", "Fill color", 0x1A7289DA);
    private final ModeSetting style = new ModeSetting("Style", "Overlay style", "Outline", "Box", "Outline", "Corners");

    public BlockOverlay() {
        super("Block Overlay", "Customize the block selection outline", Category.RENDER);
        addSettings(color, lineWidth, fill, fillColor, style);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public int getColor() { return color.getValue(); }
    public float getLineWidth() { return (float)(double) lineWidth.getValue(); }
    public boolean isFill() { return fill.getValue(); }
    public int getFillColor() { return fillColor.getValue(); }
    public String getStyle() { return style.getValue(); }
}
