package net.glacierclient.gui.screens;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.theme.GlacierTheme;
import net.glacierclient.core.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ClickGUIScreen extends Screen {

    private static final int SIDEBAR_WIDTH = 120;
    private static final int SETTINGS_WIDTH = 180;
    private static final int MODULE_HEIGHT = 28;
    private static final int SEARCH_HEIGHT = 32;
    private static final int CATEGORY_TAB_HEIGHT = 32;

    private Category selectedCategory = Category.HUD;
    private GlacierMod hoveredModule = null;
    private GlacierMod selectedModuleForSettings = null;
    private String searchQuery = "";
    private boolean searchFocused = false;
    private int scrollOffset = 0;
    private float hoverAnim = 0;

    public ClickGUIScreen() {
        super(Text.literal("Glacier Client"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Dark overlay background
        context.fill(0, 0, width, height, 0xC8000000);

        // Main window centered
        int windowX = width / 2 - (width - 60) / 2;
        int windowY = height / 2 - (height - 60) / 2;
        int windowW = width - 60;
        int windowH = height - 60;

        // Window background
        context.fill(windowX, windowY, windowX + windowW, windowY + windowH, GlacierTheme.BG);
        RenderUtil.drawOutline(context, windowX, windowY, windowW, windowH, 1, GlacierTheme.ACCENT_BG);

        // Title bar
        context.fill(windowX, windowY, windowX + windowW, windowY + 36, GlacierTheme.BG_PANEL);
        context.drawText(textRenderer, "❄ Glacier Client", windowX + 12, windowY + 12, GlacierTheme.ACCENT, false);

        // Search bar
        int searchX = windowX + windowW / 2 - 80;
        context.fill(searchX, windowY + 8, searchX + 160, windowY + 28, GlacierTheme.BG_ITEM);
        RenderUtil.drawOutline(context, searchX, windowY + 8, 160, 20, 1, searchFocused ? GlacierTheme.ACCENT : GlacierTheme.BG_ITEM_HOVER);
        String displaySearch = searchQuery.isEmpty() && !searchFocused ? "Search modules..." : searchQuery;
        int searchColor = searchQuery.isEmpty() && !searchFocused ? GlacierTheme.TEXT_DIM : GlacierTheme.TEXT;
        context.drawText(textRenderer, displaySearch, searchX + 6, windowY + 14, searchColor, false);

        // Sidebar (categories)
        int sideX = windowX;
        int sideY = windowY + 36;
        context.fill(sideX, sideY, sideX + SIDEBAR_WIDTH, windowY + windowH, GlacierTheme.BG_PANEL);

        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            Category cat = categories[i];
            int tabY = sideY + i * CATEGORY_TAB_HEIGHT;
            boolean isSelected = cat == selectedCategory;
            int tabBg = isSelected ? GlacierTheme.ACCENT_BG : (isMouseOver(mouseX, mouseY, sideX, tabY, SIDEBAR_WIDTH, CATEGORY_TAB_HEIGHT) ? GlacierTheme.BG_ITEM_HOVER : 0);
            if (tabBg != 0) context.fill(sideX, tabY, sideX + SIDEBAR_WIDTH, tabY + CATEGORY_TAB_HEIGHT, tabBg);
            if (isSelected) context.fill(sideX, tabY, sideX + 3, tabY + CATEGORY_TAB_HEIGHT, GlacierTheme.ACCENT);
            int textColor = isSelected ? GlacierTheme.ACCENT : GlacierTheme.TEXT_DIM;
            context.drawText(textRenderer, cat.displayName, sideX + 12, tabY + 10, textColor, false);
            int count = GlacierClient.getInstance().getModuleManager().getModulesByCategory(cat).size();
            context.drawText(textRenderer, String.valueOf(count), sideX + SIDEBAR_WIDTH - 20, tabY + 10, GlacierTheme.TEXT_DIM, false);
        }

        // Module list area
        int listX = windowX + SIDEBAR_WIDTH;
        int listW = windowW - SIDEBAR_WIDTH - (selectedModuleForSettings != null ? SETTINGS_WIDTH : 0);
        int listY = sideY;
        int listH = windowH - 36;
        context.fill(listX, listY, listX + listW, listY + listH, GlacierTheme.BG);

        List<GlacierMod> modules = getFilteredModules();
        int modY = listY + 8 - scrollOffset;
        hoveredModule = null;
        for (GlacierMod mod : modules) {
            if (modY + MODULE_HEIGHT < listY || modY > listY + listH) {
                modY += MODULE_HEIGHT + 4;
                continue;
            }
            boolean hovered = isMouseOver(mouseX, mouseY, listX + 4, modY, listW - 8, MODULE_HEIGHT);
            boolean enabled = mod.isEnabled();
            if (hovered) {
                hoveredModule = mod;
                context.fill(listX + 4, modY, listX + listW - 4, modY + MODULE_HEIGHT, GlacierTheme.BG_ITEM_HOVER);
            } else {
                context.fill(listX + 4, modY, listX + listW - 4, modY + MODULE_HEIGHT, GlacierTheme.BG_ITEM);
            }
            RenderUtil.drawOutline(context, listX + 4, modY, listW - 8, MODULE_HEIGHT, 1, enabled ? GlacierTheme.ACCENT_BG : 0x22FFFFFF);
            if (enabled) context.fill(listX + 4, modY, listX + 7, modY + MODULE_HEIGHT, GlacierTheme.ACCENT);
            context.drawText(textRenderer, mod.getName(), listX + 12, modY + 7, enabled ? GlacierTheme.TEXT : GlacierTheme.TEXT_DIM, false);
            String toggleText = enabled ? "ON" : "OFF";
            int toggleColor = enabled ? GlacierTheme.GREEN : GlacierTheme.RED;
            context.drawText(textRenderer, toggleText, listX + listW - 28, modY + 7, toggleColor, false);
            modY += MODULE_HEIGHT + 4;
        }

        // Settings panel
        if (selectedModuleForSettings != null) {
            int settX = windowX + windowW - SETTINGS_WIDTH;
            context.fill(settX, sideY, settX + SETTINGS_WIDTH, windowY + windowH, GlacierTheme.BG_PANEL);
            context.fill(settX, sideY, settX + 1, windowY + windowH, GlacierTheme.ACCENT_BG);
            context.drawText(textRenderer, selectedModuleForSettings.getName(), settX + 8, sideY + 8, GlacierTheme.ACCENT, false);
            context.drawText(textRenderer, selectedModuleForSettings.getDescription(), settX + 8, sideY + 20, GlacierTheme.TEXT_DIM, false);

            int sy = sideY + 36;
            for (Setting<?> setting : selectedModuleForSettings.getSettings()) {
                context.drawText(textRenderer, setting.getName(), settX + 8, sy, GlacierTheme.TEXT_DIM, false);
                sy += 12;
                if (setting instanceof BooleanSetting bs) {
                    boolean val = bs.getValue();
                    int toggleBg = val ? GlacierTheme.ACCENT : GlacierTheme.BG_ITEM;
                    context.fill(settX + 8, sy, settX + 44, sy + 14, toggleBg);
                    context.fill(val ? settX + 30 : settX + 10, sy + 2, val ? settX + 42 : settX + 22, sy + 12, GlacierTheme.TEXT);
                } else if (setting instanceof NumberSetting ns) {
                    float pct = ns.getPercent();
                    context.fill(settX + 8, sy + 3, settX + SETTINGS_WIDTH - 8, sy + 9, GlacierTheme.BG_ITEM);
                    context.fill(settX + 8, sy + 3, settX + 8 + (int)((SETTINGS_WIDTH - 16) * pct), sy + 9, GlacierTheme.ACCENT);
                    String valStr = String.format("%.1f", ns.getValue());
                    context.drawText(textRenderer, valStr, settX + SETTINGS_WIDTH / 2 - 8, sy - 1, GlacierTheme.TEXT, false);
                } else if (setting instanceof ModeSetting ms) {
                    context.fill(settX + 8, sy, settX + SETTINGS_WIDTH - 8, sy + 14, GlacierTheme.BG_ITEM);
                    context.drawText(textRenderer, "< " + ms.getValue() + " >", settX + 12, sy + 3, GlacierTheme.TEXT, false);
                } else if (setting instanceof StringSetting ss) {
                    context.fill(settX + 8, sy, settX + SETTINGS_WIDTH - 8, sy + 14, GlacierTheme.BG_ITEM);
                    context.drawText(textRenderer, ss.getValue(), settX + 12, sy + 3, GlacierTheme.TEXT, false);
                }
                sy += 20;
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int windowX = width / 2 - (width - 60) / 2;
        int windowY = height / 2 - (height - 60) / 2;
        int windowW = width - 60;
        int sideY = windowY + 36;

        // Category click
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            int tabY = sideY + i * CATEGORY_TAB_HEIGHT;
            if (isMouseOver((int)mouseX, (int)mouseY, windowX, tabY, SIDEBAR_WIDTH, CATEGORY_TAB_HEIGHT)) {
                selectedCategory = categories[i];
                scrollOffset = 0;
                return true;
            }
        }

        // Module click
        int listX = windowX + SIDEBAR_WIDTH;
        int listW = windowW - SIDEBAR_WIDTH - (selectedModuleForSettings != null ? SETTINGS_WIDTH : 0);
        int listY = sideY;
        List<GlacierMod> modules = getFilteredModules();
        int modY = listY + 8 - scrollOffset;
        for (GlacierMod mod : modules) {
            if (isMouseOver((int)mouseX, (int)mouseY, listX + 4, modY, listW - 8, MODULE_HEIGHT)) {
                if (button == 0) {
                    mod.toggle();
                } else if (button == 1) {
                    selectedModuleForSettings = selectedModuleForSettings == mod ? null : mod;
                }
                return true;
            }
            modY += MODULE_HEIGHT + 4;
        }

        // Search bar click
        int searchX = windowX + windowW / 2 - 80;
        if (isMouseOver((int)mouseX, (int)mouseY, searchX, windowY + 8, 160, 20)) {
            searchFocused = true;
            return true;
        }
        searchFocused = false;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        if (searchFocused) {
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE && !searchQuery.isEmpty()) {
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                scrollOffset = 0;
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchFocused) {
            searchQuery += chr;
            scrollOffset = 0;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset = Math.max(0, scrollOffset - (int)(verticalAmount * 12));
        return true;
    }

    private List<GlacierMod> getFilteredModules() {
        List<GlacierMod> base = searchQuery.isEmpty()
                ? GlacierClient.getInstance().getModuleManager().getModulesByCategory(selectedCategory)
                : GlacierClient.getInstance().getModuleManager().getModules();
        if (!searchQuery.isEmpty()) {
            List<GlacierMod> filtered = new ArrayList<>();
            for (GlacierMod m : base) {
                if (m.getName().toLowerCase().contains(searchQuery.toLowerCase())) filtered.add(m);
            }
            return filtered;
        }
        return base;
    }

    private boolean isMouseOver(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public boolean shouldPause() { return false; }
}
