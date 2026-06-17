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
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/** Glacier-styled replacement for the vanilla title screen. */
public class GlacierTitleScreen extends Screen {

    private static final int BTN_X = 18;
    private static final int BTN_W = 238;
    private static final int BTN_H = 30;
    private static final int GAP = 3;
    private static final int TOP_SIZE = 36;
    private static final int TOP_GAP = 6;

    private final String[] labels = {"Play Game", "Options", "Marketplace", "Dressing Room", "Changelog"};
    private final String[] topIcons = {"common/achievements", "common/inbox", "common/switcher", "common/exit"};

    public GlacierTitleScreen() { super(Text.literal("Glacier")); }

    private int startY() { return height / 2 - 34; }
    private int btnY(int i) { return startY() + i * (BTN_H + GAP); }
    private int topIconX(int i) { return width - 12 - (topIcons.length - i) * (TOP_SIZE + TOP_GAP); }
    private int topIconY() { return 4; }

    private void runAction(int i) {
        MinecraftClient mc = MinecraftClient.getInstance();
        switch (i) {
            case 0 -> mc.setScreen(new SelectWorldScreen(this));
            case 1 -> mc.setScreen(new OptionsScreen(this, mc.options));
            case 2 -> mc.setScreen(new MultiplayerScreen(this));
            case 3 -> GlacierClient.getInstance().getClickGUI().open();
            case 4 -> GlacierClient.getInstance().getClickGUI().open();
            default -> { }
        }
    }

