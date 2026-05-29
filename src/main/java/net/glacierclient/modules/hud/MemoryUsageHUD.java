package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class MemoryUsageHUD extends HUDMod {

    private final ModeSetting style = new ModeSetting("Style", "Display style", "Both", "Bar", "Text", "Both");
    private final ColorSetting barColor = new ColorSetting("Bar Color", "Color of memory bar", GlacierTheme.ACCENT);
    private final BooleanSetting showMax = new BooleanSetting("Show Max", "Show max memory", true);

    public MemoryUsageHUD() {
        super("Memory Usage", "Shows JVM memory usage", 140, 30);
        addSettings(style, barColor, showMax);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    @Override
    public void render(DrawContext context, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null) return;
        Runtime rt = Runtime.getRuntime();
        long used = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        long max = rt.maxMemory() / (1024 * 1024);
        int x = getX() + 2, y = getY() + 2;
        boolean showText = "Text".equals(style.getValue()) || "Both".equals(style.getValue());
        boolean showBar = "Bar".equals(style.getValue()) || "Both".equals(style.getValue());
        if (showText) {
            String text = used + "MB" + (showMax.getValue() ? " / " + max + "MB" : "");
            context.drawText(mc.textRenderer, text, x, y, GlacierTheme.TEXT, false);
            y += 10;
        }
        if (showBar) {
            int barW = getWidth() - 4;
            int filled = max > 0 ? (int)(used * barW / max) : 0;
            context.fill(x, y, x + barW, y + 6, GlacierTheme.BG_PANEL);
            context.fill(x, y, x + filled, y + 6, barColor.getValue());
        }
    }
}
