package net.glacierclient.gui.screens;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.theme.GlacierTheme;
import net.glacierclient.core.util.GuiTextures;
import net.glacierclient.core.util.Icons;
import net.glacierclient.core.util.RenderUtil;
import net.glacierclient.core.util.Sprites;
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
    private static final int BTN_H = 26;
    private static final int GAP = 8;

    private final String[] labels = {"Play Game", "Options", "Marketplace", "Dressing Room", "Changelog"};
    private final String[] icons = {"common/player", "common/settings", "common/store", "common/skins", "common/info"};

    // top-right icon buttons
    private final String[] topIcons = {"common/achievements", "common/inbox", "common/switcher", "common/exit"};

    public GlacierTitleScreen() { super(Text.literal("Glacier")); }

    private int startY() { return height / 2 - 40; }
    private int btnY(int i) { return startY() + i * (BTN_H + GAP); }
    private int topX(int i) { return width - 32 - i * 30; }   // i=0 rightmost? we lay out from right
    private int topIconX(int i) { return width - 28 - (topIcons.length - 1 - i) * 30; }
    private int topIconY() { return 16; }

    private void runAction(int i) {
        MinecraftClient mc = MinecraftClient.getInstance();
        switch (i) {
            case 0 -> mc.setScreen(new SelectWorldScreen(this));
            case 1 -> mc.setScreen(new OptionsScreen(this, mc.options));
            case 2 -> mc.setScreen(new MultiplayerScreen(this));      // Marketplace placeholder -> servers
            case 3 -> GlacierClient.getInstance().getClickGUI().open(); // Dressing Room -> Glacier menu (cosmetics)
            case 4 -> GlacierClient.getInstance().getClickGUI().open(); // Changelog -> Glacier menu
            default -> { }
        }
    }

    private void runTop(int i) {
        MinecraftClient mc = MinecraftClient.getInstance();
        // achievements/inbox/switcher are decorative here; exit quits
        if (i == topIcons.length - 1) mc.scheduleStop();
        else GlacierClient.getInstance().getClickGUI().open();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // background layer
        ctx.fillGradient(0, 0, width, height, 0xFF14181D, 0xFF0A0C0F);

        // logo + wordmark layer
        int logoY = startY() - 64;
        Icons.bear(ctx, BTN_X + 22, logoY + 18, 40, GlacierTheme.ACCENT);
        ctx.getMatrices().push();
        ctx.getMatrices().translate(BTN_X + 52, logoY, 0);
        ctx.getMatrices().scale(2.6f, 2.6f, 1f);
        ctx.drawText(textRenderer, "Glacier", 0, 0, GlacierTheme.TEXT, true);
        ctx.getMatrices().pop();

        // button layer (bg texture -> hover overlay -> icon -> label, drawn in that order)
        for (int i = 0; i < labels.length; i++) {
            int by = btnY(i);
            boolean hov = within(mouseX, mouseY, BTN_X, by, BTN_W, BTN_H);
            if (GuiTextures.has("start_buttons_left_side")) {
                GuiTextures.nineSlice(ctx, "start_buttons_left_side", BTN_X, by, BTN_W, BTN_H);
            } else {
                RenderUtil.drawRoundedRect(ctx, BTN_X, by, BTN_W, BTN_H, GlacierTheme.RADIUS_SM, 0xFF1B1F24);
                RenderUtil.drawRoundedRect(ctx, BTN_X, by + 4, 4, BTN_H - 8, 2, GlacierTheme.ACCENT);
            }
            if (hov) RenderUtil.drawRoundedRect(ctx, BTN_X, by, BTN_W, BTN_H, GlacierTheme.RADIUS_SM, 0x18FFFFFF);

            int iconSize = 14;
            int textX = BTN_X + 14;
            if (Sprites.drawCentered(ctx, icons[i], BTN_X + 18, by + BTN_H / 2, iconSize)) textX = BTN_X + 32;
            ctx.drawText(textRenderer, labels[i], textX, by + (BTN_H - 8) / 2, GlacierTheme.TEXT, false);
        }

        // top-right icon buttons
        for (int i = 0; i < topIcons.length; i++) {
            int ix = topIconX(i), iy = topIconY();
            boolean hov = within(mouseX, mouseY, ix - 12, iy - 4, 24, 24);
            if (hov) RenderUtil.drawRoundedRect(ctx, ix - 12, iy - 4, 24, 24, 6, 0x22FFFFFF);
            if (!Sprites.drawCentered(ctx, topIcons[i], ix, iy + 8, 16)) {
                ctx.drawText(textRenderer, "•", ix - 2, iy + 4, GlacierTheme.TEXT_DIM, false);
            }
        }

        // login banner (top-left)
        String user = "Player";
        try { user = MinecraftClient.getInstance().getSession().getUsername(); } catch (Exception ignored) {}
        int bw = 12 + textRenderer.getWidth("Logged in as:") + 12;
        RenderUtil.drawRoundedRect(ctx, 8, 8, Math.max(bw, 30 + textRenderer.getWidth(user)), 30, GlacierTheme.RADIUS_SM, 0xCC15181C);
        if (Sprites.drawCentered(ctx, "common/player", 22, 23, 14)) {
            ctx.drawText(textRenderer, "Logged in as:", 34, 13, GlacierTheme.TEXT_DIM, false);
            ctx.drawText(textRenderer, user, 34, 24, GlacierTheme.ACCENT, false);
        } else {
            ctx.drawText(textRenderer, "Logged in as:", 14, 13, GlacierTheme.TEXT_DIM, false);
            ctx.drawText(textRenderer, user, 14, 24, GlacierTheme.ACCENT, false);
        }

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
        for (int i = 0; i < topIcons.length; i++) {
            if (within((int) mx, (int) my, topIconX(i) - 12, topIconY() - 4, 24, 24)) { runTop(i); return true; }
        }
        return super.mouseClicked(mx, my, button);
    }

    private boolean within(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}
