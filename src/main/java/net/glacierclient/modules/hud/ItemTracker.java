package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.settings.StringSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ItemTracker extends HUDMod {

    private final StringSetting trackedItem = new StringSetting("Tracked Item", "Item ID to track", "diamond");
    private final BooleanSetting countAll = new BooleanSetting("Count All", "Count all matching items", true);
    private final NumberSetting maxDisplay = new NumberSetting("Max Display", "Max items to show", 1, 10, 5);

    private final Map<String, Integer> counts = new HashMap<>();

    public ItemTracker() {
        super("Item Tracker", "Tracks item count in inventory", 140, 60);
        addSettings(trackedItem, countAll, maxDisplay);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        counts.clear();
        String target = trackedItem.getValue().toLowerCase();
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            String id = Registries.ITEM.getId(stack.getItem()).getPath();
            if (countAll.getValue() ? id.contains(target) : id.equals(target)) {
                counts.merge(id, stack.getCount(), Integer::sum);
            }
        }
    }

    @Override
    public void render(DrawContext context, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null) return;
        int x = getX() + 2, y = getY() + 2;
        int shown = 0;
        int maxD = (int)(double) maxDisplay.getValue();
        int lines = Math.max(1, Math.min(counts.size(), maxD));
        drawBackground(context, x, y, getWidth() - 4, lines * 10);
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (shown >= maxD) break;
            context.drawText(mc.textRenderer, entry.getKey() + ": " + entry.getValue(), x, y + shown * 10, getTextColor(), hasShadow());
            shown++;
        }
        if (shown == 0) {
            context.drawText(mc.textRenderer, trackedItem.getValue() + ": 0", x, y, GlacierTheme.TEXT_DIM, hasShadow());
        }
    }
}
