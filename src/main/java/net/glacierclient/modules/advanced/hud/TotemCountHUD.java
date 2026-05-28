package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Items;

public class TotemCountHUD extends HUDMod {

    private final NumberSetting warnThreshold = new NumberSetting("Warn Threshold", "Totem count warning level", 0, 10, 1);
    private final ColorSetting zeroColor = new ColorSetting("Zero Color", "Color when no totems", 0xFFF04747);
    private final BooleanSetting showInHotbar = new BooleanSetting("Show Hotbar", "Highlight totems in hotbar", true);

    private int totemCount = 0;

    public TotemCountHUD() {
        super("Totem Count", "Shows totems of undying in inventory", 80, 20);
        addSettings(warnThreshold, zeroColor, showInHotbar);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}

    @Override
    public void onTick() {
        totemCount = 0;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            var stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) totemCount += stack.getCount();
        }
        // Check offhand
        if (mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING)
            totemCount += mc.player.getOffHandStack().getCount();
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        var tr = MinecraftClient.getInstance().textRenderer;
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        boolean warn = totemCount <= (int) warnThreshold.getValue();
        int color = totemCount == 0 ? zeroColor.getValue() : (warn ? 0xFFFAA61A : GlacierTheme.TEXT);
        String text = "Totems: " + totemCount;
        context.drawText(tr, text, x + 4, y + 6, color, warn);
    }
}
