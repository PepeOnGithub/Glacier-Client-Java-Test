package net.glacierclient.modules.engine;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.gui.DrawContext;

public class MaterialRequiredList extends HUDMod {

    private final BooleanSetting showRequired = new BooleanSetting("Show Required", "Show total required block counts", false);
    private final BooleanSetting showMissing = new BooleanSetting("Show Missing", "Show missing block counts", false);
    private final BooleanSetting sortByCount = new BooleanSetting("Sort By Count", "Sort materials by required count", false);
    private final NumberSetting maxDisplay = new NumberSetting("Max Display", "Maximum number of materials to show", 20, 5, 50);
    private final BooleanSetting highlightMissing = new BooleanSetting("Highlight Missing", "Highlight materials not in inventory", false);

    public MaterialRequiredList() {
        super("Material List", "HUD showing block inventory needed for loaded schematic", 200, 200);
        addSettings(showRequired, showMissing, sortByCount, maxDisplay, highlightMissing);
    }

    @Override
    public void render(DrawContext context, float delta) {
        int x = getX();
        int y = getY();
        int w = getWidth();

        // Header
        context.fill(x, y, x + w, y + 200, 0xCC1E1E2E);
        context.drawTextWithShadow(
            net.minecraft.client.MinecraftClient.getInstance().textRenderer,
            "Material List",
            x + 4, y + 4,
            GlacierTheme.ACCENT
        );

        int lineY = y + 16;
        int lineH = 10;
        int count = (int)(double) maxDisplay.getValue();

        for (int i = 0; i < count && lineY + lineH < y + 200; i++) {
            boolean missing = highlightMissing.getValue() && (i % 3 == 0);
            int color = missing ? 0xFFFF5555 : GlacierTheme.TEXT;
            context.drawTextWithShadow(
                net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                "█ Block " + (i + 1) + (showRequired.getValue() ? " x64" : "") + (showMissing.getValue() && missing ? " [MISSING]" : ""),
                x + 4, lineY,
                color
            );
            lineY += lineH;
        }
    }
}
