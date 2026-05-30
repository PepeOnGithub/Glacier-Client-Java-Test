package net.glacierclient.gui.screens;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.cosmetic.Cosmetic;
import net.glacierclient.core.cosmetic.CosmeticCategory;
import net.glacierclient.core.customization.CardStyle;
import net.glacierclient.core.customization.GuiCustomization;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.ModuleManager;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.theme.GlacierTheme;
import net.glacierclient.core.util.AnimationUtil;
import net.glacierclient.core.util.GuiTextures;
import net.glacierclient.core.util.Icons;
import net.glacierclient.core.util.IconTextures;
import net.glacierclient.core.util.RenderUtil;
import net.glacierclient.gui.widget.ColorPicker;
import net.glacierclient.modules.render.BossbarCustomizer;
import net.glacierclient.modules.render.CustomCrosshair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Glacier ClickGUI — card-grid layout with top tabs (Modules / Elements / Editors / Music Player),
 * draggable module cards, a floating per-module settings popup, and dedicated editors.
 */
public class ClickGUIScreen extends Screen {

    private enum Tab { MODULES, ELEMENTS, EDITORS, MUSIC }

    // Layout
    private static final int PAD       = 12;
    private static final int HEADER_H  = 38;
    private static final int TAB_H     = 34;
    private static final int COLS      = 3;
    private static final int CARD_H    = 74;
    private static final int CARD_GAP  = 10;
    private static final int DRAG_THRESHOLD = 4;

    private int panelX, panelY, panelW, panelH;
    private Tab activeTab = Tab.MODULES;

    // search
    private String searchQuery = "";
    private boolean searchFocused = false;

    // grid scroll (pixels) per tab
    private int gridScroll = 0;

    // drag-to-reorder (Modules tab)
    private GlacierMod pressedCard;
    private int pressX, pressY;
    private GlacierMod draggingCard;
    private int dragMouseX, dragMouseY;

    // editors
    private String activeEditor; // null | "Crosshair" | "Bossbar" | "Cosmetics" | "Emotes"

    // unified settings popup (works for modules and cosmetics)
    private enum PopupTab { SETTINGS, APPEARANCE }
    private boolean popupOpen = false;
    private String popupTitle = "", popupDesc = "";
    private String popupKey = null;            // module name → CardStyle key
    private boolean popupAppearance = false;    // appearance sub-tab available?
    private PopupTab popupTab = PopupTab.SETTINGS;
    private List<Setting<?>> popupSettings = new ArrayList<>();
    private Runnable popupToggle;
    private BooleanSupplier popupEnabled;
    private int popupScroll = 0;
    private GlacierMod popupBindTarget;   // module whose keybind can be set (null for cosmetics)
    private boolean awaitingBind = false; // capturing the next key press as a bind

    private final ColorPicker colorPicker = new ColorPicker();

    // setting interaction state (shared by popup + editors)
    private NumberSetting draggingSlider;
    private int sliderBarX, sliderBarW;
    private ModeSetting openDropdown;
    private int dropdownX, dropdownY;

    // music player (visual placeholder state)
    private boolean musicPlaying = false;

    private static final String[] EMOTES = {"Dab", "Wave", "Spin", "Flex", "Bow"};

    public ClickGUIScreen() {
        super(Text.literal("Glacier Client"));
    }

    @Override
    protected void init() {
        panelW = Math.min(width - 80, 480);
        panelH = Math.min(height - 80, 300);
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
        POPUP_W = Math.min(panelW - 16, 270);
        POPUP_H = Math.min(panelH - 16, 250);
    }

    private ModuleManager modules() { return GlacierClient.getInstance().getModuleManager(); }

