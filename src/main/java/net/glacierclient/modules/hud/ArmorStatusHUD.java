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

    private final ModeSetting style = new ModeSetting("Style", "Display style", "Numbers", "Icons", "Numbers", "Both");
    private final BooleanSetting showDurability = new BooleanSetting("Show Durability", "Show durability values", true);
    private final ColorSetting lowDurabilityColor = new ColorSetting("Low Durability Color", "Color when durability is low", GlacierTheme.RED);

    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };
    private static final String[] LABELS = {"H", "C", "L", "B"};

    public ArmorStatusHUD() {
        super("Armor Status", "Shows armor durability values", 100, 20);
        addSettings(style, showDurability, lowDurabilityColor);
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
        for (int i = 0; i < SLOTS.length; i++) {
            ItemStack stack = mc.player.getEquippedStack(SLOTS[i]);
            if (stack.isEmpty()) continue;
            int color = GlacierTheme.TEXT;
            if (showDurability.getValue()) {
                int maxDur = stack.getMaxDamage();
                int curDur = maxDur - stack.getDamage();
                float pct = maxDur > 0 ? (float) curDur / maxDur : 1f;
                if (pct < 0.25f) color = lowDurabilityColor.getValue();
                String durStr = LABELS[i] + ":" + curDur;
                context.drawText(mc.textRenderer, durStr, x + i * 24, y, color, false);
            } else {
                context.drawText(mc.textRenderer, LABELS[i], x + i * 24, y, color, false);
            }
        }
    }
}
