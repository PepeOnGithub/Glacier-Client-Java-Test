package net.glacierclient.gui.screens;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.theme.GlacierTheme;
import net.glacierclient.core.util.GuiTextures;
import net.glacierclient.core.util.Icons;
import net.glacierclient.core.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;

/** Glacier-styled replacement for the vanilla title screen. */
public class GlacierTitleScreen extends Screen {

    private static final int BTN_X = 30;
    private static final int BTN_W = 230;
    private static final int BTN_H = 24;
    private static final int GAP = 8;

    private final String[] labels = {"Play Game", "Multiplayer", "Options", "Glacier Menu", "Quit"};

    public GlacierTitleScreen() { super(Text.literal("Glacier")); }

    private int startY() { return height / 2 - 28; }
    private int btnY(int i) { return startY() + i * (BTN_H + GAP); }

    private void runAction(int i) {
        MinecraftClient mc = MinecraftClient.getInstance();
        switch (i) {
            case 0 -> mc.setScreen(new SelectWorldScreen(this));
            case 1 -> mc.setScreen(new MultiplayerScreen(this));
            case 2 -> mc.setScreen(new OptionsScreen(this, mc.options));
            case 3 -> GlacierClient.getInstance().getClickGUI().open();
            case 4 -> mc.scheduleStop();
            default -> { }
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // background
        ctx.fillGradient(0, 0, width, height, 0xFF14181D, 0xFF0A0C0F);

        // logo + wordmark
        int logoY = startY() - 70;
        Icons.bear(ctx, BTN_X + 22, logoY + 18, 40, GlacierTheme.ACCENT);
        ctx.getMatrices().push();
        ctx.getMatrices().translate(BTN_X + 52, logoY, 0);
        ctx.getMatrices().scale(2.6f, 2.6f, 1f);
        ctx.drawText(textRenderer, "Glacier", 0, 0, GlacierTheme.TEXT, true);
        ctx.getMatrices().pop();

        // buttons
        for (int i = 0; i < labels.length; i++) {
            int by = btnY(i);
            boolean hov = within(mouseX, mouseY, BTN_X, by, BTN_W, BTN_H);
            int accentBar = i == labels.length - 1 ? GlacierTheme.RED : GlacierTheme.ACCENT;
            if (GuiTextures.has("start_buttons_left_side")) {
                GuiTextures.nineSlice(ctx, "start_buttons_left_side", BTN_X, by, BTN_W, BTN_H);
                if (hov) RenderUtil.drawRoundedRect(ctx, BTN_X, by, BTN_W, BTN_H, GlacierTheme.RADIUS_SM, 0x18FFFFFF);
            } else {
                RenderUtil.drawRoundedRect(ctx, BTN_X, by, BTN_W, BTN_H, GlacierTheme.RADIUS_SM,
                        hov ? GlacierTheme.BG_ITEM_HOVER : 0xFF1B1F24);
                RenderUtil.drawRoundedRect(ctx, BTN_X, by + 4, 4, BTN_H - 8, 2, accentBar);
            }
            int tw = textRenderer.getWidth(labels[i]);
            ctx.drawText(textRenderer, labels[i], BTN_X + (BTN_W - tw) / 2, by + (BTN_H - 8) / 2, GlacierTheme.TEXT, false);
        }

        // login banner (top-left)
        String user = "Player";
        try { user = MinecraftClient.getInstance().getSession().getUsername(); } catch (Exception ignored) {}
        RenderUtil.drawRoundedRect(ctx, 8, 8, 16 + textRenderer.getWidth(user) + 12, 30, GlacierTheme.RADIUS_SM, 0xCC15181C);
        ctx.drawText(textRenderer, "Logged in as:", 14, 13, GlacierTheme.TEXT_DIM, false);
        ctx.drawText(textRenderer, user, 14, 24, GlacierTheme.ACCENT, false);

        // version (bottom-right)
        String ver = "Glacier v" + GlacierClient.VERSION;
        ctx.drawText(textRenderer, ver, width - textRenderer.getWidth(ver) - 8, height - 12, GlacierTheme.TEXT_DIM, false);

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        for (int i = 0; i < labels.length; i++) {
            if (within((int) mx, (int) my, BTN_X, btnY(i), BTN_W, BTN_H)) { runAction(i); return true; }
        }
        return super.mouseClicked(mx, my, button);
    }

    private boolean within(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}