    // =======================================================================
    // RENDER
    // =======================================================================

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0xB0000000);

        RenderUtil.drawShadow(ctx, panelX, panelY, panelW, panelH, 6, 0x50000000);
        if (GuiTextures.has("base_bg")) {
            GuiTextures.nineSlice(ctx, "base_bg", panelX, panelY, panelW, panelH);
        } else {
            RenderUtil.drawRoundedRect(ctx, panelX, panelY, panelW, panelH, GlacierTheme.RADIUS_MD, GlacierTheme.BG);
            RenderUtil.drawOutline(ctx, panelX, panelY, panelW, panelH, 1, GlacierTheme.ACCENT_GLOW);
        }

        renderHeader(ctx, mouseX, mouseY);
        renderTabs(ctx, mouseX, mouseY);

        switch (activeTab) {
            case MODULES  -> renderModulesTab(ctx, mouseX, mouseY);
            case ELEMENTS -> renderElementsTab(ctx, mouseX, mouseY);
            case EDITORS  -> renderEditorsTab(ctx, mouseX, mouseY);
            case MUSIC    -> renderMusicTab(ctx, mouseX, mouseY);
        }

        // dragged card floats on top
        if (draggingCard != null) {
            int cw = cardWidth();
            renderCard(ctx, draggingCard, dragMouseX - cw / 2, dragMouseY - CARD_H / 2, cw, mouseX, mouseY, true);
        }

        if (popupOpen) renderPopup(ctx, mouseX, mouseY);
        if (openDropdown != null) renderDropdown(ctx, openDropdown, dropdownX, dropdownY, mouseX, mouseY);
        if (colorPicker.isOpen()) colorPicker.render(ctx, textRenderer, mouseX, mouseY);

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void renderHeader(DrawContext ctx, int mouseX, int mouseY) {
        ctx.fill(panelX, panelY, panelX + panelW, panelY + HEADER_H, GlacierTheme.BG_PANEL);

        // bear logo
        Icons.bear(ctx, panelX + 26, panelY + HEADER_H / 2, 22, GlacierTheme.ACCENT);

        // centered title
        String title = "Glacier";
        ctx.drawTextWithShadow(textRenderer, Text.literal(title),
                panelX + (panelW - textRenderer.getWidth(title)) / 2, panelY + HEADER_H / 2 - 4, GlacierTheme.TEXT);

        // close X
        int xc = panelX + panelW - 24, yc = panelY + HEADER_H / 2;
        boolean xHov = within(mouseX, mouseY, xc - 10, yc - 10, 20, 20);
        drawX(ctx, xc, yc, 6, xHov ? GlacierTheme.RED : GlacierTheme.TEXT_DIM);
    }

    private void renderTabs(DrawContext ctx, int mouseX, int mouseY) {
        int y = panelY + HEADER_H + 6;
        int x = panelX + PAD;
        Tab[] tabs = Tab.values();
        String[] labels = {"Modules", "Elements", "Editors", "Music Player"};
        for (int i = 0; i < tabs.length; i++) {
            int w = textRenderer.getWidth(labels[i]) + 30;
            boolean sel = activeTab == tabs[i];
            boolean hov = within(mouseX, mouseY, x, y, w, TAB_H - 14);
            int bg = sel ? GlacierTheme.ACCENT : (hov ? GlacierTheme.BG_ITEM_HOVER : GlacierTheme.BG_PANEL);
            String tabTex = sel ? "accent_bg" : "underlined_base_bg";
            if (GuiTextures.has(tabTex)) GuiTextures.nineSlice(ctx, tabTex, x, y, w, TAB_H - 14);
            else RenderUtil.drawRoundedRect(ctx, x, y, w, TAB_H - 14, GlacierTheme.RADIUS_SM, bg);
            int fg = sel ? GlacierTheme.TEXT : GlacierTheme.TEXT_DIM;
            int icx = x + 12, icy = y + (TAB_H - 14) / 2;
            tabIcon(ctx, tabs[i], icx, icy, fg);
            ctx.drawTextWithShadow(textRenderer, labels[i], x + 22, y + (TAB_H - 14) / 2 - 4, fg);
            x += w + 6;
        }
    }

    private void tabIcon(DrawContext ctx, Tab t, int cx, int cy, int color) {
        switch (t) {
            case MODULES  -> Icons.draw(ctx, textRenderer, "chunk map", "HUD", cx, cy, 12, color);
            case ELEMENTS -> Icons.draw(ctx, textRenderer, "fps graph", "HUD", cx, cy, 12, color);
            case EDITORS  -> Icons.gear(ctx, cx, cy, 5, color);
            case MUSIC    -> Icons.draw(ctx, textRenderer, "music note", "COSMETICS", cx, cy, 12, color);
        }
    }

    // ---- content area helpers ----
    private int contentX() { return panelX + PAD; }
    private int contentY() { return panelY + HEADER_H + TAB_H; }
    private int contentW() { return panelW - PAD * 2; }
    private int contentH() { return panelY + panelH - contentY() - PAD; }
    private int cardWidth() { return (contentW() - (COLS - 1) * CARD_GAP) / COLS; }

    // =======================================================================
    // MODULES TAB (card grid, draggable)
    // =======================================================================

    private void renderModulesTab(DrawContext ctx, int mouseX, int mouseY) {
        renderCardGrid(ctx, getFilteredModules(), mouseX, mouseY, true);
    }

    private void renderElementsTab(DrawContext ctx, int mouseX, int mouseY) {
        // "Open HUD Editor" button at top of content
        int bx = contentX(), by = contentY(), bw = 150, bh = 24;
        boolean hov = within(mouseX, mouseY, bx, by, bw, bh);
        RenderUtil.drawRoundedRect(ctx, bx, by, bw, bh, GlacierTheme.RADIUS_SM, hov ? GlacierTheme.ACCENT : GlacierTheme.ACCENT_BG);
        ctx.drawTextWithShadow(textRenderer, "Open HUD Editor", bx + 12, by + 8, hov ? GlacierTheme.TEXT : GlacierTheme.ACCENT);

        List<GlacierMod> hud = modules().getModulesByCategory(Category.HUD);
        renderCardGridAt(ctx, hud, contentX(), by + bh + 10, mouseX, mouseY, false);
    }

    private void renderCardGrid(DrawContext ctx, List<GlacierMod> mods, int mouseX, int mouseY, boolean draggable) {
        renderCardGridAt(ctx, mods, contentX(), contentY(), mouseX, mouseY, draggable);
    }

    private void renderCardGridAt(DrawContext ctx, List<GlacierMod> mods, int gx, int gy, int mouseX, int mouseY, boolean draggable) {
        int cw = cardWidth();
        int areaBottom = panelY + panelH - PAD;
        // clip via scissor
        ctx.enableScissor(panelX, gy, panelX + panelW, areaBottom);
        int rows = (mods.size() + COLS - 1) / COLS;
        int totalH = rows * (CARD_H + CARD_GAP);
        int visibleH = areaBottom - gy;
        int maxScroll = Math.max(0, totalH - visibleH);
        gridScroll = Math.max(0, Math.min(gridScroll, maxScroll));

        for (int i = 0; i < mods.size(); i++) {
            GlacierMod mod = mods.get(i);
            if (mod == draggingCard) continue;
            int col = i % COLS, row = i / COLS;
            int cx = gx + col * (cw + CARD_GAP);
            int cy = gy + row * (CARD_H + CARD_GAP) - gridScroll;
            if (cy + CARD_H < gy || cy > areaBottom) continue;
            renderCard(ctx, mod, cx, cy, cw, mouseX, mouseY, false);
        }
        ctx.disableScissor();

        // scrollbar
        if (maxScroll > 0) {
            int trackX = panelX + panelW - 6;
            int thumbH = Math.max(24, visibleH * visibleH / totalH);
            int thumbY = gy + (int) ((float) gridScroll / maxScroll * (visibleH - thumbH));
            ctx.fill(trackX, gy, trackX + 3, areaBottom, GlacierTheme.BG_ITEM);
            ctx.fill(trackX, thumbY, trackX + 3, thumbY + thumbH, GlacierTheme.ACCENT_GLOW);
        }
    }

    private void renderCard(DrawContext ctx, GlacierMod mod, int x, int y, int w, int mouseX, int mouseY, boolean floating) {
        boolean hov = !floating && within(mouseX, mouseY, x, y, w, CARD_H);
        CardStyle st = GuiCustomization.get().styleFor(mod.getName());
        drawStyledCard(ctx, st, x, y, w, CARD_H, hov, mod.isEnabled(), floating);

        int iconColor = mod.isEnabled() ? st.accentColor : GlacierTheme.TEXT;
        if (!IconTextures.draw(ctx, mod.getName(), x + w / 2, y + 36, 30)) {
            Icons.draw(ctx, textRenderer, mod.getName(), mod.getCategory().name(), x + w / 2, y + 36, 30, iconColor);
        }

        // name (centered, trimmed)
        String name = trim(mod.getName(), w - 12);
        ctx.drawTextWithShadow(textRenderer, name, x + (w - textRenderer.getWidth(name)) / 2, y + CARD_H - 18,
                mod.isEnabled() ? GlacierTheme.TEXT : GlacierTheme.TEXT_DIM);

        // gear
        if (!mod.getSettings().isEmpty()) {
            int gx = x + w - 18, gy = y + 16;
            boolean gHov = !floating && within(mouseX, mouseY, gx - 9, gy - 9, 18, 18);
            Icons.gear(ctx, gx, gy, 6, gHov ? GlacierTheme.ACCENT : GlacierTheme.TEXT_DIM);
        }
    }

    // =======================================================================
    // EDITORS TAB
    // =======================================================================

    private void renderEditorsTab(DrawContext ctx, int mouseX, int mouseY) {
        if (activeEditor == null) {
            String[] names = {"Crosshair", "Bossbar", "Cosmetics", "Emotes"};
            int cw = cardWidth();
            for (int i = 0; i < names.length; i++) {
                int col = i % COLS, row = i / COLS;
                int x = contentX() + col * (cw + CARD_GAP);
                int y = contentY() + row * (CARD_H + CARD_GAP);
                boolean hov = within(mouseX, mouseY, x, y, cw, CARD_H);
                RenderUtil.drawRoundedRect(ctx, x, y, cw, CARD_H, GlacierTheme.RADIUS_SM, hov ? 0xFF2A2E33 : 0xFF202327);
                int col2 = hov ? GlacierTheme.ACCENT : GlacierTheme.TEXT;
                editorIcon(ctx, names[i], x + cw / 2, y + 36, col2);
                ctx.drawTextWithShadow(textRenderer, names[i], x + (cw - textRenderer.getWidth(names[i])) / 2, y + CARD_H - 18, col2);
            }
            String hint = "Open an editor to customize visuals & cosmetics";
            ctx.drawTextWithShadow(textRenderer, hint, contentX(), panelY + panelH - PAD - 10, GlacierTheme.TEXT_DIM);
            return;
        }

        // header w/ back button
        int bx = contentX(), by = contentY();
        boolean backHov = within(mouseX, mouseY, bx, by, 60, 20);
        ctx.drawTextWithShadow(textRenderer, "< Back", bx, by + 6, backHov ? GlacierTheme.ACCENT : GlacierTheme.TEXT_DIM);
        ctx.drawTextWithShadow(textRenderer, activeEditor + " Editor", bx + 80, by + 6, GlacierTheme.ACCENT);
        ctx.fill(bx, by + 22, panelX + panelW - PAD, by + 23, GlacierTheme.ACCENT_GLOW);

        switch (activeEditor) {
            case "Crosshair" -> renderModuleEditor(ctx, modules().getModule(CustomCrosshair.class), bx, by + 30, mouseX, mouseY);
            case "Bossbar"   -> renderModuleEditor(ctx, modules().getModule(BossbarCustomizer.class), bx, by + 30, mouseX, mouseY);
            case "Cosmetics" -> renderCosmeticsEditor(ctx, bx, by + 30, mouseX, mouseY);
            case "Emotes"    -> renderEmotesEditor(ctx, bx, by + 30, mouseX, mouseY);
        }
    }

    private void editorIcon(DrawContext ctx, String name, int cx, int cy, int color) {
        switch (name) {
            case "Crosshair" -> Icons.draw(ctx, textRenderer, "crosshair", "RENDER", cx, cy, 30, color);
            case "Bossbar"   -> Icons.draw(ctx, textRenderer, "fps graph", "HUD", cx, cy, 30, color);
            case "Cosmetics" -> Icons.draw(ctx, textRenderer, "wings", "COSMETICS", cx, cy, 30, color);
            case "Emotes"    -> Icons.draw(ctx, textRenderer, "person", "COSMETICS", cx, cy, 30, color);
        }
    }

    private void renderModuleEditor(DrawContext ctx, GlacierMod mod, int x, int y, int mouseX, int mouseY) {
        if (mod == null) {
            ctx.drawTextWithShadow(textRenderer, "Module unavailable.", x, y, GlacierTheme.TEXT_DIM);
            return;
        }
        // enable toggle
        ctx.drawTextWithShadow(textRenderer, "Enabled", x, y + 4, GlacierTheme.TEXT);
        renderTogglePill(ctx, x + 70, y + 2, mod.isEnabled());
        int bottom = panelY + panelH - PAD;
        ctx.enableScissor(panelX, y + 20, panelX + panelW - PAD, bottom);
        renderSettingsList(ctx, mod.getSettings(), x, y + 22, contentW() - 6, mouseX, mouseY);
        ctx.disableScissor();
    }

    private static final int COS_PREVIEW_W = 150;
    private static final int COS_COLS = 2;
    private int cosGridW() { return contentW() - COS_PREVIEW_W - 12; }
    private int cosCardW() { return (cosGridW() - CARD_GAP) / COS_COLS; }

    private void renderCosmeticsEditor(DrawContext ctx, int x, int y, int mouseX, int mouseY) {
        List<Cosmetic> cosmetics = GlacierClient.getInstance().getCosmeticManager().getCosmetics();
        int cw = cosCardW();
        int gridW = cosGridW();
        int areaBottom = panelY + panelH - PAD;
        ctx.enableScissor(panelX, y, x + gridW, areaBottom);
        int rows = (cosmetics.size() + COS_COLS - 1) / COS_COLS;
        int totalH = rows * (CARD_H + CARD_GAP);
        gridScroll = Math.max(0, Math.min(gridScroll, Math.max(0, totalH - (areaBottom - y))));
        for (int i = 0; i < cosmetics.size(); i++) {
            Cosmetic cos = cosmetics.get(i);
            int col = i % COS_COLS, row = i / COS_COLS;
            int cx = x + col * (cw + CARD_GAP);
            int cy = y + row * (CARD_H + CARD_GAP) - gridScroll;
            if (cy + CARD_H < y || cy > areaBottom) continue;
            boolean hov = within(mouseX, mouseY, cx, cy, cw, CARD_H);
            RenderUtil.drawRoundedRect(ctx, cx, cy, cw, CARD_H, GlacierTheme.RADIUS_SM, hov ? 0xFF2A2E33 : 0xFF202327);
            if (cos.isEnabled()) RenderUtil.drawOutline(ctx, cx, cy, cw, CARD_H, 1, GlacierTheme.ACCENT);
            int ic = cos.isEnabled() ? GlacierTheme.ACCENT : GlacierTheme.TEXT;
            Icons.draw(ctx, textRenderer, cos.getName(), "COSMETICS", cx + cw / 2, cy + 34, 28, withAlpha(cos.getColor(), 0xFF));
            String nm = trim(cos.getName(), cw - 12);
            ctx.drawTextWithShadow(textRenderer, nm, cx + (cw - textRenderer.getWidth(nm)) / 2, cy + CARD_H - 16, ic);
            int gx = cx + cw - 18, gy = cy + 16;
            boolean gHov = within(mouseX, mouseY, gx - 9, gy - 9, 18, 18);
            Icons.gear(ctx, gx, gy, 6, gHov ? GlacierTheme.ACCENT : GlacierTheme.TEXT_DIM);
        }
        ctx.disableScissor();

        // live try-on preview
        renderCosmeticPreview(ctx, x + gridW + 12, y, COS_PREVIEW_W, areaBottom - y, cosmetics, mouseX, mouseY);
    }

    private void renderCosmeticPreview(DrawContext ctx, int px, int py, int pw, int ph,
                                       List<Cosmetic> cosmetics, int mouseX, int mouseY) {
        RenderUtil.drawRoundedRect(ctx, px, py, pw, ph, GlacierTheme.RADIUS_SM, 0xFF17191C);
        RenderUtil.drawOutline(ctx, px, py, pw, ph, 1, GlacierTheme.ACCENT_GLOW);
        String hdr = "Try-On";
        ctx.drawTextWithShadow(textRenderer, hdr, px + (pw - textRenderer.getWidth(hdr)) / 2, py + 6, GlacierTheme.ACCENT);

        MinecraftClient mc = MinecraftClient.getInstance();
        int cx = px + pw / 2;
        int cy = py + ph / 2 + 10;

        // cosmetic shapes drawn behind the model
        for (Cosmetic cos : cosmetics) {
            if (cos.isEnabled()) drawCosmeticShape(ctx, cos, cx, cy - 20, 42);
        }

        if (mc.player != null) {
            int size = Math.min(pw, ph) / 4;
            InventoryScreen.drawEntity(ctx, px + 14, py + 24, px + pw - 14, py + ph - 14,
                    size, 0.0f, (float) mouseX, (float) mouseY, mc.player);
        } else {
            String msg = "Join a world";
            ctx.drawTextWithShadow(textRenderer, msg, px + (pw - textRenderer.getWidth(msg)) / 2, py + ph / 2, GlacierTheme.TEXT_DIM);
            String msg2 = "to preview";
            ctx.drawTextWithShadow(textRenderer, msg2, px + (pw - textRenderer.getWidth(msg2)) / 2, py + ph / 2 + 12, GlacierTheme.TEXT_DIM);
        }
    }

    private void drawCosmeticShape(DrawContext ctx, Cosmetic cos, int cx, int cy, int baseSize) {
        int col = withAlpha(cos.getColor(), 0xCC);
        int s = Math.max(8, (int) (baseSize * cos.getScale()));
        switch (cos.getCategory()) {
            case WINGS -> { previewWing(ctx, cx, cy, s, col, -1); previewWing(ctx, cx, cy, s, col, 1); }
            case CAPES -> RenderUtil.drawRoundedRect(ctx, cx - s / 3, cy - s / 4, (s * 2) / 3, s, 3, col);
            case PETS -> Icons.disc(ctx, cx + s, cy + s / 2, Math.max(3, s / 4), col);
            case HATS -> RenderUtil.drawRoundedRect(ctx, cx - s / 3, cy - s, (s * 2) / 3, Math.max(3, s / 4), 2, col);
            case WEAPONS, ITEMS -> Icons.ring(ctx, cx, cy, s, 2, col);
            default -> Icons.ring(ctx, cx, cy, s, 3, withAlpha(cos.getColor(), 0x99));
        }
    }

    private void previewWing(DrawContext ctx, int cx, int cy, int s, int col, int dir) {
        for (int i = 0; i < s; i++) {
            int h = (s - i) / 2;
            int xx = cx + dir * i;
            ctx.fill(Math.min(xx, xx + dir), cy - h, Math.max(xx, xx + dir) + 1, cy + h / 2 + 1, col);
        }
    }

    private void renderEmotesEditor(DrawContext ctx, int x, int y, int mouseX, int mouseY) {
        ctx.drawTextWithShadow(textRenderer, "Trigger emotes with their keybind in-game:", x, y, GlacierTheme.TEXT_DIM);
        int cw = cardWidth();
        for (int i = 0; i < EMOTES.length; i++) {
            int col = i % COLS, row = i / COLS;
            int cx = x + col * (cw + CARD_GAP);
            int cy = y + 16 + row * (CARD_H + CARD_GAP);
            boolean hov = within(mouseX, mouseY, cx, cy, cw, CARD_H);
            RenderUtil.drawRoundedRect(ctx, cx, cy, cw, CARD_H, GlacierTheme.RADIUS_SM, hov ? 0xFF2A2E33 : 0xFF202327);
            Icons.draw(ctx, textRenderer, "person", "COSMETICS", cx + cw / 2, cy + 34, 28, GlacierTheme.TEXT);
            String nm = EMOTES[i];
            ctx.drawTextWithShadow(textRenderer, nm, cx + (cw - textRenderer.getWidth(nm)) / 2, cy + CARD_H - 16, GlacierTheme.TEXT);
        }
    }

    // =======================================================================
    // MUSIC PLAYER TAB (visual placeholder)
    // =======================================================================

    private void renderMusicTab(DrawContext ctx, int mouseX, int mouseY) {
        int x = contentX(), y = contentY();
        int w = contentW();
        RenderUtil.drawRoundedRect(ctx, x, y, w, 120, GlacierTheme.RADIUS_SM, 0xFF202327);
        // album art placeholder
        RenderUtil.drawRoundedRect(ctx, x + 16, y + 16, 88, 88, GlacierTheme.RADIUS_SM, GlacierTheme.BG_PANEL);
        Icons.draw(ctx, textRenderer, "music note", "COSMETICS", x + 60, y + 60, 36, GlacierTheme.ACCENT);

        ctx.drawTextWithShadow(textRenderer, "Nothing playing", x + 120, y + 24, GlacierTheme.TEXT);
        ctx.drawTextWithShadow(textRenderer, "Connect a source in QoL > Spotify Media Bridge", x + 120, y + 38, GlacierTheme.TEXT_DIM);

        // progress bar
        int barX = x + 120, barY = y + 60, barW = w - 140;
        RenderUtil.drawRoundedRect(ctx, barX, barY, barW, 4, 2, GlacierTheme.BG_ITEM);
        int prog = musicPlaying ? barW / 3 : 0;
        if (prog > 0) RenderUtil.drawRoundedRect(ctx, barX, barY, prog, 4, 2, GlacierTheme.ACCENT);

        // controls
        int ctrlY = y + 84;
        drawPrevNext(ctx, barX + 10, ctrlY, false, mouseX, mouseY);
        // play/pause circle
        int pcx = barX + 44, pcy = ctrlY;
        boolean pHov = within(mouseX, mouseY, pcx - 12, pcy - 12, 24, 24);
        Icons.disc(ctx, pcx, pcy, 12, pHov ? GlacierTheme.ACCENT_HOVER : GlacierTheme.ACCENT);
        if (musicPlaying) {
            ctx.fill(pcx - 4, pcy - 5, pcx - 1, pcy + 5, GlacierTheme.BG);
            ctx.fill(pcx + 1, pcy - 5, pcx + 4, pcy + 5, GlacierTheme.BG);
        } else {
            for (int i = 0; i < 10; i++) ctx.fill(pcx - 3, pcy - 5 + i, pcx - 3 + (5 - Math.abs(i - 5)), pcy - 4 + i, GlacierTheme.BG);
        }
        drawPrevNext(ctx, barX + 78, ctrlY, true, mouseX, mouseY);
    }

    private void drawPrevNext(DrawContext ctx, int cx, int cy, boolean next, int mouseX, int mouseY) {
        boolean hov = within(mouseX, mouseY, cx - 10, cy - 10, 20, 20);
        int c = hov ? GlacierTheme.ACCENT : GlacierTheme.TEXT_DIM;
        int dir = next ? 1 : -1;
        for (int i = 0; i < 6; i++) {
            ctx.fill(cx + dir * (i - 3), cy - i, cx + dir * (i - 3) + 1, cy + i, c);
        }
        ctx.fill(cx + dir * 4, cy - 6, cx + dir * 4 + 2, cy + 6, c);
    }

    // =======================================================================
    // SETTINGS POPUP
    // =======================================================================

    private void openSettings(String title, String desc, List<Setting<?>> settings, Runnable toggle,
                              BooleanSupplier enabled, String styleKey) {
        popupOpen = true;
        popupTitle = title;
        popupDesc = desc;
        popupSettings = settings;
        popupToggle = toggle;
        popupEnabled = enabled;
        popupKey = styleKey;
        popupAppearance = styleKey != null;
        popupTab = PopupTab.SETTINGS;
        popupScroll = 0;
        openDropdown = null;
        popupBindTarget = null;
        awaitingBind = false;
    }

    private void closePopup() {
        popupOpen = false;
        openDropdown = null;
        draggingSlider = null;
        awaitingBind = false;
        popupBindTarget = null;
        colorPicker.close();
    }

    private int popupX() { return panelX + (panelW - POPUP_W) / 2; }
    private int popupY() { return panelY + (panelH - POPUP_H) / 2; }
    private int POPUP_W = 270, POPUP_H = 250;

    private void renderPopup(DrawContext ctx, int mouseX, int mouseY) {
        ctx.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0x66000000);
        int x = popupX(), y = popupY();
        if (GuiTextures.has("secondary_bg")) {
            GuiTextures.nineSlice(ctx, "secondary_bg", x, y, POPUP_W, POPUP_H);
        } else {
            RenderUtil.drawRoundedRect(ctx, x, y, POPUP_W, POPUP_H, GlacierTheme.RADIUS_MD, GlacierTheme.BG_PANEL);
            RenderUtil.drawOutline(ctx, x, y, POPUP_W, POPUP_H, 1, GlacierTheme.ACCENT);
        }

        ctx.drawTextWithShadow(textRenderer, popupTitle, x + 12, y + 12, GlacierTheme.ACCENT);
        ctx.drawTextWithShadow(textRenderer, trim(popupDesc, POPUP_W - 24), x + 12, y + 24, GlacierTheme.TEXT_DIM);

        if (popupToggle != null && popupEnabled != null) {
            ctx.drawTextWithShadow(textRenderer, "Enabled", x + 12, y + 40, GlacierTheme.TEXT);
            renderTogglePill(ctx, x + 70, y + 38, popupEnabled.getAsBoolean());
        }

        // sub-tabs
        if (popupAppearance) {
            drawSubTab(ctx, "Settings", x + 110, y + 38, popupTab == PopupTab.SETTINGS, mouseX, mouseY);
            drawSubTab(ctx, "Appearance", x + 110 + textRenderer.getWidth("Settings") + 16, y + 38, popupTab == PopupTab.APPEARANCE, mouseX, mouseY);
        }

        // close X
        int xc = x + POPUP_W - 16, yc = y + 16;
        boolean xHov = within(mouseX, mouseY, xc - 8, yc - 8, 16, 16);
        drawX(ctx, xc, yc, 5, xHov ? GlacierTheme.RED : GlacierTheme.TEXT_DIM);

        ctx.fill(x, y + 56, x + POPUP_W, y + 57, GlacierTheme.ACCENT_GLOW);

        if (popupTab == PopupTab.APPEARANCE) {
            renderAppearance(ctx, x, y + 62, mouseX, mouseY);
            return;
        }

        boolean footer = popupBindTarget != null;
        int listBottom = footer ? y + POPUP_H - 22 : y + POPUP_H - 8;
        if (popupSettings.isEmpty()) {
            ctx.drawTextWithShadow(textRenderer, "No settings", x + 12, y + 66, GlacierTheme.TEXT_DIM);
        } else {
            ctx.enableScissor(x, y + 58, x + POPUP_W, listBottom);
            renderSettingsList(ctx, popupSettings, x + 12, y + 62 - popupScroll, POPUP_W - 24, mouseX, mouseY);
            ctx.disableScissor();
        }
        if (footer) renderBindButton(ctx, x, y, mouseX, mouseY);
    }

    private void renderBindButton(DrawContext ctx, int x, int y, int mouseX, int mouseY) {
        int by = y + POPUP_H - 18, bw = POPUP_W - 24;
        boolean hov = within(mouseX, mouseY, x + 12, by, bw, 14);
        int bg = awaitingBind ? GlacierTheme.ACCENT_BG : (hov ? GlacierTheme.BG_ITEM_HOVER : GlacierTheme.BG_ITEM);
        RenderUtil.drawRoundedRect(ctx, x + 12, by, bw, 14, 6, bg);
        String label = "Keybind: " + (awaitingBind ? "press a key (Esc to clear)" : keyName(popupBindTarget.getKeybind()));
        ctx.drawTextWithShadow(textRenderer, label, x + 18, by + 3, awaitingBind ? GlacierTheme.ACCENT : GlacierTheme.TEXT_DIM);
    }

    private String keyName(int code) {
        if (code < 0) return "None";
        try { return InputUtil.fromKeyCode(code, 0).getLocalizedText().getString(); }
        catch (Exception e) { return String.valueOf(code); }
    }

    private void drawSubTab(DrawContext ctx, String label, int x, int y, boolean sel, int mouseX, int mouseY) {
        int w = textRenderer.getWidth(label) + 12;
        boolean hov = within(mouseX, mouseY, x, y, w, 14);
        RenderUtil.drawRoundedRect(ctx, x, y, w, 14, 6, sel ? GlacierTheme.ACCENT : (hov ? GlacierTheme.BG_ITEM_HOVER : GlacierTheme.BG_ITEM));
        ctx.drawTextWithShadow(textRenderer, label, x + 6, y + 3, sel ? GlacierTheme.TEXT : GlacierTheme.TEXT_DIM);
    }

    // appearance row layout
    private int appBaseY;
    private int appRowY(int i) { return appBaseY + 44 + i * 22; }

    private void renderAppearance(DrawContext ctx, int x, int y, int mouseX, int mouseY) {
        appBaseY = y;
        CardStyle st = GuiCustomization.get().styleFor(popupKey);

        // live preview card
        ctx.drawTextWithShadow(textRenderer, "Preview", x + 12, y, GlacierTheme.TEXT_DIM);
        drawStyledCard(ctx, st, x + 12, y + 12, POPUP_W - 24, 28, false, popupEnabled != null && popupEnabled.getAsBoolean(), false);
        ctx.drawTextWithShadow(textRenderer, popupTitle, x + 20, y + 22, st.accentColor);

        int rx = x + 12;
        int swX = x + POPUP_W - 12 - 16;
        appRow(ctx, "Background", rx, appRowY(0));
        swatch(ctx, swX, appRowY(0) - 2, 16, st.bgColor);
        appRow(ctx, "Gradient", rx, appRowY(1));
        swatch(ctx, swX, appRowY(1) - 2, 16, st.bgColor2);
        appRow(ctx, "Accent", rx, appRowY(2));
        swatch(ctx, swX, appRowY(2) - 2, 16, st.accentColor);

        appRow(ctx, "Style", rx, appRowY(3));
        int btnX = x + POPUP_W - 12 - 80;
        boolean sHov = within(mouseX, mouseY, btnX, appRowY(3) - 2, 80, 14);
        RenderUtil.drawRoundedRect(ctx, btnX, appRowY(3) - 2, 80, 14, 6, sHov ? GlacierTheme.ACCENT_BG : GlacierTheme.BG_ITEM);
        ctx.drawTextWithShadow(textRenderer, cap(st.style.name()), btnX + 6, appRowY(3) + 1, GlacierTheme.ACCENT);

        appRow(ctx, "Radius", rx, appRowY(4));
        int barX = x + POPUP_W - 12 - 90, barW = 90;
        ctx.fill(barX, appRowY(4) + 3, barX + barW, appRowY(4) + 7, GlacierTheme.BG_ITEM);
        int fillW = (int) (barW * (st.radius / 16f));
        ctx.fill(barX, appRowY(4) + 3, barX + fillW, appRowY(4) + 7, st.accentColor);
        ctx.drawTextWithShadow(textRenderer, String.valueOf(st.radius), barX + barW - 14, appRowY(4) - 6, GlacierTheme.TEXT_DIM);

        boolean rHov = within(mouseX, mouseY, rx, appRowY(5), 110, 14);
        ctx.drawTextWithShadow(textRenderer, "↺ Reset to default", rx, appRowY(5) + 2, rHov ? GlacierTheme.RED : GlacierTheme.TEXT_DIM);
    }

    private void appRow(DrawContext ctx, String label, int x, int y) {
        ctx.drawTextWithShadow(textRenderer, label, x, y + 2, GlacierTheme.TEXT);
    }

    private void swatch(DrawContext ctx, int x, int y, int size, int color) {
        // checker for alpha
        for (int iy = 0; iy < size; iy += 4)
            for (int ix = 0; ix < size; ix += 4) {
                boolean d = ((ix / 4) + (iy / 4)) % 2 == 0;
                ctx.fill(x + ix, y + iy, Math.min(x + ix + 4, x + size), Math.min(y + iy + 4, y + size), d ? 0xFF888888 : 0xFFCCCCCC);
            }
        ctx.fill(x, y, x + size, y + size, color);
        RenderUtil.drawOutline(ctx, x, y, size, size, 1, GlacierTheme.TEXT_DIM);
    }

    private String cap(String s) { return s.charAt(0) + s.substring(1).toLowerCase(); }

    // =======================================================================
    // SETTINGS RENDERING (shared)
    // =======================================================================

    private static final int SETTING_H = 26;

    private void renderSettingsList(DrawContext ctx, List<Setting<?>> settings, int x, int y, int w, int mouseX, int mouseY) {
        int sy = y;
        for (Setting<?> s : settings) {
            renderSetting(ctx, s, x, sy, w, mouseX, mouseY);
            sy += SETTING_H + 4;
        }
    }

    private void renderSetting(DrawContext ctx, Setting<?> setting, int x, int y, int w, int mouseX, int mouseY) {
        ctx.drawTextWithShadow(textRenderer, setting.getName(), x, y + 2, GlacierTheme.TEXT_DIM);
        if (setting instanceof BooleanSetting bs) {
            renderTogglePill(ctx, x + w - 28, y, bs.getValue());
        } else if (setting instanceof NumberSetting ns) {
            renderSlider(ctx, ns, x, y + 14, w, mouseX, mouseY);
        } else if (setting instanceof ModeSetting ms) {
            int btnX = x + w - 84;
            boolean hov = within(mouseX, mouseY, btnX, y, 84, 16);
            RenderUtil.drawRoundedRect(ctx, btnX, y, 84, 16, GlacierTheme.RADIUS_SM, hov ? GlacierTheme.ACCENT_BG : GlacierTheme.BG_ITEM);
            ctx.drawTextWithShadow(textRenderer, trim(ms.getValue(), 76), btnX + 4, y + 4, GlacierTheme.ACCENT);
        } else if (setting instanceof StringSetting ss) {
            RenderUtil.drawRoundedRect(ctx, x, y + 12, w, 14, GlacierTheme.RADIUS_SM, GlacierTheme.BG_ITEM);
            ctx.drawTextWithShadow(textRenderer, trim(ss.getValue(), w - 8), x + 4, y + 15, GlacierTheme.TEXT);
        } else if (setting instanceof ColorSetting cs) {
            int sw = 14, swX = x + w - sw, swY = y;
            ctx.fill(swX, swY, swX + sw, swY + sw, 0xFF000000 | (cs.getValue() & 0xFFFFFF));
            RenderUtil.drawOutline(ctx, swX, swY, sw, sw, 1, GlacierTheme.TEXT_DIM);
        }
    }

    private void renderSlider(DrawContext ctx, NumberSetting ns, int x, int y, int w, int mouseX, int mouseY) {
        RenderUtil.drawRoundedRect(ctx, x, y, w, 4, 2, GlacierTheme.BG_ITEM);
        int fillW = (int) (w * ns.getPercent());
        if (fillW > 0) RenderUtil.drawRoundedRect(ctx, x, y, fillW, 4, 2, GlacierTheme.ACCENT);
        int knobX = x + fillW;
        Icons.disc(ctx, knobX, y + 2, 4, GlacierTheme.ACCENT_HOVER);
        String val = fmt(ns.getValue());
        ctx.drawTextWithShadow(textRenderer, val, x + w - textRenderer.getWidth(val), y - 12, GlacierTheme.TEXT);
        if (draggingSlider == ns) { sliderBarX = x; sliderBarW = w; }
    }

    private void renderTogglePill(DrawContext ctx, int x, int y, boolean on) {
        int pw = 24, ph = 12;
        RenderUtil.drawRoundedRect(ctx, x, y, pw, ph, ph / 2, on ? GlacierTheme.ACCENT : GlacierTheme.BG_ITEM_HOVER);
        int k = ph - 4;
        int kx = on ? x + pw - k - 2 : x + 2;
        Icons.disc(ctx, kx + k / 2, y + 2 + k / 2, (k + 1) / 2, GlacierTheme.TEXT);
    }

    private void renderDropdown(DrawContext ctx, ModeSetting ms, int x, int y, int mouseX, int mouseY) {
        int itemH = 16, w = 84;
        int h = ms.getModes().size() * itemH;
        RenderUtil.drawRoundedRect(ctx, x, y, w, h, GlacierTheme.RADIUS_SM, GlacierTheme.BG);
        RenderUtil.drawOutline(ctx, x, y, w, h, 1, GlacierTheme.ACCENT);
        int iy = y;
        for (String mode : ms.getModes()) {
            boolean hov = within(mouseX, mouseY, x, iy, w, itemH);
            if (hov) ctx.fill(x, iy, x + w, iy + itemH, GlacierTheme.BG_ITEM_HOVER);
            ctx.drawTextWithShadow(textRenderer, trim(mode, w - 8), x + 4, iy + 4, mode.equals(ms.getValue()) ? GlacierTheme.ACCENT : GlacierTheme.TEXT);
            iy += itemH;
        }
    }

    // =======================================================================
    // INPUT
    // =======================================================================

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int x = (int) mx, y = (int) my;

        // color picker is top-most
        if (colorPicker.isOpen()) { colorPicker.mouseClicked(mx, my); return true; }

        // dropdown first
        if (openDropdown != null) {
            int itemH = 16, w = 84;
            if (within(x, y, dropdownX, dropdownY, w, openDropdown.getModes().size() * itemH)) {
                int idx = (y - dropdownY) / itemH;
                if (idx >= 0 && idx < openDropdown.getModes().size()) openDropdown.setValue(openDropdown.getModes().get(idx));
            }
            openDropdown = null;
            return true;
        }

        // popup
        if (popupOpen) {
            int px = popupX(), py = popupY();
            if (within(x, y, px + POPUP_W - 24, py + 8, 24, 24)) { closePopup(); return true; } // X
            if (!within(x, y, px, py, POPUP_W, POPUP_H)) { closePopup(); return true; }          // outside
            if (popupToggle != null && within(x, y, px + 70, py + 38, 24, 12)) { popupToggle.run(); return true; }
            // sub-tabs
            if (popupAppearance) {
                int t1 = px + 110, t1w = textRenderer.getWidth("Settings") + 12;
                int t2 = px + 110 + textRenderer.getWidth("Settings") + 16, t2w = textRenderer.getWidth("Appearance") + 12;
                if (within(x, y, t1, py + 38, t1w, 14)) { popupTab = PopupTab.SETTINGS; return true; }
                if (within(x, y, t2, py + 38, t2w, 14)) { popupTab = PopupTab.APPEARANCE; return true; }
            }
            if (popupTab == PopupTab.APPEARANCE) { handleAppearanceClick(px, py + 62, x, y); return true; }
            // keybind footer
            if (popupBindTarget != null && within(x, y, px + 12, py + POPUP_H - 18, POPUP_W - 24, 14)) {
                awaitingBind = true;
                return true;
            }
            if (handleSettingsClick(popupSettings, px + 12, py + 62 - popupScroll, POPUP_W - 24, x, y)) return true;
            return true;
        }

        // close X
        if (within(x, y, panelX + panelW - 34, panelY + HEADER_H / 2 - 10, 24, 20)) { close(); return true; }

        // tabs
        int tx = panelX + PAD, ty = panelY + HEADER_H + 6;
        Tab[] tabs = Tab.values();
        String[] labels = {"Modules", "Elements", "Editors", "Music Player"};
        for (int i = 0; i < tabs.length; i++) {
            int tw = textRenderer.getWidth(labels[i]) + 30;
            if (within(x, y, tx, ty, tw, TAB_H - 14)) {
                activeTab = tabs[i];
                gridScroll = 0;
                activeEditor = null;
                return true;
            }
            tx += tw + 6;
        }

        switch (activeTab) {
            case MODULES -> { return clickModuleGrid(getFilteredModules(), contentX(), contentY(), x, y, button, true); }
            case ELEMENTS -> {
                if (within(x, y, contentX(), contentY(), 150, 24)) {
                    MinecraftClient.getInstance().setScreen(new HUDEditorScreen());
                    return true;
                }
                return clickModuleGrid(modules().getModulesByCategory(Category.HUD), contentX(), contentY() + 34, x, y, button, false);
            }
            case EDITORS -> { return clickEditors(x, y); }
            case MUSIC -> { return clickMusic(x, y); }
        }
        return super.mouseClicked(mx, my, button);
    }

    private boolean clickModuleGrid(List<GlacierMod> mods, int gx, int gy, int x, int y, int button, boolean draggable) {
        int cw = cardWidth();
        int areaBottom = panelY + panelH - PAD;
        for (int i = 0; i < mods.size(); i++) {
            int col = i % COLS, row = i / COLS;
            int cx = gx + col * (cw + CARD_GAP);
            int cy = gy + row * (CARD_H + CARD_GAP) - gridScroll;
            if (cy + CARD_H < gy || cy > areaBottom) continue;
            if (!within(x, y, cx, cy, cw, CARD_H)) continue;
            GlacierMod mod = mods.get(i);
            // gear?
            if (!mod.getSettings().isEmpty() && within(x, y, cx + cw - 27, cy + 7, 18, 18)) {
                openModuleSettings(mod);
                return true;
            }
            if (button == 1 || !draggable) { // right-click or non-draggable → just settings/toggle
                if (button == 1) openModuleSettings(mod); else mod.toggle();
                return true;
            }
            // left-click on draggable card → defer (could be drag)
            pressedCard = mod;
            pressX = x; pressY = y;
            return true;
        }
        return false;
    }

    private void openModuleSettings(GlacierMod mod) {
        openSettings(mod.getName(), mod.getDescription(), mod.getSettings(), mod::toggle, mod::isEnabled, mod.getName());
        popupBindTarget = mod;
    }

    private boolean clickEditors(int x, int y) {
        if (activeEditor == null) {
            String[] names = {"Crosshair", "Bossbar", "Cosmetics", "Emotes"};
            int cw = cardWidth();
            for (int i = 0; i < names.length; i++) {
                int col = i % COLS, row = i / COLS;
                int cx = contentX() + col * (cw + CARD_GAP);
                int cy = contentY() + row * (CARD_H + CARD_GAP);
                if (within(x, y, cx, cy, cw, CARD_H)) { activeEditor = names[i]; gridScroll = 0; return true; }
            }
            return false;
        }
        // back
        if (within(x, y, contentX(), contentY(), 60, 20)) { activeEditor = null; gridScroll = 0; return true; }

        int bx = contentX(), by = contentY() + 30;
        switch (activeEditor) {
            case "Crosshair" -> { return clickModuleEditor(modules().getModule(CustomCrosshair.class), bx, by, x, y); }
            case "Bossbar"   -> { return clickModuleEditor(modules().getModule(BossbarCustomizer.class), bx, by, x, y); }
            case "Cosmetics" -> { return clickCosmeticsEditor(bx, by, x, y); }
            default -> { return false; }
        }
    }

    private boolean clickModuleEditor(GlacierMod mod, int x, int y, int mx, int my) {
        if (mod == null) return false;
        if (within(mx, my, x + 70, y + 2, 24, 12)) { mod.toggle(); return true; }
        return handleSettingsClick(mod.getSettings(), x, y + 22, contentW() - 6, mx, my);
    }

    private boolean clickCosmeticsEditor(int x, int y, int mx, int my) {
        List<Cosmetic> cosmetics = GlacierClient.getInstance().getCosmeticManager().getCosmetics();
        int cw = cosCardW();
        int areaBottom = panelY + panelH - PAD;
        for (int i = 0; i < cosmetics.size(); i++) {
            int col = i % COS_COLS, row = i / COS_COLS;
            int cx = x + col * (cw + CARD_GAP);
            int cy = y + row * (CARD_H + CARD_GAP) - gridScroll;
            if (cy + CARD_H < y || cy > areaBottom) continue;
            if (!within(mx, my, cx, cy, cw, CARD_H)) continue;
            Cosmetic cos = cosmetics.get(i);
            if (within(mx, my, cx + cw - 27, cy + 7, 18, 18)) {
                openSettings(cos.getName(), cos.getDescription(), cos.getSettings(), cos::toggle, cos::isEnabled, null);
            } else {
                cos.toggle();
            }
            return true;
        }
        return false;
    }

    private boolean clickMusic(int x, int y) {
        int barX = contentX() + 120;
        int pcx = barX + 44, pcy = contentY() + 84;
        if (within(x, y, pcx - 12, pcy - 12, 24, 24)) { musicPlaying = !musicPlaying; return true; }
        return false;
    }

    private boolean handleSettingsClick(List<Setting<?>> settings, int x, int y, int w, int mx, int my) {
        int sy = y;
        for (Setting<?> s : settings) {
            if (s instanceof BooleanSetting bs) {
                if (within(mx, my, x + w - 28, sy, 24, 12)) { bs.setValue(!bs.getValue()); return true; }
            } else if (s instanceof NumberSetting ns) {
                int slY = sy + 14;
                if (within(mx, my, x, slY - 3, w, 10)) {
                    draggingSlider = ns; sliderBarX = x; sliderBarW = w;
                    double pct = clamp01((double) (mx - x) / w);
                    ns.setValue(ns.getMin() + pct * (ns.getMax() - ns.getMin()));
                    return true;
                }
            } else if (s instanceof ModeSetting ms) {
                int btnX = x + w - 84;
                if (within(mx, my, btnX, sy, 84, 16)) {
                    openDropdown = ms;
                    int ddH = ms.getModes().size() * 16;
                    int bottom = panelY + panelH - PAD;
                    dropdownY = (sy + 16 + ddH > bottom) ? sy - ddH : sy + 16; // open upward if no room
                    dropdownX = Math.max(panelX + PAD, Math.min(btnX, panelX + panelW - PAD - 84));
                    return true;
                }
            } else if (s instanceof ColorSetting cs) {
                if (within(mx, my, x + w - 14, sy, 14, 14)) { openPickerFor(cs::setValue, cs.getValue(), mx, my); return true; }
            }
            sy += SETTING_H + 4;
        }
        return false;
    }

    private void handleAppearanceClick(int px, int baseY, int mx, int my) {
        CardStyle st = GuiCustomization.get().styleFor(popupKey);
        int swX = px + POPUP_W - 12 - 16;
        int[] r = new int[6];
        for (int i = 0; i < 6; i++) r[i] = baseY + 44 + i * 22;
        if (within(mx, my, swX, r[0] - 2, 16, 16)) { openPickerFor(c -> st.bgColor = c, st.bgColor, mx, my); return; }
        if (within(mx, my, swX, r[1] - 2, 16, 16)) { openPickerFor(c -> st.bgColor2 = c, st.bgColor2, mx, my); return; }
        if (within(mx, my, swX, r[2] - 2, 16, 16)) { openPickerFor(c -> st.accentColor = c, st.accentColor, mx, my); return; }
        int btnX = px + POPUP_W - 12 - 80;
        if (within(mx, my, btnX, r[3] - 2, 80, 14)) {
            CardStyle.Style[] styles = CardStyle.Style.values();
            st.style = styles[(st.style.ordinal() + 1) % styles.length];
            return;
        }
        int barX = px + POPUP_W - 12 - 90, barW = 90;
        if (within(mx, my, barX, r[4] - 1, barW, 12)) {
            st.radius = Math.max(0, Math.min(16, Math.round((float) (mx - barX) / barW * 16)));
            return;
        }
        if (within(mx, my, px + 12, r[5], 110, 14)) { GuiCustomization.get().resetStyle(popupKey); }
    }

    private void openPickerFor(java.util.function.IntConsumer onChange, int current, int mx, int my) {
        int cx = panelX + (panelW - ColorPicker.W) / 2;
        int cy = panelY + panelH - ColorPicker.H - 16;
        colorPicker.open(cx, cy, current, onChange);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (colorPicker.isOpen() && colorPicker.mouseDragged(mx, my)) return true;
        if (draggingSlider != null && sliderBarW > 0) {
            double pct = clamp01((mx - sliderBarX) / sliderBarW);
            draggingSlider.setValue(draggingSlider.getMin() + pct * (draggingSlider.getMax() - draggingSlider.getMin()));
            return true;
        }
        if (pressedCard != null) {
            if (Math.abs(mx - pressX) > DRAG_THRESHOLD || Math.abs(my - pressY) > DRAG_THRESHOLD) {
                draggingCard = pressedCard;
            }
        }
        if (draggingCard != null) { dragMouseX = (int) mx; dragMouseY = (int) my; return true; }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (colorPicker.isOpen()) { colorPicker.mouseReleased(); return true; }
        if (draggingSlider != null) { draggingSlider = null; return true; }
        if (draggingCard != null) {
            GlacierMod target = cardAt((int) mx, (int) my, getFilteredModules());
            if (target != null && target != draggingCard) modules().moveModule(draggingCard, target);
            draggingCard = null; pressedCard = null;
            return true;
        }
        if (pressedCard != null) { // a click, not a drag
            pressedCard.toggle();
            pressedCard = null;
            return true;
        }
        return super.mouseReleased(mx, my, button);
    }

    private GlacierMod cardAt(int x, int y, List<GlacierMod> mods) {
        int gx = contentX(), gy = contentY(), cw = cardWidth();
        for (int i = 0; i < mods.size(); i++) {
            int col = i % COLS, row = i / COLS;
            int cx = gx + col * (cw + CARD_GAP);
            int cy = gy + row * (CARD_H + CARD_GAP) - gridScroll;
            if (within(x, y, cx, cy, cw, CARD_H)) return mods.get(i);
        }
        return null;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hAmt, double vAmt) {
        if (popupOpen) {
            popupScroll = Math.max(0, popupScroll - (int) (vAmt * 16));
            return true;
        }
        gridScroll = Math.max(0, gridScroll - (int) (vAmt * 24));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // capturing a keybind takes priority over everything (incl. Esc, which clears it)
        if (awaitingBind && popupBindTarget != null) {
            popupBindTarget.setKeybind(keyCode == GLFW.GLFW_KEY_ESCAPE ? -1 : keyCode);
            awaitingBind = false;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (colorPicker.isOpen()) { colorPicker.close(); return true; }
            if (openDropdown != null) { openDropdown = null; return true; }
            if (popupOpen) { closePopup(); return true; }
            if (activeEditor != null) { activeEditor = null; return true; }
            close();
            return true;
        }
        if (searchFocused && keyCode == GLFW.GLFW_KEY_BACKSPACE && !searchQuery.isEmpty()) {
            searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
            gridScroll = 0;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchFocused && chr >= 32 && searchQuery.length() < 32) {
            searchQuery += chr;
            gridScroll = 0;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    // =======================================================================
    // HELPERS
    // =======================================================================

    private List<GlacierMod> getFilteredModules() {
        List<GlacierMod> all = modules().getModules();
        if (searchQuery.isEmpty()) return all;
        List<GlacierMod> out = new ArrayList<>();
        String q = searchQuery.toLowerCase();
        for (GlacierMod m : all) if (m.getName().toLowerCase().contains(q)) out.add(m);
        return out;
    }

    private boolean within(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private void drawX(DrawContext ctx, int cx, int cy, int r, int color) {
        for (int i = -r; i <= r; i++) {
            ctx.fill(cx + i, cy + i, cx + i + 2, cy + i + 2, color);
            ctx.fill(cx + i, cy - i, cx + i + 2, cy - i + 2, color);
        }
    }

    private String trim(String s, int maxW) {
        if (textRenderer.getWidth(s) <= maxW) return s;
        while (s.length() > 1 && textRenderer.getWidth(s + "..") > maxW) s = s.substring(0, s.length() - 1);
        return s + "..";
    }

    private int blend(int a, int b, float t) {
        int ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF, aa = (a >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF, ba = (b >> 24) & 0xFF;
        return ((int) (aa + (ba - aa) * t) << 24) | ((int) (ar + (br - ar) * t) << 16)
                | ((int) (ag + (bg - ag) * t) << 8) | (int) (ab + (bb - ab) * t);
    }

    private int withAlpha(int color, int alpha) { return (alpha << 24) | (color & 0xFFFFFF); }

    private int lighten(int color, int amount) {
        int a = (color >>> 24) & 0xFF;
        int r = Math.min(255, ((color >> 16) & 0xFF) + amount);
        int g = Math.min(255, ((color >> 8) & 0xFF) + amount);
        int b = Math.min(255, (color & 0xFF) + amount);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /** Draws a card background according to its {@link CardStyle}. */
    private void drawStyledCard(DrawContext ctx, CardStyle st, int x, int y, int w, int h,
                                boolean hovered, boolean enabled, boolean floating) {
        // PNG nine-slice card background takes priority over the drawn style when present.
        if (GuiTextures.has("modules_base_bg")) {
            GuiTextures.nineSlice(ctx, "modules_base_bg", x, y, w, h);
            if (enabled && GuiTextures.has("accent_bg")) GuiTextures.nineSlice(ctx, "accent_bg", x, y, w, 2);
            else if (enabled) RenderUtil.drawOutline(ctx, x, y, w, h, 1, st.accentColor);
            return;
        }
        int bg = hovered ? lighten(st.bgColor, 12) : st.bgColor;
        int bg2 = hovered ? lighten(st.bgColor2, 12) : st.bgColor2;
        if (floating) { bg = withAlpha(bg, 0xE0); bg2 = withAlpha(bg2, 0xE0); }
        int rad = Math.max(0, Math.min(st.radius, Math.min(w, h) / 2));
        switch (st.style) {
            case SOLID -> RenderUtil.drawRoundedRect(ctx, x, y, w, h, rad, bg);
            case GRADIENT -> {
                RenderUtil.drawRoundedRect(ctx, x, y, w, h, rad, bg);
                if (w > rad * 2 && h > rad * 2)
                    ctx.fillGradient(x + rad, y + rad, x + w - rad, y + h - rad, withAlpha(bg2, 0), bg2);
            }
            case GLASS -> {
                RenderUtil.drawRoundedRect(ctx, x, y, w, h, rad, withAlpha(bg, 0x88));
                RenderUtil.drawOutline(ctx, x, y, w, h, 1, withAlpha(st.accentColor, 0x66));
            }
            case OUTLINE -> {
                RenderUtil.drawRoundedRect(ctx, x, y, w, h, rad, withAlpha(bg, 0x22));
                RenderUtil.drawOutline(ctx, x, y, w, h, 1, st.accentColor);
            }
        }
        if (enabled) RenderUtil.drawOutline(ctx, x, y, w, h, 1, st.accentColor);
    }

    private double clamp01(double v) { return Math.max(0, Math.min(1, v)); }

    private String fmt(double v) {
        if (v == Math.floor(v)) return String.valueOf((int) v);
        return String.format("%.2f", v);
    }

    @Override
    public void close() {
        GuiCustomization.get().save();
        super.close();
    }

    @Override public boolean shouldPause() { return false; }
    @Override public boolean shouldCloseOnEsc() { return true; }
}
