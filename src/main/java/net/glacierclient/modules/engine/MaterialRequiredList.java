package net.glacierclient.modules.engine;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialRequiredList extends HUDMod {

    private final BooleanSetting showRequired = new BooleanSetting("Show Required", "Show total required block counts", false);
    private final BooleanSetting showMissing = new BooleanSetting("Show Missing", "Show missing block counts", false);
    private final BooleanSetting sortByCount = new BooleanSetting("Sort By Count", "Sort materials by required count", false);
    private final NumberSetting maxDisplay = new NumberSetting("Max Display", "Maximum number of materials to show", 20, 5, 50);
    private final BooleanSetting highlightMissing = new BooleanSetting("Highlight Missing", "Highlight materials not in inventory", false);

    public MaterialRequiredList() {
        super("Material List", "HUD showing tracked inventory material counts", 200, 200);
        addSettings(showRequired, showMissing, sortByCount, maxDisplay, highlightMissing);
    }

    @Override
    public void render(DrawContext context, float delta) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        MinecraftClient mc = MinecraftClient.getInstance();

        drawBackground(context, x, y, w, h);
        context.drawTextWithShadow(mc.textRenderer, "Material List", x + 4, y + 4, GlacierTheme.ACCENT);

        List<Map.Entry<String, Integer>> materials = materials(mc);
        if (sortByCount.getValue()) materials.sort(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()));

        int lineY = y + 16;
        int lineH = 10;
        int count = Math.min((int)(double) maxDisplay.getValue(), materials.size());
        for (int i = 0; i < count && lineY + lineH < y + h; i++) {
            Map.Entry<String, Integer> entry = materials.get(i);
            boolean missing = entry.getValue() <= 0;
            if (missing && !showMissing.getValue()) continue;
            int color = missing && highlightMissing.getValue() ? 0xFFFF5555 : GlacierTheme.TEXT;
            String text = entry.getKey() + (showRequired.getValue() ? " x" + entry.getValue() : "");
            context.drawTextWithShadow(mc.textRenderer, text, x + 4, lineY, color);
            lineY += lineH;
        }
    }

    private List<Map.Entry<String, Integer>> materials(MinecraftClient mc) {
        Map<String, Integer> counts = new HashMap<>();
        if (mc.player != null) {
            for (ItemStack stack : mc.player.getInventory().main) {
                if (!stack.isEmpty()) counts.merge(stack.getName().getString(), stack.getCount(), Integer::sum);
            }
        }
        return new ArrayList<>(counts.entrySet());
    }
}
