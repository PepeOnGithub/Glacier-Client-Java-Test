package net.glacierclient.gui.screens;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.cosmetic.Cosmetic;
import net.glacierclient.core.cosmetic.CosmeticCategory;
import net.glacierclient.core.customization.CardStyle;
import net.glacierclient.core.customization.GuiCustomization;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.ModuleManager;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.theme.GlacierTheme;
import net.glacierclient.core.util.AnimationUtil;
import net.glacierclient.core.util.GuiTextures;
import net.glacierclient.core.util.FontAwesome;
import net.glacierclient.core.util.Icons;
import net.glacierclient.core.util.RenderUtil;
import net.glacierclient.gui.widget.ColorPicker;
import net.glacierclient.modules.engine.SpotifyMediaBridge;
import net.glacierclient.modules.render.BossbarCustomizer;
import net.glacierclient.modules.render.CustomCrosshair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;

/**
 * Glacier ClickGUI — card-grid layout with top tabs (Modules / Editors / Spotify) and a tab-row
 * search bar, draggable module cards, a floating per-module settings popup, and dedicated editors.
 */
public class ClickGUIScreen extends Screen {

    private enum Tab { MODULES, EDITORS, SPOTIFY }

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
    private long openTime;   // for the open ease-in animation

    // Category (folder) sub-tabs for the Modules view. "all" = no folder filter.
    private static final int SUBTAB_H = 26;
    private static final String[] CAT_KEYS   = {"all","hud","render","pvp","performance","qol","advanced","engine","expanded"};
    private static final String[] CAT_LABELS = {"All","HUD","Render","PvP","Perf","QoL","Advanced","Engine","Expanded"};
    private String folderFilter = "all";
    private final java.util.Map<int[], String> catTabHit = new java.util.LinkedHashMap<>(); // hitbox -> key (rebuilt each render)

    // search
    private String searchQuery = "";
    private boolean searchFocused = false;
    private final List<GlacierMod> filteredModules = new ArrayList<>();
    private String lastFilterQuery = null;
    private int lastFilterSourceSize = -1;

    // grid scroll (pixels) per tab
    private int gridScroll = 0;

    // search bar geometry (lives in the tab row, computed each render)
    private int searchX, searchY, searchW, searchH;

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
    private StringSetting editingString;   // string setting currently being typed into (null = none)

    private static final String[] EMOTES = {"Dab", "Wave", "Spin", "Flex", "Bow"};

    /** Screen to return to when this one closes (e.g. the Glacier title screen). Null = resume game. */
    private final Screen parent;

    public ClickGUIScreen() { this(null); }

