package net.glacierclient.gui.screens;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.theme.GlacierTheme;
import net.glacierclient.core.util.AnimationUtil;
import net.glacierclient.core.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ClickGUIScreen extends Screen {

    // Layout constants
    private static final int SIDEBAR_W    = 120;
    private static final int MODULES_W    = 180;
    private static final int SETTINGS_W   = 200;
    private static final int GUI_H        = 340;
    private static final int HEADER_H     = 36;
    private static final int MODULE_H     = 28;
    private static final int CATEGORY_H   = 38;
    private static final int PADDING      = 10;
    private static final int SETTING_H    = 24;

    // Total GUI width = sidebar + modules + settings + 4 * padding
    private static final int GUI_W = SIDEBAR_W + MODULES_W + SETTINGS_W + PADDING * 4;

    // State
    private int guiX, guiY;
    private Category selectedCategory = Category.HUD;
    private GlacierMod hoveredModule;
    private GlacierMod selectedModule;
    private String searchQuery = "";
    private boolean searchFocused = false;
    private int moduleScrollOffset = 0;
    private int settingScrollOffset = 0;

    // Drag state for active number slider
    private NumberSetting draggingSlider;
    private int sliderStartX;
    private double sliderStartValue;
    private int sliderBarX, sliderBarW;

    // Mode dropdown
    private ModeSetting openDropdown;
    private int dropdownX, dropdownY;

    // Hover animation keys use: "cat_" + category.name(), "mod_" + mod.getName()
    // Toggle animation: "tog_" + mod.getName()

    // Profile dropdown
    private boolean profileDropdownOpen = false;
    private final List<String> profiles = List.of("Default", "PvP", "Recording", "Performance");
    private String activeProfile = "Default";

    public ClickGUIScreen() {
        super(Text.literal("Glacier Client"));
    }

    @Override
    protected void init() {
        guiX = (width - GUI_W) / 2;
        guiY = (height - GUI_H) / 2;
    }

    // -----------------------------------------------------------------------
    // RENDER
    // -----------------------------------------------------------------------

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Dim background
        ctx.fill(0, 0, width, height, 0x88000000);

        int x = guiX;
        int y = guiY;

        // Main background
        RenderUtil.drawRoundedRect(ctx, x, y, GUI_W, GUI_H, GlacierTheme.RADIUS_MD, GlacierTheme.BG);
        // Subtle outer glow / border
        RenderUtil.drawOutline(ctx, x, y, GUI_W, GUI_H, 1, GlacierTheme.ACCENT_GLOW);

        renderHeader(ctx, x, y, mouseX, mouseY);
        renderSidebar(ctx, x, y + HEADER_H, mouseX, mouseY);
        renderModulesPanel(ctx, x + SIDEBAR_W + PADDING, y + HEADER_H, mouseX, mouseY);
        renderSettingsPanel(ctx, x + SIDEBAR_W + MODULES_W + PADDING * 3, y + HEADER_H, mouseX, mouseY);

        // Draw open dropdown on top
        if (openDropdown != null) {
            renderDropdown(ctx, openDropdown, dropdownX, dropdownY, mouseX, mouseY);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void renderHeader(DrawContext ctx, int x, int y, int mouseX, int mouseY) {
        // Header background
        ctx.fill(x, y, x + GUI_W, y + HEADER_H, GlacierTheme.BG_PANEL);
        // Bottom separator
        ctx.fill(x, y + HEADER_H - 1, x + GUI_W, y + HEADER_H, GlacierTheme.ACCENT_GLOW);

        // Logo / title
        ctx.drawTextWithShadow(textRenderer, "Glacier", x + PADDING, y + 7, GlacierTheme.ACCENT);
        ctx.drawTextWithShadow(textRenderer, "Client", x + PADDING + textRenderer.getWidth("Glacier "), y + 7, GlacierTheme.TEXT);
        String version = "v" + GlacierClient.VERSION;
        ctx.drawTextWithShadow(textRenderer, version, x + PADDING, y + 18, GlacierTheme.TEXT_DIM);

        // Search bar
        int searchX = x + SIDEBAR_W + PADDING + 10;
        int searchY = y + 9;
        int searchW = MODULES_W - 20;
        boolean searchHov = mouseX >= searchX && mouseX <= searchX + searchW && mouseY >= searchY && mouseY <= searchY + 18;
        int searchBg = searchFocused ? GlacierTheme.ACCENT_BG : GlacierTheme.BG_ITEM;
        RenderUtil.drawRoundedRect(ctx, searchX, searchY, searchW, 18, GlacierTheme.RADIUS_SM, searchBg);
        if (searchFocused || searchHov) {
            RenderUtil.drawOutline(ctx, searchX, searchY, searchW, 18, 1, GlacierTheme.ACCENT);
        }
        String displayQuery = searchQuery.isEmpty() && !searchFocused ? "Search modules..." : searchQuery + (searchFocused ? "|" : "");
        int queryColor = searchQuery.isEmpty() && !searchFocused ? GlacierTheme.TEXT_DIM : GlacierTheme.TEXT;
        ctx.drawTextWithShadow(textRenderer, displayQuery, searchX + 5, searchY + 5, queryColor);

        // Profile quick-swap
        int profX = x + GUI_W - 110;
        int profY = y + 8;
        boolean profHov = mouseX >= profX && mouseX <= profX + 100 && mouseY >= profY && mouseY <= profY + 20;
        int profBg = profHov || profileDropdownOpen ? GlacierTheme.ACCENT_BG : GlacierTheme.BG_ITEM;
        RenderUtil.drawRoundedRect(ctx, profX, profY, 100, 20, GlacierTheme.RADIUS_SM, profBg);
        ctx.drawTextWithShadow(textRenderer, activeProfile + " v", profX + 6, profY + 6, GlacierTheme.TEXT_DIM);

        if (profileDropdownOpen) {
            int dy = profY + 22;
            for (String prof : profiles) {
                boolean hov = mouseX >= profX && mouseX <= profX + 100 && mouseY >= dy && mouseY <= dy + 18;
                ctx.fill(profX, dy, profX + 100, dy + 18, hov ? GlacierTheme.BG_ITEM_HOVER : GlacierTheme.BG_PANEL);
                ctx.drawTextWithShadow(textRenderer, prof, profX + 6, dy + 5,
                        prof.equals(activeProfile) ? GlacierTheme.ACCENT : GlacierTheme.TEXT);
                dy += 18;
            }
        }
    }

    private void renderSidebar(DrawContext ctx, int x, int y, int mouseX, int mouseY) {
        int panelH = GUI_H - HEADER_H;
        ctx.fill(x, y, x + SIDEBAR_W, y + panelH, GlacierTheme.BG_PANEL);

        int cy = y + 8;
        for (Category cat : Category.values()) {
            boolean selected = cat == selectedCategory;
            boolean hov = mouseX >= x && mouseX <= x + SIDEBAR_W && mouseY >= cy && mouseY <= cy + CATEGORY_H;
            double anim = AnimationUtil.animate("cat_" + cat.name(), (selected || hov) ? 1.0 : 0.0, 8.0);
            int bg = blendColor(GlacierTheme.BG_PANEL, GlacierTheme.BG_ITEM_HOVER, (float) anim);
            RenderUtil.drawRoundedRect(ctx, x + 6, cy, SIDEBAR_W - 12, CATEGORY_H - 4, GlacierTheme.RADIUS_SM, bg);
            if (selected) {
                ctx.fill(x + 6, cy, x + 9, cy + CATEGORY_H - 4, GlacierTheme.ACCENT);
            }
            int textColor = selected ? GlacierTheme.ACCENT : (hov ? GlacierTheme.TEXT : GlacierTheme.TEXT_DIM);
            ctx.drawTextWithShadow(textRenderer, cat.displayName, x + 14, cy + 8, textColor);
            // Module count badge
            long count = GlacierClient.getInstance().getModuleManager().getModulesByCategory(cat).stream()
                    .filter(GlacierMod::isEnabled).count();
            if (count > 0) {
                String badge = String.valueOf(count);
                int bw = textRenderer.getWidth(badge) + 6;
                int bx = x + SIDEBAR_W - bw - 8;
                int bcy = cy + (CATEGORY_H - 4) / 2 - 6;
                RenderUtil.drawRoundedRect(ctx, bx, bcy, bw, 12, 4, GlacierTheme.ACCENT_BG);
                ctx.drawTextWithShadow(textRenderer, badge, bx + 3, bcy + 2, GlacierTheme.ACCENT);
            }
            cy += CATEGORY_H;
        }
    }

    private void renderModulesPanel(DrawContext ctx, int x, int y, int mouseX, int mouseY) {
        int panelH = GUI_H - HEADER_H;
        ctx.fill(x, y, x + MODULES_W, y + panelH, GlacierTheme.BG);

        List<GlacierMod> mods = getFilteredModules();
        int visibleCount = (panelH - PADDING * 2) / MODULE_H;
        int maxScroll = Math.max(0, mods.size() - visibleCount);
        moduleScrollOffset = Math.max(0, Math.min(moduleScrollOffset, maxScroll));

        hoveredModule = null;
        int my = y + PADDING;
        for (int i = moduleScrollOffset; i < Math.min(mods.size(), moduleScrollOffset + visibleCount + 1); i++) {
            if (my + MODULE_H > y + panelH) break;
            GlacierMod mod = mods.get(i);
            boolean hov = mouseX >= x && mouseX <= x + MODULES_W && mouseY >= my && mouseY <= my + MODULE_H;
            boolean sel = mod == selectedModule;
            if (hov) hoveredModule = mod;

            double anim = AnimationUtil.animate("mod_" + mod.getName(), (hov || sel) ? 1.0 : 0.0, 8.0);
            int bg = blendColor(GlacierTheme.BG, GlacierTheme.BG_ITEM_HOVER, (float) anim);
            ctx.fill(x, my, x + MODULES_W, my + MODULE_H, bg);

            // Enabled indicator bar on left
            double togAnim = AnimationUtil.animate("tog_" + mod.getName(), mod.isEnabled() ? 1.0 : 0.0, 10.0);
            if (togAnim > 0.01) {
                int barH = (int)(MODULE_H * togAnim);
                int barY = my + (MODULE_H - barH) / 2;
                ctx.fill(x, barY, x + 3, barY + barH, blendColor(0x007289DA, GlacierTheme.ACCENT, (float) togAnim));
            }

            int nameColor = mod.isEnabled() ? GlacierTheme.TEXT : GlacierTheme.TEXT_DIM;
            if (sel) nameColor = GlacierTheme.ACCENT;
            ctx.drawTextWithShadow(textRenderer, mod.getName(), x + 10, my + (MODULE_H - 8) / 2, nameColor);

            // Toggle pill on right
            renderTogglePill(ctx, x + MODULES_W - 34, my + MODULE_H / 2 - 5, mod.isEnabled());

            my += MODULE_H;
        }

        // Scroll indicator
        if (mods.size() > visibleCount) {
            float scrollFrac = (float) moduleScrollOffset / maxScroll;
            int trackH = panelH - PADDING * 2;
            int thumbH = Math.max(20, trackH * visibleCount / mods.size());
            int thumbY = y + PADDING + (int)((trackH - thumbH) * scrollFrac);
            ctx.fill(x + MODULES_W - 3, y + PADDING, x + MODULES_W - 1, y + PADDING + trackH, GlacierTheme.BG_ITEM);
            ctx.fill(x + MODULES_W - 3, thumbY, x + MODULES_W - 1, thumbY + thumbH, GlacierTheme.ACCENT_GLOW);
        }
    }

    private void renderSettingsPanel(DrawContext ctx, int x, int y, int mouseX, int mouseY) {
        int panelH = GUI_H - HEADER_H;
        ctx.fill(x, y, x + SETTINGS_W, y + panelH, GlacierTheme.BG_PANEL);

        GlacierMod mod = selectedModule != null ? selectedModule : hoveredModule;
        if (mod == null) {
            String hint = "Select a module";
            ctx.drawTextWithShadow(textRenderer, hint,
                    x + (SETTINGS_W - textRenderer.getWidth(hint)) / 2,
                    y + panelH / 2 - 4,
                    GlacierTheme.TEXT_DIM);
            return;
        }

        // Module title
        ctx.drawTextWithShadow(textRenderer, mod.getName(), x + PADDING, y + 10, GlacierTheme.ACCENT);
        ctx.drawTextWithShadow(textRenderer, mod.getDescription(), x + PADDING, y + 22, GlacierTheme.TEXT_DIM);
        ctx.fill(x, y + 34, x + SETTINGS_W, y + 35, GlacierTheme.ACCENT_GLOW);

        if (mod.getSettings().isEmpty()) {
            ctx.drawTextWithShadow(textRenderer, "No settings", x + PADDING, y + 44, GlacierTheme.TEXT_DIM);
            return;
        }

        List<Setting<?>> settings = mod.getSettings();
        int visCount = (panelH - 40) / SETTING_H;
        int maxScroll = Math.max(0, settings.size() - visCount);
        settingScrollOffset = Math.max(0, Math.min(settingScrollOffset, maxScroll));

        int sy = y + 40;
        for (int i = settingScrollOffset; i < Math.min(settings.size(), settingScrollOffset + visCount + 1); i++) {
            if (sy + SETTING_H > y + panelH) break;
            renderSetting(ctx, settings.get(i), x, sy, SETTINGS_W, mouseX, mouseY);
            sy += SETTING_H + 2;
        }
    }

    private void renderSetting(DrawContext ctx, Setting<?> setting, int x, int y, int w, int mouseX, int mouseY) {
        boolean hov = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + SETTING_H;
        if (hov) ctx.fill(x, y, x + w, y + SETTING_H, GlacierTheme.BG_ITEM);

        ctx.drawTextWithShadow(textRenderer, setting.getName(), x + PADDING, y + 4, GlacierTheme.TEXT_DIM);

        if (setting instanceof BooleanSetting bs) {
            renderTogglePill(ctx, x + w - 34, y + SETTING_H / 2 - 5, bs.getValue());
        } else if (setting instanceof NumberSetting ns) {
            renderSlider(ctx, ns, x + PADDING, y + 14, w - PADDING * 2, mouseX, mouseY);
        } else if (setting instanceof ModeSetting ms) {
            int btnX = x + w - 80;
            boolean btnHov = mouseX >= btnX && mouseX <= btnX + 70 && mouseY >= y + 2 && mouseY <= y + SETTING_H - 2;
            RenderUtil.drawRoundedRect(ctx, btnX, y + 2, 70, SETTING_H - 4, GlacierTheme.RADIUS_SM,
                    btnHov ? GlacierTheme.ACCENT_BG : GlacierTheme.BG_ITEM);
            if (btnHov) RenderUtil.drawOutline(ctx, btnX, y + 2, 70, SETTING_H - 4, 1, GlacierTheme.ACCENT_GLOW);
            ctx.drawTextWithShadow(textRenderer, ms.getValue(), btnX + 4, y + 8, GlacierTheme.ACCENT);
        } else if (setting instanceof StringSetting ss) {
            int inputX = x + PADDING;
            int inputW = w - PADDING * 2;
            RenderUtil.drawRoundedRect(ctx, inputX, y + 2, inputW, SETTING_H - 4, GlacierTheme.RADIUS_SM, GlacierTheme.BG_ITEM);
            ctx.drawTextWithShadow(textRenderer, ss.getValue(), inputX + 4, y + 8, GlacierTheme.TEXT);
        } else if (setting instanceof ColorSetting cs) {
            int swatch = 14;
            int swX = x + w - swatch - PADDING;
            int swY = y + (SETTING_H - swatch) / 2;
            ctx.fill(swX, swY, swX + swatch, swY + swatch, cs.getValue());
            RenderUtil.drawOutline(ctx, swX, swY, swatch, swatch, 1, GlacierTheme.TEXT_DIM);
        }
    }

    private void renderTogglePill(DrawContext ctx, int x, int y, boolean on) {
        int pillW = 28;
        int pillH = 12;
        int pillColor = on ? GlacierTheme.ACCENT : GlacierTheme.BG_ITEM_HOVER;
        RenderUtil.drawRoundedRect(ctx, x, y, pillW, pillH, pillH / 2, pillColor);
        // Knob
        int knobR = pillH - 4;
        int knobX = on ? x + pillW - knobR - 3 : x + 3;
        ctx.fill(knobX, y + 2, knobX + knobR, y + 2 + knobR, GlacierTheme.TEXT);
    }

    private void renderSlider(DrawContext ctx, NumberSetting ns, int x, int y, int w, int mouseX, int mouseY) {
        int barH = 4;
        int barY = y + 1;
        // Track
        ctx.fill(x, barY, x + w, barY + barH, GlacierTheme.BG_ITEM);
        // Fill
        int fillW = (int)(w * ns.getPercent());
        ctx.fill(x, barY, x + fillW, barY + barH, GlacierTheme.ACCENT);
        // Knob
        int knobX = x + fillW - 4;
        ctx.fill(knobX, barY - 2, knobX + 8, barY + barH + 2, GlacierTheme.ACCENT_HOVER);
        // Value label
        String val = formatDouble(ns.getValue());
        ctx.drawTextWithShadow(textRenderer, val, x + w - textRenderer.getWidth(val), y - 10, GlacierTheme.TEXT_DIM);

        // Store slider coords so mouseClicked/mouseDragged can reference them
        if (draggingSlider == ns) {
            sliderBarX = x;
            sliderBarW = w;
        }
    }

    private void renderDropdown(DrawContext ctx, ModeSetting ms, int x, int y, int mouseX, int mouseY) {
        int itemH = 18;
        int ddW = 100;
        int ddH = ms.getModes().size() * itemH;
        ctx.fill(x, y, x + ddW, y + ddH, GlacierTheme.BG_PANEL);
        RenderUtil.drawOutline(ctx, x, y, ddW, ddH, 1, GlacierTheme.ACCENT_GLOW);
        int iy = y;
        for (String mode : ms.getModes()) {
            boolean hov = mouseX >= x && mouseX <= x + ddW && mouseY >= iy && mouseY <= iy + itemH;
            boolean sel = mode.equals(ms.getValue());
            if (hov) ctx.fill(x, iy, x + ddW, iy + itemH, GlacierTheme.BG_ITEM_HOVER);
            ctx.drawTextWithShadow(textRenderer, mode, x + 6, iy + 5, sel ? GlacierTheme.ACCENT : GlacierTheme.TEXT);
            iy += itemH;
        }
    }

    // -----------------------------------------------------------------------
    // INPUT
    // -----------------------------------------------------------------------

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = (int) mouseX;
        int my = (int) mouseY;

        // Close dropdown if clicking elsewhere
        if (openDropdown != null) {
            int ddW = 100;
            int itemH = 18;
            boolean inDropdown = mx >= dropdownX && mx <= dropdownX + ddW
                    && my >= dropdownY && my <= dropdownY + openDropdown.getModes().size() * itemH;
            if (inDropdown) {
                int idx = (my - dropdownY) / itemH;
                if (idx >= 0 && idx < openDropdown.getModes().size()) {
                    openDropdown.setValue(openDropdown.getModes().get(idx));
                }
            }
            openDropdown = null;
            return true;
        }

        // Profile dropdown
        int profX = guiX + GUI_W - 110;
        int profY = guiY + 8;
        if (mx >= profX && mx <= profX + 100 && my >= profY && my <= profY + 20) {
            profileDropdownOpen = !profileDropdownOpen;
            return true;
        }
        if (profileDropdownOpen) {
            int dy = profY + 22;
            for (String prof : profiles) {
                if (mx >= profX && mx <= profX + 100 && my >= dy && my <= dy + 18) {
                    activeProfile = prof;
                    profileDropdownOpen = false;
                    return true;
                }
                dy += 18;
            }
            profileDropdownOpen = false;
        }

        // Search bar click
        int searchX = guiX + SIDEBAR_W + PADDING + 10;
        int searchY = guiY + 9;
        int searchW = MODULES_W - 20;
        searchFocused = mx >= searchX && mx <= searchX + searchW && my >= searchY && my <= searchY + 18;

        // Sidebar category click
        int sideX = guiX;
        int sideY = guiY + HEADER_H + 8;
        for (Category cat : Category.values()) {
            if (mx >= sideX + 6 && mx <= sideX + SIDEBAR_W - 6 && my >= sideY && my <= sideY + CATEGORY_H - 4) {
                selectedCategory = cat;
                moduleScrollOffset = 0;
                return true;
            }
            sideY += CATEGORY_H;
        }

        // Module panel clicks
        int modX = guiX + SIDEBAR_W + PADDING;
        int modY = guiY + HEADER_H + PADDING;
        List<GlacierMod> mods = getFilteredModules();
        int panelH = GUI_H - HEADER_H;
        int visibleCount = (panelH - PADDING * 2) / MODULE_H;
        for (int i = moduleScrollOffset; i < Math.min(mods.size(), moduleScrollOffset + visibleCount + 1); i++) {
            int row = i - moduleScrollOffset;
            int ry = modY + row * MODULE_H;
            if (ry + MODULE_H > guiY + GUI_H) break;
            GlacierMod mod = mods.get(i);
            if (mx >= modX && mx <= modX + MODULES_W && my >= ry && my <= ry + MODULE_H) {
                if (button == 0) {
                    mod.toggle();
                    selectedModule = mod;
                    settingScrollOffset = 0;
                } else if (button == 1) {
                    selectedModule = mod;
                    settingScrollOffset = 0;
                }
                return true;
            }
        }

        // Settings panel clicks
        GlacierMod mod = selectedModule != null ? selectedModule : hoveredModule;
        if (mod != null) {
            int setX = guiX + SIDEBAR_W + MODULES_W + PADDING * 3;
            int setY = guiY + HEADER_H + 40;
            List<Setting<?>> settings = mod.getSettings();
            int visSet = (panelH - 40) / SETTING_H;
            for (int i = settingScrollOffset; i < Math.min(settings.size(), settingScrollOffset + visSet + 1); i++) {
                int row = i - settingScrollOffset;
                int sy = setY + row * (SETTING_H + 2);
                if (sy + SETTING_H > guiY + GUI_H) break;
                Setting<?> setting = settings.get(i);

                if (setting instanceof BooleanSetting bs) {
                    int pillX = setX + SETTINGS_W - 34;
                    int pillY = sy + SETTING_H / 2 - 5;
                    if (mx >= pillX && mx <= pillX + 28 && my >= pillY && my <= pillY + 12) {
                        bs.toggle();
                        return true;
                    }
                } else if (setting instanceof NumberSetting ns) {
                    int sliderX = setX + PADDING;
                    int sliderW = SETTINGS_W - PADDING * 2;
                    int sliderY = sy + 14;
                    if (mx >= sliderX && mx <= sliderX + sliderW && my >= sliderY - 4 && my <= sliderY + 8) {
                        draggingSlider = ns;
                        sliderBarX = sliderX;
                        sliderBarW = sliderW;
                        double pct = (double)(mx - sliderX) / sliderW;
                        ns.setValue(ns.getMin() + pct * (ns.getMax() - ns.getMin()));
                        return true;
                    }
                    // -/+ buttons
                    if (mx >= sliderX - 12 && mx <= sliderX && my >= sliderY - 4 && my <= sliderY + 8) {
                        ns.decrement(); return true;
                    }
                    if (mx >= sliderX + sliderW && mx <= sliderX + sliderW + 12 && my >= sliderY - 4 && my <= sliderY + 8) {
                        ns.increment(); return true;
                    }
                } else if (setting instanceof ModeSetting ms) {
                    int btnX = setX + SETTINGS_W - 80;
                    if (mx >= btnX && mx <= btnX + 70 && my >= sy + 2 && my <= sy + SETTING_H - 2) {
                        openDropdown = ms;
                        dropdownX = btnX;
                        dropdownY = sy + SETTING_H;
                        return true;
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingSlider != null && sliderBarW > 0) {
            double pct = ((double) mouseX - sliderBarX) / sliderBarW;
            draggingSlider.setValue(draggingSlider.getMin() + pct * (draggingSlider.getMax() - draggingSlider.getMin()));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingSlider = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int mx = (int) mouseX;
        int my = (int) mouseY;
        int modX = guiX + SIDEBAR_W + PADDING;
        int setX = guiX + SIDEBAR_W + MODULES_W + PADDING * 3;
        if (mx >= modX && mx <= modX + MODULES_W) {
            moduleScrollOffset = Math.max(0, moduleScrollOffset - (int) Math.signum(verticalAmount));
        } else if (mx >= setX && mx <= setX + SETTINGS_W) {
            settingScrollOffset = Math.max(0, settingScrollOffset - (int) Math.signum(verticalAmount));
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (openDropdown != null) { openDropdown = null; return true; }
            if (profileDropdownOpen) { profileDropdownOpen = false; return true; }
            close();
            return true;
        }
        if (searchFocused) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if (!searchQuery.isEmpty()) searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchFocused && chr >= 32 && searchQuery.length() < 32) {
            searchQuery += chr;
            moduleScrollOffset = 0;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    // -----------------------------------------------------------------------
    // HELPERS
    // -----------------------------------------------------------------------

    private List<GlacierMod> getFilteredModules() {
        List<GlacierMod> base = GlacierClient.getInstance().getModuleManager().getModulesByCategory(selectedCategory);
        if (searchQuery.isEmpty()) return base;
        List<GlacierMod> filtered = new ArrayList<>();
        String q = searchQuery.toLowerCase();
        for (GlacierMod m : base) {
            if (m.getName().toLowerCase().contains(q) || m.getDescription().toLowerCase().contains(q)) {
                filtered.add(m);
            }
        }
        return filtered;
    }

    private int blendColor(int a, int b, float t) {
        int ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF, aa = (a >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF, ba = (b >> 24) & 0xFF;
        int rr = (int)(ar + (br - ar) * t);
        int rg = (int)(ag + (bg - ag) * t);
        int rb = (int)(ab + (bb - ab) * t);
        int ra = (int)(aa + (ba - aa) * t);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    }

    private String formatDouble(double v) {
        if (v == Math.floor(v)) return String.valueOf((int) v);
        return String.format("%.2f", v);
    }

    @Override
    public boolean shouldPause() { return false; }

    @Override
    public boolean shouldCloseOnEsc() { return true; }
}
