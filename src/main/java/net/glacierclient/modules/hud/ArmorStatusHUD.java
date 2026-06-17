package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;

public class ArmorStatusHUD extends HUDMod {

    private final ModeSetting style = new ModeSetting("Style", "Display style", "Both", "Icons", "Numbers", "Both");
    private final BooleanSetting durabilityBar = new BooleanSetting("Durability Bar", "Draw a colored durability bar under each icon", true);
    private final BooleanSetting showDurability = new BooleanSetting("Show Durability", "Show durability values", true);
    private final ColorSetting lowDurabilityColor = new ColorSetting("Low Durability Color", "Color when durability is low", GlacierTheme.RED);

    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };
    private static final String[] LABELS = {"H", "C", "L", "B"};

    public ArmorStatusHUD() {
        super("Armor Status", "Shows armor durability values", 172, 22);
        addSettings(style, showDurability, durabilityBar, lowDurabilityColor);
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
        if (mc.textRenderer == null || mc.player == null) return;
        int x = getX() + 2;
        int y = getY() + 2;
        String mode = style.getValue();
        boolean drawIcon = mode.equals("Icons") || mode.equals("Both");
        boolean drawText = mode.equals("Numbers") || mode.equals("Both");
        boolean barRow = drawIcon && durabilityBar.getValue();
        int cellW = drawIcon ? (drawText ? 42 : 20) : 26;
        int rowH = drawIcon ? (barRow ? 20 : 16) : 9;
        drawBackground(context, x, y, SLOTS.length * cellW, rowH);
        for (int i = 0; i < SLOTS.length; i++) {
            ItemStack stack = mc.player.getEquippedStack(SLOTS[i]);
            if (stack.isEmpty()) continue;
            int cx = x + i * cellW;
            int color = getTextColor();
            int maxDur = stack.getMaxDamage();
            int curDur = maxDur - stack.getDamage();
            boolean damageable = maxDur > 0;
            float pct = damageable ? (float) curDur / maxDur : 1f;
            if (showDurability.getValue() && damageable && pct < 0.25f) color = lowDurabilityColor.getValue();
            if (drawIcon) {
                context.drawItem(stack, cx, y);
                // Colored durability bar beneath the icon.
                if (barRow && damageable) {
                    int barColor = pct > 0.5f ? 0xFF55C572 : pct > 0.25f ? 0xFFE3B341 : lowDurabilityColor.getValue();
                    int by = y + 17;
                    context.fill(cx, by, cx + 16, by + 2, 0x66000000);
                    context.fill(cx, by, cx + (int) (16 * pct), by + 2, barColor);
                }
                cx += 18;
            }
            if (drawText) {
                String s;
                if (!showDurability.getValue()) s = LABELS[i];
                else if (!damageable) s = drawIcon ? "" : LABELS[i];
                else s = drawIcon ? String.valueOf(curDur) : LABELS[i] + ":" + curDur;
                if (!s.isEmpty())
                    context.drawText(mc.textRenderer, s, cx, y + (drawIcon ? 4 : 0), color, hasShadow());
            }
        }
    }
}
