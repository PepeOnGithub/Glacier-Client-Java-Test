package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class CustomCrosshair extends GlacierMod {

    private final ModeSetting style = new ModeSetting("Style", "Crosshair shape", "Cross", "Default", "Cross", "Dot", "Circle", "Arrow");
    private final NumberSetting size = new NumberSetting("Size", "Crosshair size", 4, 32, 8);
    private final ColorSetting color = new ColorSetting("Color", "Crosshair color", GlacierTheme.TEXT);
    private final BooleanSetting dynamicColor = new BooleanSetting("Dynamic Color", "Change color based on target", false);
    private final NumberSetting thickness = new NumberSetting("Thickness", "Line thickness", 1, 4, 1);

    public CustomCrosshair() {
        super("Custom Crosshair", "Replace vanilla crosshair with custom styles", Category.RENDER);
        addSettings(style, size, color, dynamicColor, thickness);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public void render(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int cx = mc.getWindow().getScaledWidth() / 2;
        int cy = mc.getWindow().getScaledHeight() / 2;
        int sz = (int) size.getValue();
        int thick = (int) thickness.getValue();
        int c = color.getValue();
        String s = style.getValue();
        if ("Default".equals(s)) return;
        if ("Dot".equals(s)) {
            context.fill(cx - thick, cy - thick, cx + thick, cy + thick, c);
        } else if ("Cross".equals(s)) {
            context.fill(cx - sz, cy - thick, cx + sz, cy + thick, c);
            context.fill(cx - thick, cy - sz, cx + thick, cy + sz, c);
        } else if ("Circle".equals(s)) {
            for (int i = 0; i < 360; i += 10) {
                double rad = Math.toRadians(i);
                int px = cx + (int)(Math.cos(rad) * sz);
                int py = cy + (int)(Math.sin(rad) * sz);
                context.fill(px, py, px + thick, py + thick, c);
            }
        }
    }
}
