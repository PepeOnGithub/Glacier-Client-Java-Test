package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class HeldItemLargeDisplay extends HUDMod {

    private final NumberSetting scale = new NumberSetting("Scale", "Text scale", 0.5, 3.0, 1.5);
    private final NumberSetting fadeTime = new NumberSetting("Fade Time", "Fade out after ms", 1000, 10000, 3000);
    private final BooleanSetting showDurability = new BooleanSetting("Show Durability", "Show item durability", true);
    private final ColorSetting textColor = new ColorSetting("Text Color", "Item name color", GlacierTheme.TEXT);

    private ItemStack lastStack = ItemStack.EMPTY;
    private String displayName = "";
    private long shownAt = 0;

    public HeldItemLargeDisplay() {
        super("Held Item Display", "Large display of current held item name", 200, 40);
        addSettings(scale, fadeTime, showDurability, textColor);
    }

    @Override public void onEnable() { lastStack = ItemStack.EMPTY; }
    @Override public void onDisable() {}

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        ItemStack cur = mc.player.getMainHandStack();
        if (!ItemStack.areEqual(cur, lastStack)) {
            lastStack = cur.copy();
            displayName = cur.isEmpty() ? "" : cur.getName().getString();
            shownAt = System.currentTimeMillis();
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        MinecraftClient mc = MinecraftClient.getInstance();
        if (displayName.isEmpty()) return;
        long elapsed = System.currentTimeMillis() - shownAt;
        long fade = (long)(double) fadeTime.getValue();
        if (elapsed > fade) return;
        float alpha = elapsed < fade - 1000 ? 1.0f : (fade - elapsed) / 1000.0f;
        int a = Math.max(0, Math.min(255, (int) (alpha * 255)));
        int col = (a << 24) | (textColor.getValue() & 0x00FFFFFF);
        context.fill(x, y, x + w, y + h, (a / 2) << 24 | 0x1A1A2E);
        float sc = (float)(double) scale.getValue();
        var matrix = context.getMatrices();
        matrix.push();
        matrix.translate(x + 4, y + h / 2f - 4, 0);
        matrix.scale(sc, sc, 1);
        context.drawText(mc.textRenderer, displayName, 0, 0, col, true);
        if (showDurability.getValue() && lastStack.isDamageable()) {
            int rem = lastStack.getMaxDamage() - lastStack.getDamage();
            context.drawText(mc.textRenderer, rem + "/" + lastStack.getMaxDamage(), 0, 10, col, false);
        }
        matrix.pop();
    }
}
