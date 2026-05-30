package net.glacierclient.gui.screens;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.theme.GlacierTheme;
import net.glacierclient.core.util.Icons;
import net.glacierclient.core.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.text.Text;

/** Glacier-styled replacement for the in-game pause (GameMenu) screen. */
public class GlacierPauseScreen extends Screen {

    private static final int PANEL_W = 240;
    private static final int PANEL_H = 150;

    public GlacierPauseScreen() { super(Text.literal("Glacier")); }

    private int px() { return (width - PANEL_W) / 2; }
    private int py() { return (height - PANEL_H) / 2; }

    private int resumeY() { return py() + 44; }
    private int iconRowY() { return py() + 80; }
    private int quitY() { return py() + PANEL_H - 32; }
    private int iconX(int i) { return px() + 20 + i * 44; }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0x88000000);
        int x = px(), y = py();
        RenderUtil.drawShadow(ctx, x, y, PANEL_W, PANEL_H, 6, 0x50000000);
        RenderUtil.drawRoundedRect(ctx, x, y, PANEL_W, PANEL_H, GlacierTheme.RADIUS_MD, GlacierTheme.BG_PANEL);
        RenderUtil.drawOutline(ctx, x, y, PANEL_W, PANEL_H, 1, GlacierTheme.ACCENT_GLOW);

        // header
        Icons.bear(ctx, x + 22, y + 18, 22, GlacierTheme.ACCENT);
        ctx.drawText(textRenderer, "Glacier", x + 40, y + 13, GlacierTheme.TEXT, true);

        // Resume
        button(ctx, x + 16, resumeY(), PANEL_W - 32, 22, "Resume", mouseX, mouseY, false);

        // icon row: Glacier menu, Options
        iconButton(ctx, iconX(0), iconRowY(), mouseX, mouseY, 0);
        iconButton(ctx, iconX(1), iconRowY(), mouseX, mouseY, 1);

        // Save & Quit
        button(ctx, x + 16, quitY(), PANEL_W - 32, 22, "Save & Quit", mouseX, mouseY, true);

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void button(DrawContext ctx, int x, int y, int w, int h, String label, int mouseX, int mouseY, boolean danger) {
        boolean hov = within(mouseX, mouseY, x, y, w, h);
        RenderUtil.drawRoundedRect(ctx, x, y, w, h, GlacierTheme.RADIUS_SM,
                hov ? GlacierTheme.BG_ITEM_HOVER : 0xFF1B1F24);
        int tw = textRenderer.getWidth(label);
        ctx.drawText(textRenderer, label, x + (w - tw) / 2, y + (h - 8) / 2,
                danger ? GlacierTheme.RED : GlacierTheme.TEXT, false);
    }

    private void iconButton(DrawContext ctx, int x, int y, int mouseX, int mouseY, int which) {
        int s = 32;
        boolean hov = within(mouseX, mouseY, x, y, s, s);
        RenderUtil.drawRoundedRect(ctx, x, y, s, s, GlacierTheme.RADIUS_SM, hov ? GlacierTheme.BG_ITEM_HOVER : 0xFF1B1F24);
        int c = hov ? GlacierTheme.ACCENT : GlacierTheme.TEXT;
        if (which == 0) Icons.gear(ctx, x + s / 2, y + s / 2, 8, c);
        else Icons.draw(ctx, textRenderer, "render", "RENDER", x + s / 2, y + s / 2, 16, c);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int x = px();
        if (within((int) mx, (int) my, x + 16, resumeY(), PANEL_W - 32, 22)) { mc.setScreen(null); return true; }
        if (within((int) mx, (int) my, x + 16, quitY(), PANEL_W - 32, 22)) { saveAndQuit(mc); return true; }
        if (within((int) mx, (int) my, iconX(0), iconRowY(), 32, 32)) {
            GlacierClient.getInstance().getClickGUI().open(); return true;
        }
        if (within((int) mx, (int) my, iconX(1), iconRowY(), 32, 32)) {
            mc.setScreen(new OptionsScreen(this, mc.options)); return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    private void saveAndQuit(MinecraftClient mc) {
        if (mc.world != null) mc.world.disconnect();
        mc.disconnect();
        mc.setScreen(new TitleScreen());
    }

    private boolean within(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public boolean shouldPause() { return true; }
}