    private void runTop(int i) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (i == topIcons.length - 1) mc.scheduleStop();
        else GlacierClient.getInstance().getClickGUI().open();
    }

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        if (GuiTextures.has("bg")) {
            GuiTextures.fullscreen(ctx, "bg", width, height);
        } else {
            ctx.fillGradient(0, 0, width, height, 0xFF14181D, 0xFF0A0C0F);
        }
        ctx.fillGradient(0, 0, width, height, 0x4404080D, 0xAA05080B);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        drawBrand(ctx);
        drawButtons(ctx, mouseX, mouseY);
        drawTopButtons(ctx, mouseX, mouseY);
        drawLogin(ctx);
        drawPromo(ctx);
        drawVersion(ctx);
    }

    private void drawBrand(DrawContext ctx) {
        int logoY = startY() - 58;
        Icons.bearLogo(ctx, BTN_X + 24, logoY + 22, 48, GlacierTheme.ACCENT);
        ctx.getMatrices().push();
        ctx.getMatrices().translate(BTN_X + 64, logoY + 4, 0);
        ctx.getMatrices().scale(3.2f, 3.2f, 1f);
        ctx.drawText(textRenderer, inter("Glacier"), 0, 0, GlacierTheme.TEXT, false);
        ctx.getMatrices().pop();
    }

    private void drawButtons(DrawContext ctx, int mouseX, int mouseY) {
        for (int i = 0; i < labels.length; i++) {
            int by = btnY(i);
            boolean hov = within(mouseX, mouseY, BTN_X, by, BTN_W, BTN_H);

            // Full-pill (capsule) button to match the Bedrock layout.
            int rad = BTN_H / 2;
            RenderUtil.drawRoundedRect(ctx, BTN_X, by, BTN_W, BTN_H, rad, hov ? 0xF21A2230 : 0xE00B1220);

            // Small rounded accent bar centred on the left edge.
            RenderUtil.drawRoundedRect(ctx, BTN_X + 7, by + BTN_H / 2 - 8, 3, 16, 1, GlacierTheme.ACCENT_HOVER);

            if (hov) {
                RenderUtil.drawRoundedRect(ctx, BTN_X, by, BTN_W, BTN_H, rad, 0x14FFFFFF);
            }

            Text label = inter(labels[i]);
            int textX = BTN_X + (BTN_W - textRenderer.getWidth(label)) / 2;
            ctx.drawText(textRenderer, label, textX, by + (BTN_H - 9) / 2, GlacierTheme.TEXT, false);
        }
    }

    private void drawTopButtons(DrawContext ctx, int mouseX, int mouseY) {
        for (int i = 0; i < topIcons.length; i++) {
            int ix = topIconX(i), iy = topIconY();
            boolean hov = within(mouseX, mouseY, ix, iy, TOP_SIZE, TOP_SIZE);
            RenderUtil.drawRoundedRect(ctx, ix, iy, TOP_SIZE, TOP_SIZE, 9, hov ? 0xEE111923 : 0xDD080F18);
            if (!Sprites.drawCentered(ctx, topIcons[i], ix + TOP_SIZE / 2, iy + TOP_SIZE / 2, 20)) {
                ctx.drawText(textRenderer, inter("*"), ix + 16, iy + 13, GlacierTheme.TEXT_DIM, false);
            }
        }
    }

    private void drawLogin(DrawContext ctx) {
        String user = "Player";
        try { user = MinecraftClient.getInstance().getSession().getUsername(); } catch (Exception ignored) {}

        Text loginLabel = inter("Logged in as:");
        Text userLabel = inter(user);
        int bw = Math.max(170, 38 + Math.max(textRenderer.getWidth(loginLabel), textRenderer.getWidth(userLabel)) + 10);
        
        // Premium rounded capsule for login widget
        RenderUtil.drawRoundedRect(ctx, 0, 4, bw, 36, 12, 0xDD070E17);
        RenderUtil.drawRoundedOutline(ctx, 0, 4, bw, 36, 12, 1, 0x1AFFFFFF);
        
        if (Sprites.drawCentered(ctx, "common/player", 24, 22, 20)) {
            ctx.drawText(textRenderer, loginLabel, 40, 10, GlacierTheme.TEXT, false);
            ctx.drawText(textRenderer, userLabel, 40, 22, GlacierTheme.ACCENT_HOVER, false);
        } else {
            ctx.drawText(textRenderer, loginLabel, 12, 10, GlacierTheme.TEXT, false);
            ctx.drawText(textRenderer, userLabel, 12, 22, GlacierTheme.ACCENT_HOVER, false);
        }
    }

    private void drawPromo(DrawContext ctx) {
        int x = Math.max(width - 158, 270);
        int y = height - 92;
        int w = Math.min(150, width - x - 8);
        if (w < 120 || y < 52) return;

        Text event = inter("Minecraft Event");
        RenderUtil.drawRoundedRect(ctx, x, y, w, 36, 10, 0xDD070E17);
        ctx.drawText(textRenderer, event, x + (w - textRenderer.getWidth(event)) / 2, y + 13, GlacierTheme.TEXT, false);

        int cardY = y + 40;
        RenderUtil.drawRoundedRect(ctx, x, cardY, w, 62, 8, 0xE0161418);
        
        // FIXED: Replaced sharp outline with a beautiful rounded outline matching the card radius (8)!
        RenderUtil.drawRoundedOutline(ctx, x, cardY, w, 62, 8, 1, GlacierTheme.ACCENT_HOVER);
        
        RenderUtil.drawRoundedRect(ctx, x + 10, cardY + 20, 26, 22, 6, 0xFF5865F2);
        RenderUtil.drawRoundedRect(ctx, x + 16, cardY + 30, 4, 4, 2, 0xFF111923);
        RenderUtil.drawRoundedRect(ctx, x + 27, cardY + 30, 4, 4, 2, 0xFF111923);

        Text join = inter("Join Our Discord!");
        Text url = inter("discord.glacierclient.xyz");
        ctx.drawText(textRenderer, join, x + 42, cardY + 17, GlacierTheme.TEXT, false);
        ctx.drawText(textRenderer, url, x + 42, cardY + 31, GlacierTheme.TEXT_DIM, false);
    }

    private void drawVersion(DrawContext ctx) {
        Text ver = inter("Glacier Client v" + GlacierClient.VERSION);
        int vx = 4;
        int vy = height - 14;
        RenderUtil.drawRoundedRect(ctx, vx - 3, vy - 3, textRenderer.getWidth(ver) + 8, 15, 3, 0xB2070E17);
        ctx.drawText(textRenderer, ver, vx, vy, GlacierTheme.TEXT, false);
        ctx.fill(vx, height - 2, vx + textRenderer.getWidth(ver) + 4, height - 1, GlacierTheme.ACCENT);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        for (int i = 0; i < labels.length; i++) {
            if (within((int) mx, (int) my, BTN_X, btnY(i), BTN_W, BTN_H)) { runAction(i); return true; }
        }
        for (int i = 0; i < topIcons.length; i++) {
            if (within((int) mx, (int) my, topIconX(i), topIconY(), TOP_SIZE, TOP_SIZE)) { runTop(i); return true; }
        }
        return super.mouseClicked(mx, my, button);
    }

    private static final Identifier INTER_FONT = new Identifier("glacierclient", "inter_medium");

    /** Renders text in the Inter Medium font (assets/glacierclient/font/inter_medium.json). */
    private Text inter(String text) {
        return Text.literal(text).setStyle(Style.EMPTY.withFont(INTER_FONT));
    }

    private boolean within(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}
