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
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/** Glacier-styled replacement for the in-game pause (GameMenu) screen. */
public class GlacierPauseScreen extends Screen {

    private static final int PANEL_W = 250;
    private static final int PANEL_H = 160;
    private static final int ICON_S = 32;

    public GlacierPauseScreen() { super(Text.literal("Glacier")); }

    private int px() { return (width - PANEL_W) / 2; }
    private int py() { return (height - PANEL_H) / 2; }

    private int resumeY() { return py() + 48; }
    private int iconRowY() { return py() + 82; }
    private int quitY() { return py() + PANEL_H - 30; }
    private int iconX(int i) { return px() + 25 + i * 42; }

    private static final String[] ROW_ICONS = {
        "common/achievements",
        "common/store",
        "common/settings",
        "common/skins",
        "common/player"
    };

    private static final String[] HEADER_ICONS = {
        "common/info",
        "common/inbox",
        "common/screenshot"
    };

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) { }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int x = px(), y = py();

        // 1. Draw side / corner widgets
        drawSideWidgets(ctx);
        drawBottomLeftButtons(ctx, mouseX, mouseY);
        drawRightSlideButton(ctx, mouseX, mouseY);

        // 2. Draw central panel
        RenderUtil.drawShadow(ctx, x, y, PANEL_W, PANEL_H, 6, 0x50000000);
        if (GuiTextures.has("base_bg")) {
            GuiTextures.nineSlice(ctx, "base_bg", x, y, PANEL_W, PANEL_H);
        } else {
            RenderUtil.drawRoundedRect(ctx, x, y, PANEL_W, PANEL_H, GlacierTheme.RADIUS_MD, 0xDD070E17);
            RenderUtil.drawRoundedOutline(ctx, x, y, PANEL_W, PANEL_H, GlacierTheme.RADIUS_MD, 1, GlacierTheme.ACCENT_GLOW);
        }
        // Black edge vignette to match the mod menu.
        RenderUtil.drawEdgeVignette(ctx, x, y, PANEL_W, PANEL_H, 14, 0x66000000);

        // Header Bear Icon & Title
        Icons.bearLogo(ctx, x + 16, y + 16, 20, GlacierTheme.ACCENT);
        ctx.getMatrices().push();
        ctx.getMatrices().translate(x + 40, y + 12, 0);
        ctx.getMatrices().scale(1.3f, 1.3f, 1f);
        ctx.drawText(textRenderer, inter("Glacier"), 0, 0, GlacierTheme.TEXT, false);
        ctx.getMatrices().pop();

        // Header Buttons (Info, Inbox, Camera)
        drawHeaderButtons(ctx, mouseX, mouseY);

        // Resume Button
        button(ctx, x + 16, resumeY(), PANEL_W - 32, 22, "Resume", mouseX, mouseY, false);

        // 5-Icon Row
        for (int i = 0; i < ROW_ICONS.length; i++) {
            iconButton(ctx, iconX(i), iconRowY(), mouseX, mouseY, i);
        }

        // Save & Quit Button
        button(ctx, x + 16, quitY(), PANEL_W - 32, 22, "Save & Quit", mouseX, mouseY, true);
    }

    private void drawSideWidgets(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();

        // Capsule 1: Logged in as
        String user = "Player";
        try { user = mc.getSession().getUsername(); } catch (Exception ignored) {}
        drawWidget(ctx, 8, 8, "common/player", "Logged in as:", user);

        // Capsule 2: Playing in
        String serverOrWorld = "My World";
        if (mc.isIntegratedServerRunning() && mc.getServer() != null) {
            serverOrWorld = mc.getServer().getSaveProperties().getLevelName();
        } else if (mc.getCurrentServerEntry() != null) {
            serverOrWorld = mc.getCurrentServerEntry().name;
        }
        drawWidget(ctx, 8, 44, "common/playing", "Playing in:", serverOrWorld);

        // Capsule 3: Game Status
        drawWidget(ctx, 8, 80, "common/paused", "Game Status:", "Game is paused");
    }

    private void drawWidget(DrawContext ctx, int x, int y, String icon, String title, String value) {
        int w = 135;
        int h = 30;
        RenderUtil.drawRoundedRect(ctx, x, y, w, h, 10, 0xB2070E17);
        RenderUtil.drawRoundedOutline(ctx, x, y, w, h, 10, 1, 0x1AFFFFFF);

        if (icon.equals("common/playing")) {
            Icons.bearLogo(ctx, x + 7, y + 7, 16, GlacierTheme.ACCENT);
        } else {
            Sprites.drawCentered(ctx, icon, x + 15, y + 15, 16);
        }

        ctx.drawText(textRenderer, inter(title), x + 32, y + 4, GlacierTheme.TEXT_DIM, false);
        ctx.drawText(textRenderer, inter(value), x + 32, y + 15, GlacierTheme.ACCENT_HOVER, false);
    }

    private void drawBottomLeftButtons(DrawContext ctx, int mouseX, int mouseY) {
        int y = height - 36;

        // Mod Menu button (circular)
        boolean hov1 = within(mouseX, mouseY, 8, y, 28, 28);
        RenderUtil.drawRoundedRect(ctx, 8, y, 28, 28, 14, hov1 ? 0xEE1B2433 : 0xCC0B1220);
        RenderUtil.drawRoundedOutline(ctx, 8, y, 28, 28, 14, 1, 0x1AFFFFFF);
        Sprites.drawCentered(ctx, "common/mod_menu", 22, y + 14, 16);

        // HUD Editor button (pencil, circular)
        boolean hov2 = within(mouseX, mouseY, 42, y, 28, 28);
        RenderUtil.drawRoundedRect(ctx, 42, y, 28, 28, 14, hov2 ? 0xEE1B2433 : 0xCC0B1220);
        RenderUtil.drawRoundedOutline(ctx, 42, y, 28, 28, 14, 1, 0x1AFFFFFF);
        Sprites.drawCentered(ctx, "common/edit", 56, y + 14, 16);
    }

    private void drawRightSlideButton(DrawContext ctx, int mouseX, int mouseY) {
        int x = width - 16;
        int y = (height - 36) / 2;
        boolean hov = within(mouseX, mouseY, x, y, 16, 36);

        RenderUtil.drawRoundedRect(ctx, x - 8, y, 24, 36, 14, hov ? 0xEE1B2433 : 0xCC0B1220);
        RenderUtil.drawRoundedOutline(ctx, x - 8, y, 24, 36, 14, 1, 0x1AFFFFFF);
        Sprites.drawCentered(ctx, "common/arrow/arrow_left", x + 2, y + 18, 12);
    }

    private void drawHeaderButtons(DrawContext ctx, int mouseX, int mouseY) {
        int xStart = px() + PANEL_W - 74;
        int y = py() + 14;

        for (int i = 0; i < HEADER_ICONS.length; i++) {
            int bx = xStart + i * 22;
            boolean hov = within(mouseX, mouseY, bx, y, 18, 18);
            // Fully circular buttons (radius = size/2) to match the Bedrock layout.
            RenderUtil.drawRoundedRect(ctx, bx, y, 18, 18, 9, hov ? 0xEE1B2433 : 0xCC0B1220);
            Sprites.drawCentered(ctx, HEADER_ICONS[i], bx + 9, y + 9, 11);
        }
    }

    private void button(DrawContext ctx, int x, int y, int w, int h, String label, int mouseX, int mouseY, boolean danger) {
        boolean hov = within(mouseX, mouseY, x, y, w, h);
        // Clean full-pill button to match the Bedrock layout (no underline bar).
        int rad = h / 2;
        RenderUtil.drawRoundedRect(ctx, x, y, w, h, rad, hov ? 0xF21F2632 : 0xE013171D);
        Text text = inter(label);
        int tw = textRenderer.getWidth(text);
        ctx.drawText(textRenderer, text, x + (w - tw) / 2, y + (h - 8) / 2, danger ? GlacierTheme.RED : GlacierTheme.TEXT, false);
    }

    private void iconButton(DrawContext ctx, int x, int y, int mouseX, int mouseY, int which) {
        int s = ICON_S;
        boolean hov = within(mouseX, mouseY, x, y, s, s);
        // Clean rounded-square tiles (no texture) for a consistent Bedrock-style look.
        RenderUtil.drawRoundedRect(ctx, x, y, s, s, 10, hov ? 0xEE1B2433 : 0xCC0B1220);
        Sprites.drawCentered(ctx, ROW_ICONS[which], x + s / 2, y + s / 2, 16);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int x = px();

        // Resume Button click
        if (within((int) mx, (int) my, x + 16, resumeY(), PANEL_W - 32, 22)) {
            mc.setScreen(null);
            return true;
        }

        // Save & Quit Button click
        if (within((int) mx, (int) my, x + 16, quitY(), PANEL_W - 32, 22)) {
            saveAndQuit(mc);
            return true;
        }

        // Header Buttons click: info, inbox, screenshot
        int headerXStart = px() + PANEL_W - 74;
        int headerY = py() + 14;
        for (int i = 0; i < HEADER_ICONS.length; i++) {
            if (within((int) mx, (int) my, headerXStart + i * 22, headerY, 18, 18)) {
                if (i == 2) takeScreenshot(mc);              // camera → real screenshot
                else GlacierClient.getInstance().getClickGUI().open(); // info / inbox → mod menu
                return true;
            }
        }

        // 5-Icon Row click: achievements, store, settings, dressing, profile
        for (int i = 0; i < ROW_ICONS.length; i++) {
            if (within((int) mx, (int) my, iconX(i), iconRowY(), ICON_S, ICON_S)) {
                switch (i) {
                    case 0 -> { // achievements → vanilla Advancements screen
                        if (mc.player != null)
                            mc.setScreen(new net.minecraft.client.gui.screen.advancement.AdvancementsScreen(
                                mc.player.networkHandler.getAdvancementHandler()));
                    }
                    case 2 -> mc.setScreen(new OptionsScreen(this, mc.options)); // settings
                    default -> GlacierClient.getInstance().getClickGUI().open(); // store, dressing, profile
                }
                return true;
            }
        }

        // Bottom-Left Buttons click
        int blY = height - 36;
        if (within((int) mx, (int) my, 8, blY, 28, 28)) {
            GlacierClient.getInstance().getClickGUI().open();
            return true;
        }
        if (within((int) mx, (int) my, 42, blY, 28, 28)) {
            mc.setScreen(new HUDEditorScreen());
            return true;
        }

        // Right Slide Button click - Opens Social Interactions (Player List) Screen!
        int rsY = (height - 36) / 2;
        if (within((int) mx, (int) my, width - 16, rsY, 16, 36)) {
            mc.setScreen(new net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen());
            return true;
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

    /** Takes a real screenshot and prints the saved-file message to chat (like vanilla F2). */
    private void takeScreenshot(MinecraftClient mc) {
        net.minecraft.client.util.ScreenshotRecorder.saveScreenshot(
                mc.runDirectory, mc.getFramebuffer(),
                text -> mc.execute(() -> mc.inGameHud.getChatHud().addMessage(text)));
    }

    private static final Identifier INTER_FONT = new Identifier("glacierclient", "inter_medium");

    /** Renders text in the Inter Medium font (assets/glacierclient/font/inter_medium.json). */
    private Text inter(String text) {
        return Text.literal(text).setStyle(Style.EMPTY.withFont(INTER_FONT));
    }

    @Override
    public boolean shouldPause() { return true; }
}
