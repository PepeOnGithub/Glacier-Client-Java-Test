package net.glacierclient.modules.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class Keystrokes extends HUDMod {

    private final BooleanSetting showMouseButtons = new BooleanSetting("Mouse Buttons", "Show LMB/RMB", true);
    private final BooleanSetting showSpacebar = new BooleanSetting("Spacebar", "Show spacebar key", true);
    private final NumberSetting keySize = new NumberSetting("Key Size", "Size of each key display", 10, 30, 20);
    private final ColorSetting pressedColor = new ColorSetting("Pressed Color", "Color when key is pressed", GlacierTheme.ACCENT);

    public Keystrokes() {
        super("Keystrokes", "Shows keyboard and mouse button presses", 90, 70);
        addSettings(showMouseButtons, showSpacebar, keySize, pressedColor);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    private void drawKey(DrawContext context, MinecraftClient mc, String label, int x, int y, int w, int h, boolean pressed) {
        int bg = pressed ? pressedColor.getValue() : GlacierTheme.BG_PANEL;
        int fg = pressed ? GlacierTheme.BG : GlacierTheme.TEXT;
        context.fill(x, y, x + w, y + h, bg);
        int tx = x + (w - mc.textRenderer.getWidth(label)) / 2;
        int ty = y + (h - 8) / 2;
        context.drawText(mc.textRenderer, label, tx, ty, fg, false);
    }

    @Override
    public void render(DrawContext context, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null || mc.options == null) return;
        int ks = (int) keySize.getValue();
        int gap = 2;
        int bx = getX();
        int by = getY();
        boolean w = mc.options.forwardKey.isPressed();
        boolean a = mc.options.leftKey.isPressed();
        boolean s = mc.options.backKey.isPressed();
        boolean d = mc.options.rightKey.isPressed();
        // Row 1: W centered
        drawKey(context, mc, "W", bx + ks + gap, by, ks, ks, w);
        // Row 2: A S D
        drawKey(context, mc, "A", bx, by + ks + gap, ks, ks, a);
        drawKey(context, mc, "S", bx + ks + gap, by + ks + gap, ks, ks, s);
        drawKey(context, mc, "D", bx + (ks + gap) * 2, by + ks + gap, ks, ks, d);
        // Row 3: LMB / RMB
        if (showMouseButtons.getValue()) {
            boolean lmb = mc.options.attackKey.isPressed();
            boolean rmb = mc.options.useKey.isPressed();
            drawKey(context, mc, "LMB", bx, by + (ks + gap) * 2, ks, ks, lmb);
            drawKey(context, mc, "RMB", bx + (ks + gap) * 2, by + (ks + gap) * 2, ks, ks, rmb);
        }
        // Spacebar row
        if (showSpacebar.getValue()) {
            boolean space = mc.options.jumpKey.isPressed();
            drawKey(context, mc, "___", bx, by + (ks + gap) * 3, ks * 3 + gap * 2, ks, space);
        }
    }
}
