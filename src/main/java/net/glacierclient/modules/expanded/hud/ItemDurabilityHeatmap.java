package net.glacierclient.modules.expanded.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ItemDurabilityHeatmap extends HUDMod {

    private static final int GREEN = 0xFF55FF55;
    private static final int RED = 0xFFFF5555;

    private final ColorSetting fullColor = new ColorSetting("Full Color", "Color for full durability items", GREEN);
    private final ColorSetting lowColor = new ColorSetting("Low Color", "Color for low durability items", RED);
    private final BooleanSetting showNumbers = new BooleanSetting("Show Numbers", "Show numeric durability values", false);
    private final NumberSetting warnThreshold = new NumberSetting("Warn Threshold", "Durability percent below which to warn", 30, 10, 100);

    public ItemDurabilityHeatmap() {
        super("Durability Heatmap", "Color gradient durability display on HUD icons", 120, 20);
        addSettings(fullColor, lowColor, showNumbers, warnThreshold);
    }

    @Override
    public void render(DrawContext context, float delta) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        MinecraftClient mc = MinecraftClient.getInstance();

        context.fill(x, y, x + w, y + h, 0xCC1E1E2E);

        float durPct = 0.25f; // Placeholder
        int col = durPct < (warnThreshold.getValue() / 100f) ? lowColor.getValue() : fullColor.getValue();

        String label = showNumbers.getValue()
            ? String.format("Dur: %d%%", (int)(durPct * 100))
            : "Dur: LOW";
        context.drawTextWithShadow(mc.textRenderer, label, x + 4, y + 5, col);
    }
}
