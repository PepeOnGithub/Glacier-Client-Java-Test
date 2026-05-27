package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class InventoryPeekHUD extends HUDMod {

    private final BooleanSetting showHotbar = new BooleanSetting("Show Hotbar", "Show hotbar slots", true);
    private final BooleanSetting showMain = new BooleanSetting("Show Main", "Show main inventory", true);
    private final BooleanSetting showArmor = new BooleanSetting("Show Armor", "Show armor slots", true);
    private final NumberSetting slotSize = new NumberSetting("Slot Size", "Size of each slot", 8, 24, 16);

    public InventoryPeekHUD() {
        super("Inventory Peek", "Visualizes inventory without opening", 200, 100);
        addSettings(showHotbar, showMain, showArmor, slotSize);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
    @Override public void onTick() {}

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        MinecraftClient mc = MinecraftClient.getInstance();
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        context.drawText(mc.textRenderer, "Inventory", x + 4, y + 2, GlacierTheme.ACCENT, true);
        if (mc.player == null) return;
        int sz = (int) slotSize.getValue();
        int rowX = x + 4, rowY = y + 14;
        // Armor
        if (showArmor.getValue()) {
            for (int i = 0; i < 4; i++) {
                ItemStack stack = mc.player.getInventory().getArmorStack(i);
                context.fill(rowX + i * (sz + 1), rowY, rowX + i * (sz + 1) + sz, rowY + sz, 0x44FFFFFF);
                if (!stack.isEmpty()) context.drawItem(stack, rowX + i * (sz + 1), rowY);
            }
            rowY += sz + 2;
        }
        // Main inventory (first 27 slots)
        if (showMain.getValue()) {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    int slot = 9 + row * 9 + col;
                    if (slot >= 36) break;
                    int sx = rowX + col * (sz + 1);
                    int sy = rowY + row * (sz + 1);
                    context.fill(sx, sy, sx + sz, sy + sz, 0x44FFFFFF);
                    ItemStack stack = mc.player.getInventory().getStack(slot);
                    if (!stack.isEmpty()) context.drawItem(stack, sx, sy);
                }
            }
            rowY += 3 * (sz + 1) + 2;
        }
        // Hotbar
        if (showHotbar.getValue()) {
            for (int i = 0; i < 9; i++) {
                int sx = rowX + i * (sz + 1);
                boolean selected = mc.player.getInventory().selectedSlot == i;
                context.fill(sx, rowY, sx + sz, rowY + sz, selected ? 0x88FFFFFF : 0x44FFFFFF);
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (!stack.isEmpty()) context.drawItem(stack, sx, rowY);
            }
        }
    }
}
