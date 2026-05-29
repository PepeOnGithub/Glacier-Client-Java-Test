package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Items;

public class ArrowCountNotifier extends HUDMod {

    private final NumberSetting warnThreshold = new NumberSetting("Warn Threshold", "Arrow count warning level", 0, 64, 16);
    private final ColorSetting lowColor = new ColorSetting("Low Color", "Color when arrows are low", 0xFFF04747);
    private final BooleanSetting showMax = new BooleanSetting("Show Max", "Show max arrow capacity", false);

    private int arrowCount = 0;

    public ArrowCountNotifier() {
        super("Arrow Count", "Shows arrow count in inventory", 80, 20);
        addSettings(warnThreshold, lowColor, showMax);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}

    @Override
    public void onTick() {
        arrowCount = 0;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            var stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.ARROW || stack.getItem() == Items.SPECTRAL_ARROW
                    || stack.getItem() == Items.TIPPED_ARROW) {
                arrowCount += stack.getCount();
            }
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        var tr = MinecraftClient.getInstance().textRenderer;
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        boolean warn = arrowCount <= (int)(double) warnThreshold.getValue();
        int color = warn ? lowColor.getValue() : GlacierTheme.TEXT;
        String text = "Arrows: " + arrowCount + (showMax.getValue() ? "/64" : "");
        context.drawText(tr, text, x + 4, y + 6, color, warn);
    }
}