    public ClickGUIScreen(Screen parent) {
        super(Text.literal("Glacier Client"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        panelW = Math.min(width - 80, 400);
        panelH = Math.min(height - 80, 300);
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
        // The settings page fills the panel body beneath the header.
        POPUP_W = panelW - 12;
        POPUP_H = panelH - HEADER_H - 8;
        openTime = System.currentTimeMillis();
    }

    private ModuleManager modules() { return GlacierClient.getInstance().getModuleManager(); }

    // =======================================================================
    // RENDER
    // =======================================================================

    /**
     * Background layer. In-game we draw nothing so the live world shows through (un-dimmed); on the
     * title/menu (no world) we paint the Glacier bg so the panel doesn't float over the vanilla dirt.
     */
    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        if (MinecraftClient.getInstance().world == null) {
            if (GuiTextures.has("bg")) GuiTextures.fullscreen(ctx, "bg", width, height);
            else ctx.fillGradient(0, 0, width, height, 0xFF14181D, 0xFF0A0C0F);
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);

        // subtle ease-out scale when the menu opens, multiplied by the user's GUI scale setting.
        float t = Math.min(1f, (System.currentTimeMillis() - openTime) / 160f);
        float anim = 1f - (1f - t) * (1f - t); // ease-out quad
        float scale = guiScale() * (0.96f + 0.04f * anim);

        float cx = panelX + panelW / 2f, cy = panelY + panelH / 2f;
        ctx.getMatrices().push();
        ctx.getMatrices().translate(cx, cy, 0);
        ctx.getMatrices().scale(scale, scale, 1f);
        ctx.getMatrices().translate(-cx, -cy, 0);

        // Transform the mouse into panel-local space so hover hit-testing matches the scaled render.
        mouseX = (int) localX(mouseX);
        mouseY = (int) localY(mouseY);

        RenderUtil.drawShadow(ctx, panelX, panelY, panelW, panelH, 6, 0x50000000);
        if (GuiTextures.has("base_bg")) {
            GuiTextures.nineSlice(ctx, "base_bg", panelX, panelY, panelW, panelH);
        } else {
            RenderUtil.drawRoundedRect(ctx, panelX, panelY, panelW, panelH, GlacierTheme.RADIUS_MD, GlacierTheme.BG);
            RenderUtil.drawOutline(ctx, panelX, panelY, panelW, panelH, 1, GlacierTheme.ACCENT_GLOW);
        }
        // Black edge vignette (Bedrock-style darkened panel edges).
        RenderUtil.drawEdgeVignette(ctx, panelX, panelY, panelW, panelH, 14, 0x66000000);

        renderHeader(ctx, mouseX, mouseY);

        // A module's settings take over the whole panel as a dedicated page (the grid/tabs are
        // hidden), with a Back button — instead of floating as a modal over the list.
        if (popupOpen) {
            renderSettingsPage(ctx, mouseX, mouseY);
        } else {
            renderTabs(ctx, mouseX, mouseY);
            switch (activeTab) {
                case MODULES  -> renderModulesTab(ctx, mouseX, mouseY);
                case EDITORS  -> renderEditorsTab(ctx, mouseX, mouseY);
                case SPOTIFY  -> renderSpotifyTab(ctx, mouseX, mouseY);
            }
            // dragged card floats on top
            if (draggingCard != null) {
                int cw = cardWidth();
                renderCard(ctx, draggingCard, dragMouseX - cw / 2, dragMouseY - CARD_H / 2, cw, mouseX, mouseY, true);
            }
        }

        if (openDropdown != null) renderDropdown(ctx, openDropdown, dropdownX, dropdownY, mouseX, mouseY);
        if (colorPicker.isOpen()) colorPicker.render(ctx, textRenderer, mouseX, mouseY);

        ctx.getMatrices().pop();
    }

    private static final net.minecraft.util.Identifier INTER_FONT = new net.minecraft.util.Identifier("glacierclient", "inter_medium");
    // Cache the wrapped Text per string — inter() is called for every label every frame, so building a
    // fresh Text+Style each call was pure per-frame garbage. Bounded to avoid unbounded growth.
    private static final java.util.Map<String, net.minecraft.text.Text> INTER_CACHE =
            new java.util.LinkedHashMap<>(256, 0.75f, true) {
                @Override protected boolean removeEldestEntry(java.util.Map.Entry<String, net.minecraft.text.Text> e) {
                    return size() > 512;
                }
            };
    /** Wraps text in the Inter Medium font for a consistent look across the mod menu (cached). */
    private static net.minecraft.text.Text inter(String s) {
        return INTER_CACHE.computeIfAbsent(s,
                k -> net.minecraft.text.Text.literal(k).setStyle(net.minecraft.text.Style.EMPTY.withFont(INTER_FONT)));
    }

    /** Clamped mod-menu scale (its own factor, independent of the vanilla GUI scale). */
    private float guiScale() { return Math.max(0.5f, Math.min(2.0f, GuiCustomization.get().guiScale)); }
    /** Map a screen-space X into panel-local space (inverse of the render scale about panel centre). */
    private double localX(double mx) { float cx = panelX + panelW / 2f; return cx + (mx - cx) / guiScale(); }
    private double localY(double my) { float cy = panelY + panelH / 2f; return cy + (my - cy) / guiScale(); }

    private void renderHeader(DrawContext ctx, int mouseX, int mouseY) {
        GuiTextures.rect(ctx, "underlined_base_bg", panelX, panelY, panelW, HEADER_H, GlacierTheme.BG_PANEL);

        // bear logo
        Icons.bearLogo(ctx, panelX + 26, panelY + HEADER_H / 2, 22, GlacierTheme.ACCENT);

        // centered title
        String title = "Glacier";
        ctx.drawTextWithShadow(textRenderer, inter(title),
                panelX + (panelW - textRenderer.getWidth(title)) / 2, panelY + HEADER_H / 2 - 4, GlacierTheme.TEXT);

        // close X (PNG icon, vector fallback)
        int xc = panelX + panelW - 24, yc = panelY + HEADER_H / 2;
        boolean xHov = within(mouseX, mouseY, xc - 10, yc - 10, 20, 20);
        int xCol = xHov ? GlacierTheme.RED : GlacierTheme.TEXT_DIM;
        if (!uiIcon(ctx, "close", xc, yc, 13, xCol)) drawX(ctx, xc, yc, 6, xCol);

        // GUI scale control:  −  xx%  +   (left of the close X)
        int ty = yc - 4;
        String pct = Math.round(guiScale() * 100) + "%";
        int plusX = panelX + panelW - 44;
        int labelX = plusX - 6 - textRenderer.getWidth(pct);
        int minusX = labelX - 12;
        boolean minusHov = within(mouseX, mouseY, minusX - 2, ty - 3, 10, 13);
        boolean plusHov = within(mouseX, mouseY, plusX - 2, ty - 3, 10, 13);
        ctx.drawTextWithShadow(textRenderer, "-", minusX, ty, minusHov ? GlacierTheme.ACCENT : GlacierTheme.TEXT_DIM);
        ctx.drawTextWithShadow(textRenderer, pct, labelX, ty, GlacierTheme.TEXT_DIM);
        ctx.drawTextWithShadow(textRenderer, "+", plusX, ty, plusHov ? GlacierTheme.ACCENT : GlacierTheme.TEXT_DIM);
    }

    /** Hit-test + apply a click on the header GUI-scale −/+ controls. Returns true if handled. */
    private boolean handleScaleClick(int x, int y) {
        int yc = panelY + HEADER_H / 2, ty = yc - 4;
        String pct = Math.round(guiScale() * 100) + "%";
        int plusX = panelX + panelW - 44;
        int labelX = plusX - 6 - textRenderer.getWidth(pct);
        int minusX = labelX - 12;
        if (within(x, y, minusX - 2, ty - 3, 10, 13)) { setGuiScale(guiScale() - 0.1f); return true; }
        if (within(x, y, plusX - 2, ty - 3, 10, 13)) { setGuiScale(guiScale() + 0.1f); return true; }
        return false;
    }

    private void setGuiScale(float s) {
        GuiCustomization.get().guiScale = Math.max(0.5f, Math.min(2.0f, Math.round(s * 10f) / 10f));
        GuiCustomization.get().save();
    }

    private static final String[] TAB_LABELS = {"Modules", "Editors", "Spotify"};

    private void renderTabs(DrawContext ctx, int mouseX, int mouseY) {
        int y = panelY + HEADER_H + 6;
        int h = TAB_H - 14;
        int x = panelX + PAD;
        Tab[] tabs = Tab.values();
        for (int i = 0; i < tabs.length; i++) {
            int w = textRenderer.getWidth(TAB_LABELS[i]) + 30;
            boolean sel = activeTab == tabs[i];
            boolean hov = within(mouseX, mouseY, x, y, w, h);
            int bg = sel ? GlacierTheme.ACCENT : (hov ? GlacierTheme.BG_ITEM_HOVER : GlacierTheme.BG_PANEL);
            String tabTex = sel ? "accent_bg" : "underlined_base_bg";
            if (GuiTextures.has(tabTex)) GuiTextures.nineSlice(ctx, tabTex, x, y, w, h);
            else RenderUtil.drawRoundedRect(ctx, x, y, w, h, GlacierTheme.RADIUS_SM, bg);
            int fg = sel ? GlacierTheme.TEXT : GlacierTheme.TEXT_DIM;
            int icx = x + 12, icy = y + h / 2;
            tabIcon(ctx, tabs[i], icx, icy, fg);
            ctx.drawTextWithShadow(textRenderer, inter(TAB_LABELS[i]), x + 22, y + h / 2 - 4, fg);
            x += w + 6;
        }

        int sx = x + 4;
        int sw = panelX + panelW - PAD - sx;
        if (sw >= 50) {
            searchX = sx; searchY = y; searchW = sw; searchH = h;
            renderTabSearch(ctx, mouseX, mouseY);
        } else {
            searchX = searchY = searchW = searchH = 0;
        }
    }

    private void renderTabSearch(DrawContext ctx, int mouseX, int mouseY) {
        int x = searchX, y = searchY, w = searchW, h = searchH;
        GuiTextures.rect(ctx, "secondary_bg", x, y, w, h, GlacierTheme.BG_ITEM);
        RenderUtil.drawOutline(ctx, x, y, w, h, 1,
                searchFocused ? GlacierTheme.ACCENT
                        : (within(mouseX, mouseY, x, y, w, h) ? GlacierTheme.ACCENT_GLOW : GlacierTheme.BG_ITEM_HOVER));
        int lx = x + 9, ly = y + h / 2 - 1;
        Icons.ring(ctx, lx, ly, 4, 1, GlacierTheme.TEXT_DIM);
        ctx.fill(lx + 3, ly + 3, lx + 6, ly + 5, GlacierTheme.TEXT_DIM);

        int textX = x + 20, textY = y + (h - 8) / 2;
        if (searchQuery.isEmpty() && !searchFocused) {
            ctx.drawTextWithShadow(textRenderer, "Search modules…", textX, textY, GlacierTheme.TEXT_DIM);
        } else {
            String shown = trim(searchQuery, w - 40);
            ctx.drawTextWithShadow(textRenderer, shown, textX, textY, GlacierTheme.TEXT);
            if (searchFocused && (System.currentTimeMillis() / 500) % 2 == 0) {
                int cx = textX + textRenderer.getWidth(shown);
                ctx.fill(cx + 1, y + 3, cx + 2, y + h - 3, GlacierTheme.ACCENT);
            }
        }
        if (!searchQuery.isEmpty()) {
            int xc = x + w - 10, yc = y + h / 2;
            boolean xh = within(mouseX, mouseY, xc - 7, yc - 7, 14, 14);
            drawX(ctx, xc, yc, 4, xh ? GlacierTheme.RED : GlacierTheme.TEXT_DIM);
        }
    }

    private void tabIcon(DrawContext ctx, Tab t, int cx, int cy, int color) {
        String tex = switch (t) {
            case MODULES -> "tab_modules";
            case EDITORS -> "tab_editors";
            case SPOTIFY -> "tab_spotify";
        };
        if (!uiIcon(ctx, tex, cx, cy, 13, color)) {
            // vector fallback
            switch (t) {
                case MODULES  -> Icons.draw(ctx, textRenderer, "chunk map", "HUD", cx, cy, 12, color);
                case EDITORS  -> Icons.gear(ctx, cx, cy, 5, color);
                case SPOTIFY  -> Icons.draw(ctx, textRenderer, "music", "ENGINE", cx, cy, 12, color);
            }
        }
    }

    // ---- UI icon: now a Font Awesome glyph (no PNGs). Colour's alpha defaults to opaque. ----
    private boolean uiIcon(DrawContext ctx, String name, int cx, int cy, int size, int argb) {
        if (((argb >>> 24) & 0xFF) == 0) argb |= 0xFF000000;
        return FontAwesome.draw(ctx, textRenderer, name, cx, cy, size, argb);
    }

    /** Top-level module folder (advanced/expanded/engine/hud/render/pvp/performance/qol). */
    private static String folderOf(GlacierMod m) {
        String p = m.getClass().getPackageName();
        int i = p.indexOf(".modules.");
        if (i < 0) return "other";
        String rest = p.substring(i + ".modules.".length());
        int dot = rest.indexOf('.');
        return dot < 0 ? rest : rest.substring(0, dot);
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

    private int modulesGridY() { return contentY() + SUBTAB_H; }

    private void renderModulesTab(DrawContext ctx, int mouseX, int mouseY) {
        // Category (folder) sub-tabs sit above the grid; cards are toggle/config only here.
        renderCategoryTabs(ctx, mouseX, mouseY);
        renderCardGridAt(ctx, getFilteredModules(), contentX(), modulesGridY(), mouseX, mouseY, false);
    }

    /** Row of folder sub-tabs (All + one per top-level module folder), each with its own icon. */
    private void renderCategoryTabs(DrawContext ctx, int mouseX, int mouseY) {
        catTabHit.clear();
        int x = contentX();
        int y = contentY() + 1;
        int h = SUBTAB_H - 7;
        for (int i = 0; i < CAT_KEYS.length; i++) {
            String key = CAT_KEYS[i];
            boolean sel = folderFilter.equals(key);
            int iconW = 12;
            int labelW = sel ? (4 + textRenderer.getWidth(CAT_LABELS[i])) : 0;
            int w = 9 + iconW + labelW + 7;
            boolean hov = within(mouseX, mouseY, x, y, w, h);
            if (sel) {
                if (GuiTextures.has("accent_bg")) GuiTextures.nineSlice(ctx, "accent_bg", x, y, w, h);
                else RenderUtil.drawRoundedRect(ctx, x, y, w, h, 7, GlacierTheme.ACCENT);
            } else {
                RenderUtil.drawRoundedRect(ctx, x, y, w, h, 7, hov ? GlacierTheme.BG_ITEM_HOVER : GlacierTheme.BG_ITEM);
            }
            int col = sel ? GlacierTheme.TEXT : (hov ? GlacierTheme.TEXT : GlacierTheme.TEXT_DIM);
            int icx = x + 9 + iconW / 2, icy = y + h / 2;
            if (!uiIcon(ctx, "cat_" + key, icx, icy, iconW, col))
                Icons.draw(ctx, textRenderer, key, "HUD", icx, icy, iconW, col);
            if (sel)
                ctx.drawTextWithShadow(textRenderer, inter(CAT_LABELS[i]), icx + iconW / 2 + 4, y + h / 2 - 4, col);
            catTabHit.put(new int[]{x, y, w, h}, key);
            x += w + 4;
        }
    }

    private static final int SPOTIFY_CARD_H = 56;

    private void renderSpotifyTab(DrawContext ctx, int mouseX, int mouseY) {
        SpotifyMediaBridge bridge = modules().getModule(SpotifyMediaBridge.class);
        int x = contentX(), y = contentY(), w = contentW();
        if (bridge == null) {
            ctx.drawTextWithShadow(textRenderer, "Spotify Media Bridge unavailable.", x, y, GlacierTheme.TEXT_DIM);
            return;
        }

        int ch = SPOTIFY_CARD_H;
        if (GuiTextures.has("modules_base_bg")) GuiTextures.nineSlice(ctx, "modules_base_bg", x, y, w, ch);
        else RenderUtil.drawRoundedRect(ctx, x, y, w, ch, GlacierTheme.RADIUS_SM, GlacierTheme.BG_ITEM);

        int art = ch - 16;
        ctx.fill(x + 8, y + 8, x + 8 + art, y + 8 + art, 0x663C8DFF);
        Icons.draw(ctx, textRenderer, "music", "ENGINE", x + 8 + art / 2, y + 8 + art / 2, art - 8, GlacierTheme.ACCENT);

        int tx = x + art + 16;
        String src = String.valueOf(bridge.<String>getSetting("Source").getValue());
        ctx.drawTextWithShadow(textRenderer, src + " Media", tx, y + 10, GlacierTheme.ACCENT);
        ctx.drawTextWithShadow(textRenderer, "Bridge idle — no track", tx, y + 22, GlacierTheme.TEXT_DIM);

        int barY = y + ch - 14, barRight = x + w - 12;
        ctx.fill(tx, barY, barRight, barY + 3, 0x44FFFFFF);
        ctx.fill(tx, barY, tx + (barRight - tx) / 4, barY + 3, GlacierTheme.ACCENT);

        int ey = y + ch + 8;
        ctx.drawTextWithShadow(textRenderer, "Enabled", x, ey + 2, GlacierTheme.TEXT);
        renderTogglePill(ctx, x + 70, ey, bridge.isEnabled());
        ctx.fill(x, ey + 18, x + w, ey + 19, GlacierTheme.ACCENT_GLOW);

        int bottom = panelY + panelH - PAD;
        ctx.enableScissor(panelX, ey + 20, panelX + panelW - PAD, bottom);
        renderSettingsList(ctx, bridge.getSettings(), x, ey + 24, w - 6, mouseX, mouseY);
        ctx.disableScissor();
    }

    private int spotifySettingsY() { return contentY() + SPOTIFY_CARD_H + 8 + 24; }

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
        if (!FontAwesome.drawName(ctx, textRenderer, mod.getName(), x + w / 2, y + 34, 24, iconColor)) {
            Icons.draw(ctx, textRenderer, mod.getName(), mod.getCategory().name(), x + w / 2, y + 36, 30, iconColor);
        }

        // name (centered, trimmed)
        String name = trim(mod.getName(), w - 12);
        ctx.drawTextWithShadow(textRenderer, inter(name), x + (w - textRenderer.getWidth(name)) / 2, y + CARD_H - 18,
                mod.isEnabled() ? GlacierTheme.TEXT : GlacierTheme.TEXT_DIM);

        // gear
        if (!mod.getSettings().isEmpty()) {
            int gx = x + w - 18, gy = y + 16;
            boolean gHov = !floating && within(mouseX, mouseY, gx - 9, gy - 9, 18, 18);
            FontAwesome.draw(ctx, textRenderer, "ui_gear", gx, gy, 10, gHov ? GlacierTheme.ACCENT : GlacierTheme.TEXT_DIM);
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
                GuiTextures.rect(ctx, "modules_base_bg", x, y, cw, CARD_H, hov ? 0xFF2A2E33 : 0xFF202327);
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
            GuiTextures.rect(ctx, "modules_base_bg", cx, cy, cw, CARD_H, hov ? 0xFF2A2E33 : 0xFF202327);
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
        GuiTextures.rect(ctx, "secondary_bg", px, py, pw, ph, 0xFF17191C);
        if (!GuiTextures.has("secondary_bg")) RenderUtil.drawOutline(ctx, px, py, pw, ph, 1, GlacierTheme.ACCENT_GLOW);
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
            GuiTextures.rect(ctx, "modules_base_bg", cx, cy, cw, CARD_H, hov ? 0xFF2A2E33 : 0xFF202327);
            Icons.draw(ctx, textRenderer, "person", "COSMETICS", cx + cw / 2, cy + 34, 28, GlacierTheme.TEXT);
            String nm = EMOTES[i];
            ctx.drawTextWithShadow(textRenderer, nm, cx + (cw - textRenderer.getWidth(nm)) / 2, cy + CARD_H - 16, GlacierTheme.TEXT);
        }
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
        editingString = null;
        colorPicker.close();
    }

    private int popupX() { return panelX + 6; }
    private int popupY() { return panelY + HEADER_H + 4; }
    private int POPUP_W = 270, POPUP_H = 250;

    /** Dedicated, full-panel settings page for a single module (replaces the old floating modal). */
    private void renderSettingsPage(DrawContext ctx, int mouseX, int mouseY) {
        int x = popupX(), y = popupY();
        if (GuiTextures.has("underlined_base_bg")) {
            GuiTextures.nineSlice(ctx, "underlined_base_bg", x, y, POPUP_W, POPUP_H);
        } else {
            RenderUtil.drawRoundedRect(ctx, x, y, POPUP_W, POPUP_H, GlacierTheme.RADIUS_MD, GlacierTheme.BG_PANEL);
            RenderUtil.drawOutline(ctx, x, y, POPUP_W, POPUP_H, 1, GlacierTheme.ACCENT_GLOW);
        }

        // Back button (top-left) returns to the module grid.
        boolean backHov = within(mouseX, mouseY, x + 8, y + 8, 52, 14);
        ctx.drawTextWithShadow(textRenderer, inter("< Back"), x + 10, y + 10, backHov ? GlacierTheme.ACCENT : GlacierTheme.TEXT_DIM);

        ctx.drawTextWithShadow(textRenderer, inter(popupTitle), x + 70, y + 10, GlacierTheme.ACCENT);
        ctx.drawTextWithShadow(textRenderer, inter(trim(popupDesc, POPUP_W - 90)), x + 70, y + 22, GlacierTheme.TEXT_DIM);

        if (popupToggle != null && popupEnabled != null) {
            ctx.drawTextWithShadow(textRenderer, inter("Enabled"), x + 12, y + 40, GlacierTheme.TEXT);
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
        GuiTextures.rect(ctx, awaitingBind ? "accent_bg" : "underlined_secondary_bg", x + 12, by, bw, 14, bg);
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
        if (sel) GuiTextures.rect(ctx, "accent_bg", x, y, w, 14, GlacierTheme.ACCENT);
        else GuiTextures.rect(ctx, "underlined_secondary_bg", x, y, w, 14, hov ? GlacierTheme.BG_ITEM_HOVER : GlacierTheme.BG_ITEM);
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
        ctx.drawTextWithShadow(textRenderer, inter(setting.getName()), x, y + 2, GlacierTheme.TEXT);
        if (setting instanceof BooleanSetting bs) {
            renderTogglePill(ctx, x + w - 28, y, bs.getValue());
        } else if (setting instanceof NumberSetting ns) {
            renderSlider(ctx, ns, x, y + 14, w, mouseX, mouseY);
        } else if (setting instanceof ModeSetting ms) {
            int btnX = x + w - 84;
            boolean hov = within(mouseX, mouseY, btnX, y, 84, 16);
            RenderUtil.drawRoundedRect(ctx, btnX, y, 84, 16, 8, hov ? GlacierTheme.ACCENT_BG : 0xCC0B1220);
            ctx.drawTextWithShadow(textRenderer, inter(trim(ms.getValue(), 76)), btnX + 6, y + 4, GlacierTheme.ACCENT);
        } else if (setting instanceof StringSetting ss) {
            boolean focused = editingString == ss;
            RenderUtil.drawRoundedRect(ctx, x, y + 12, w, 14, 7, focused ? GlacierTheme.ACCENT_BG : 0xCC0B1220);
            if (focused) RenderUtil.drawRoundedOutline(ctx, x, y + 12, w, 14, 7, 1, GlacierTheme.ACCENT);
            String shown = trim(ss.getValue() == null ? "" : ss.getValue(), w - 12);
            ctx.drawTextWithShadow(textRenderer, inter(shown), x + 5, y + 15, GlacierTheme.TEXT);
            if (focused && (System.currentTimeMillis() / 500) % 2 == 0) {
                int cx = x + 4 + textRenderer.getWidth(shown);
                ctx.fill(cx + 1, y + 14, cx + 2, y + 24, GlacierTheme.ACCENT);
            }
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
        ctx.drawTextWithShadow(textRenderer, inter(val), x + w - textRenderer.getWidth(val), y - 12, GlacierTheme.ACCENT);
        if (draggingSlider == ns) { sliderBarX = x; sliderBarW = w; }
    }

    private void renderTogglePill(DrawContext ctx, int x, int y, boolean on) {
        int pw = 24, ph = 12;
        GuiTextures.rect(ctx, on ? "accent_bg" : "secondary_bg", x, y, pw, ph, on ? GlacierTheme.ACCENT : GlacierTheme.BG_ITEM_HOVER);
        int k = ph - 4;
        int kx = on ? x + pw - k - 2 : x + 2;
        Icons.disc(ctx, kx + k / 2, y + 2 + k / 2, (k + 1) / 2, GlacierTheme.TEXT);
    }

    private void renderDropdown(DrawContext ctx, ModeSetting ms, int x, int y, int mouseX, int mouseY) {
        int itemH = 16, w = 84;
        int h = ms.getModes().size() * itemH;
        GuiTextures.rect(ctx, "secondary_bg", x, y, w, h, GlacierTheme.BG);
        RenderUtil.drawOutline(ctx, x, y, w, h, 1, GlacierTheme.ACCENT);
        int iy = y;
        for (String mode : ms.getModes()) {
            boolean hov = within(mouseX, mouseY, x, iy, w, itemH);
            if (hov) RenderUtil.drawRoundedRect(ctx, x + 1, iy + 1, w - 2, itemH - 2, 5, GlacierTheme.BG_ITEM_HOVER);
            ctx.drawTextWithShadow(textRenderer, trim(mode, w - 8), x + 4, iy + 4, mode.equals(ms.getValue()) ? GlacierTheme.ACCENT : GlacierTheme.TEXT);
            iy += itemH;
        }
    }

    // =======================================================================
    // INPUT
    // =======================================================================

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        // Convert to panel-local space so clicks line up with the scaled render.
        mx = localX(mx); my = localY(my);
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

        // settings page
        if (popupOpen) {
            int px = popupX(), py = popupY();
            if (within(x, y, px + 8, py + 8, 52, 14)) { closePopup(); return true; }            // Back
            if (within(x, y, px + POPUP_W - 24, py + 8, 24, 24)) { closePopup(); return true; } // X
            if (!within(x, y, px, py, POPUP_W, POPUP_H)) { closePopup(); return true; }          // outside (header)
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
            editingString = null; // clicking anywhere in the list defocuses unless a field is hit
            if (handleSettingsClick(popupSettings, px + 12, py + 62 - popupScroll, POPUP_W - 24, x, y)) return true;
            return true;
        }

        // close X
        if (within(x, y, panelX + panelW - 34, panelY + HEADER_H / 2 - 10, 24, 20)) { close(); return true; }

        // GUI scale −/+ controls in the header
        if (handleScaleClick(x, y)) return true;

        // tabs
        int tx = panelX + PAD, ty = panelY + HEADER_H + 6, th = TAB_H - 14;
        Tab[] tabs = Tab.values();
        for (int i = 0; i < tabs.length; i++) {
            int tw = textRenderer.getWidth(TAB_LABELS[i]) + 30;
            if (within(x, y, tx, ty, tw, th)) {
                activeTab = tabs[i];
                gridScroll = 0;
                activeEditor = null;
                searchFocused = false;
                return true;
            }
            tx += tw + 6;
        }

        // search bar (tab row, beside the Spotify tab)
        if (searchW > 0 && within(x, y, searchX, searchY, searchW, searchH)) {
            if (!searchQuery.isEmpty() && within(x, y, searchX + searchW - 18, searchY, 18, searchH)) {
                searchQuery = ""; gridScroll = 0;
            }
            searchFocused = true;
            return true;
        }
        searchFocused = false;

        switch (activeTab) {
            case MODULES -> {
                // category (folder) sub-tab clicks
                for (var e : catTabHit.entrySet()) {
                    int[] b = e.getKey();
                    if (within(x, y, b[0], b[1], b[2], b[3])) {
                        folderFilter = e.getValue();
                        gridScroll = 0;
                        return true;
                    }
                }
                return clickModuleGrid(getFilteredModules(), contentX(), modulesGridY(), x, y, button, false);
            }
            case EDITORS -> { return clickEditors(x, y); }
            case SPOTIFY -> { return clickSpotifyTab(x, y); }
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

    private boolean clickSpotifyTab(int x, int y) {
        SpotifyMediaBridge bridge = modules().getModule(SpotifyMediaBridge.class);
        if (bridge == null) return false;
        int cx = contentX(), w = contentW();
        int ey = contentY() + SPOTIFY_CARD_H + 8;
        if (within(x, y, cx + 70, ey, 24, 12)) { bridge.toggle(); return true; }
        return handleSettingsClick(bridge.getSettings(), cx, spotifySettingsY(), w - 6, x, y);
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
            } else if (s instanceof StringSetting ss) {
                if (within(mx, my, x, sy + 12, w, 14)) { editingString = ss; return true; }
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
        mx = localX(mx); my = localY(my);
        dx /= guiScale(); dy /= guiScale();
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
        mx = localX(mx); my = localY(my);
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
        int gx = contentX(), gy = modulesGridY(), cw = cardWidth();
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
        // text editing of a focused string setting
        if (editingString != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                editingString = null; return true;
            }
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                String v = editingString.getValue();
                if (v != null && !v.isEmpty()) editingString.setValue(v.substring(0, v.length() - 1));
                return true;
            }
            return true; // swallow other keys while editing
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (colorPicker.isOpen()) { colorPicker.close(); return true; }
            if (openDropdown != null) { openDropdown = null; return true; }
            if (popupOpen) { closePopup(); return true; }
            if (activeEditor != null) { activeEditor = null; return true; }
            if (searchFocused) { searchFocused = false; return true; }
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
        if (editingString != null && chr >= 32 && chr != 127) {
            String v = editingString.getValue() == null ? "" : editingString.getValue();
            editingString.setValue(v + chr);
            return true;
        }
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
        String q = searchQuery.toLowerCase(Locale.ROOT).trim();
        boolean allFolders = folderFilter.equals("all");
        if (q.isEmpty() && allFolders) return all;
        // Recompute only when the filter inputs change — this method is called every frame from render.
        String key = q + " " + folderFilter + " " + all.size();
        if (key.equals(lastFilterQuery)) return filteredModules;
        lastFilterQuery = key;
        filteredModules.clear();
        for (GlacierMod m : all) {
            if (!allFolders && !folderOf(m).equals(folderFilter)) continue;
            if (!q.isEmpty() && !matchesSearch(m, q)) continue;
            filteredModules.add(m);
        }
        return filteredModules;
    }

    private boolean matchesSearch(GlacierMod mod, String query) {
        if (mod.getName().toLowerCase(Locale.ROOT).contains(query)) return true;
        if (mod.getDescription().toLowerCase(Locale.ROOT).contains(query)) return true;
        if (mod.getCategory().name().toLowerCase(Locale.ROOT).contains(query)) return true;
        for (Setting<?> setting : mod.getSettings()) {
            if (setting.getName().toLowerCase(Locale.ROOT).contains(query)) return true;
            if (setting.getDescription().toLowerCase(Locale.ROOT).contains(query)) return true;
        }
        return false;
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
        String cardTex = GuiTextures.has("modules_underlined_base_bg") ? "modules_underlined_base_bg" : "modules_base_bg";
        if (GuiTextures.has(cardTex)) {
            GuiTextures.nineSlice(ctx, cardTex, x, y, w, h);
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
        // Return to the screen we were opened from (e.g. the Glacier title screen) instead of letting
        // vanilla recreate its own TitleScreen — which is what caused the start screen to revert.
        if (parent != null) {
            client.setScreen(parent);
        } else {
            super.close();
        }
    }

    @Override public boolean shouldPause() { return false; }
    @Override public boolean shouldCloseOnEsc() { return true; }
}
