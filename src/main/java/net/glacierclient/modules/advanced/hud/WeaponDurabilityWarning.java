package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class WeaponDurabilityWarning extends HUDMod {

    private final NumberSetting threshold = new NumberSetting("Threshold", "Durability warning threshold", 10, 200, 50);
    private final BooleanSetting soundAlert = new BooleanSetting("Sound Alert", "Play sound on low durability", true);
    private final ColorSetting warningColor = new ColorSetting("Warning Color", "Alert color", 0xFFF04747);
    private final BooleanSetting showAll = new BooleanSetting("Show All", "Show all items, not just low", false);

    private boolean soundPlayed = false;

    public WeaponDurabilityWarning() {
        super("Weapon Durability", "Alert when weapon/tool durability is low", 180, 30);
        addSettings(threshold, soundAlert, warningColor, showAll);
    }

    @Override public void onEnable() { soundPlayed = false; }
    @Override public void onDisable() {}
    @Override public void onTick() {}

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        MinecraftClient mc = MinecraftClient.getInstance();
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        if (mc.player == null) return;
        ItemStack held = mc.player.getMainHandStack();
        if (held.isEmpty()) {
            context.drawText(mc.textRenderer, "No Item", x + 4, y + 10, GlacierTheme.TEXT_DIM, false);
            return;
        }
        if (!held.isDamageable()) {
            if (showAll.getValue()) {
                context.drawText(mc.textRenderer, held.getName().getString() + " [Unbreakable]", x + 4, y + 10, GlacierTheme.TEXT, false);
            }
            return;
        }
        int remaining = held.getMaxDamage() - held.getDamage();
        boolean warn = remaining <= (int)(double) threshold.getValue();
        int color = warn ? warningColor.getValue() : GlacierTheme.TEXT;
        float pct = remaining / (float) held.getMaxDamage();
        String label = held.getName().getString() + " " + remaining + "/" + held.getMaxDamage();
        context.drawText(mc.textRenderer, label, x + 4, y + 4, color, warn);
        context.fill(x + 4, y + h - 5, x + w - 4, y + h - 2, 0x44FFFFFF);
        int barColor = pct > 0.5f ? 0xFF43B581 : pct > 0.25f ? 0xFFFAA61A : 0xFFF04747;
        context.fill(x + 4, y + h - 5, x + 4 + (int) ((w - 8) * pct), y + h - 2, barColor);
    }
}
