package net.glacierclient.gui.screens;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.hud.HUDEditor;
import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.hud.HUDProfile;
import net.glacierclient.core.theme.GlacierTheme;
import net.glacierclient.core.util.GuiTextures;
import net.glacierclient.core.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class HUDEditorScreen extends Screen {

    private static final int GRID_SIZE   = 10;
    private static final int HANDLE_SIZE = 8;
    private static final int TOOLBAR_H   = 30;
    private static final int SNAP_DIST   = 5;

    private final HUDEditor hudEditor;

    // Drag state
    private HUDMod dragging;
    private int dragOffsetX, dragOffsetY;

    // Scale state
    private HUDMod scaling;
    private int scaleStartX, scaleStartY;
    private float scaleStartScale;

    // Hover
    private HUDMod hoveredMod;
    private HUDMod selectedMod;

    // Grid
    private boolean showGrid = true;

    public HUDEditorScreen() {
        super(Text.literal("HUD Editor"));
        this.hudEditor = new HUDEditor();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Dark transparent overlay
        if (GuiTextures.has("BG")) GuiTextures.fullscreen(ctx, "BG", width, height);
        ctx.fill(0, 0, width, height, 0xCC101214);

        if (showGrid) renderGrid(ctx);
        renderHUDElements(ctx, mouseX, mouseY);
        renderToolbar(ctx, mouseX, mouseY);

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void renderGrid(DrawContext ctx) {
        int gridColor = 0x18FFFFFF;
        for (int x = 0; x < width; x += GRID_SIZE) {
            ctx.fill(x, 0, x + 1, height, gridColor);
        }
        for (int y = TOOLBAR_H; y < height; y += GRID_SIZE) {
            ctx.fill(0, y, width, y + 1, gridColor);
        }
        // Center cross
        ctx.fill(width / 2 - 1, 0, width / 2 + 1, height, 0x22FFFFFF);
        ctx.fill(0, height / 2 - 1, width, height / 2 + 1, 0x22FFFFFF);
    }

    private void renderHUDElements(DrawContext ctx, int mouseX, int mouseY) {
        List<HUDMod> mods = hudEditor.getActiveHUDMods();
        hoveredMod = null;

        for (HUDMod hud : mods) {
            int ex = hud.getX(width);
            int ey = hud.getY(height);
            int ew = hud.getScaledWidth();
            int eh = hud.getScaledHeight();

            boolean hov = mouseX >= ex && mouseX <= ex + ew && mouseY >= ey && mouseY <= ey + eh;
            boolean sel = hud == selectedMod;
            if (hov) hoveredMod = hud;

            // Draw element box
            int bg = sel ? GlacierTheme.ACCENT_BG : (hov ? GlacierTheme.BG_ITEM_HOVER : GlacierTheme.BG_ITEM);
            GuiTextures.rect(ctx, "secondary_bg", ex, ey, ew, eh, bg);

            // Outline
            int borderColor = sel ? GlacierTheme.ACCENT : (hov ? GlacierTheme.ACCENT_GLOW : 0x33FFFFFF);
            RenderUtil.drawOutline(ctx, ex, ey, ew, eh, 1, borderColor);

            // Name label
            ctx.drawTextWithShadow(textRenderer, hud.getName(), ex + 3, ey + (eh - 8) / 2,
                    sel ? GlacierTheme.ACCENT : GlacierTheme.TEXT_DIM);

            // Scale handle in bottom-right corner
            if (hov || sel) {
                int hx = ex + ew - HANDLE_SIZE;
                int hy = ey + eh - HANDLE_SIZE;
                GuiTextures.rect(ctx, "accent_bg", hx, hy, HANDLE_SIZE, HANDLE_SIZE, GlacierTheme.ACCENT);
                RenderUtil.drawOutline(ctx, hx, hy, HANDLE_SIZE, HANDLE_SIZE, 1, GlacierTheme.ACCENT_HOVER);
            }

            // Snap guidelines when dragging this element
            if (dragging == hud) {
                // Vertical center line
                int cx = ex + ew / 2;
                if (Math.abs(cx - width / 2) < SNAP_DIST) {
                    ctx.fill(width / 2, 0, width / 2 + 1, height, GlacierTheme.ACCENT_GLOW);
                }
                // Horizontal center line
                int cy = ey + eh / 2;
                if (Math.abs(cy - height / 2) < SNAP_DIST) {
                    ctx.fill(0, height / 2, width, height / 2 + 1, GlacierTheme.ACCENT_GLOW);
                }
            }

            // Render the actual HUD element, applying the element's Scale exactly as the in-game HUD
            // does (scale about its top-left corner) so the editor preview matches the real size.
            try {
                float s = hud.getScale();
                if (s != 1f) {
                    var ms = ctx.getMatrices();
                    ms.push();
                    ms.translate(ex, ey, 0);
                    ms.scale(s, s, 1f);
                    ms.translate(-ex, -ey, 0);
                    hud.render(ctx, 0f);
                    ms.pop();
                } else {
                    hud.render(ctx, 0f);
                }
            } catch (Exception ignored) {}
        }
    }

    private void renderToolbar(DrawContext ctx, int mouseX, int mouseY) {
        // Toolbar background
        GuiTextures.rect(ctx, "base_bg", 0, 0, width, TOOLBAR_H, GlacierTheme.BG_PANEL);
        ctx.fill(0, TOOLBAR_H - 1, width, TOOLBAR_H, GlacierTheme.ACCENT_GLOW);

        // Title
        ctx.drawTextWithShadow(textRenderer, "HUD Editor", 10, 11, GlacierTheme.ACCENT);

        // Undo/Redo
        renderToolbarBtn(ctx, width / 2 - 60, 5, 50, 20, "Undo", mouseX, mouseY);
        renderToolbarBtn(ctx, width / 2 - 5, 5, 50, 20, "Redo", mouseX, mouseY);

        // Grid toggle
        renderToolbarBtn(ctx, width / 2 + 60, 5, 60, 20, showGrid ? "Grid ON" : "Grid OFF", mouseX, mouseY);

        // Mod Menu button — the HUD Editor is the primary view; module config opens from here.
        renderToolbarBtn(ctx, width - 150, 5, 75, 20, "Mod Menu", mouseX, mouseY);

        // Done button
        renderToolbarBtn(ctx, width - 70, 5, 60, 20, "Done", mouseX, mouseY);

        // Selected mod info
        if (selectedMod != null) {
            String info = String.format("%s  |  scale: %.2f", selectedMod.getName(), selectedMod.getScale());
            ctx.drawTextWithShadow(textRenderer, info, 10 + textRenderer.getWidth("HUD Editor") + 20, 11, GlacierTheme.TEXT_DIM);
        }
    }

    private void renderToolbarBtn(DrawContext ctx, int x, int y, int w, int h, String label, int mouseX, int mouseY) {
        boolean hov = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        int bg = hov ? GlacierTheme.ACCENT_BG : GlacierTheme.BG_ITEM;
        GuiTextures.rect(ctx, hov ? "accent_bg" : "secondary_bg", x, y, w, h, bg);
        if (hov) RenderUtil.drawOutline(ctx, x, y, w, h, 1, GlacierTheme.ACCENT_GLOW);
        ctx.drawTextWithShadow(textRenderer, label, x + (w - textRenderer.getWidth(label)) / 2, y + (h - 8) / 2,
                hov ? GlacierTheme.TEXT : GlacierTheme.TEXT_DIM);
    }

    // -----------------------------------------------------------------------
    // INPUT
    // -----------------------------------------------------------------------

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = (int) mouseX;
        int my = (int) mouseY;

        // Toolbar buttons
        if (my <= TOOLBAR_H) {
            // Undo
            if (mx >= width / 2 - 60 && mx <= width / 2 - 10 && my >= 5 && my <= 25) {
                hudEditor.undo();
                return true;
            }
            // Redo
            if (mx >= width / 2 - 5 && mx <= width / 2 + 45 && my >= 5 && my <= 25) {
                hudEditor.redo();
                return true;
            }
            // Grid toggle
            if (mx >= width / 2 + 60 && mx <= width / 2 + 120 && my >= 5 && my <= 25) {
                showGrid = !showGrid;
                return true;
            }
            // Mod Menu
            if (mx >= width - 150 && mx <= width - 75 && my >= 5 && my <= 25) {
                MinecraftClient.getInstance().setScreen(new ClickGUIScreen());
                return true;
            }
            // Done
            if (mx >= width - 70 && mx <= width - 10 && my >= 5 && my <= 25) {
                close();
                return true;
            }
            return true;
        }

        // Check scale handles first
        for (HUDMod hud : hudEditor.getActiveHUDMods()) {
            int ex = hud.getX(width);
            int ey = hud.getY(height);
            int ew = hud.getScaledWidth();
            int eh = hud.getScaledHeight();
            int hx = ex + ew - HANDLE_SIZE;
            int hy = ey + eh - HANDLE_SIZE;
            if (mx >= hx && mx <= hx + HANDLE_SIZE && my >= hy && my <= hy + HANDLE_SIZE) {
                scaling = hud;
                scaleStartX = mx;
                scaleStartY = my;
                scaleStartScale = hud.getScale();
                selectedMod = hud;
                hudEditor.saveSnapshot();
                return true;
            }
        }

        // Check element drag
        for (HUDMod hud : hudEditor.getActiveHUDMods()) {
            int ex = hud.getX(width);
            int ey = hud.getY(height);
            int ew = hud.getScaledWidth();
            int eh = hud.getScaledHeight();
            if (mx >= ex && mx <= ex + ew && my >= ey && my <= ey + eh) {
                dragging = hud;
                dragOffsetX = mx - ex;
                dragOffsetY = my - ey;
                selectedMod = hud;
                hudEditor.saveSnapshot();
                return true;
            }
        }

        selectedMod = null;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        int mx = (int) mouseX;
        int my = (int) mouseY;

        if (dragging != null) {
            int newX = mx - dragOffsetX;
            int newY = my - dragOffsetY;

            // Grid snap
            newX = snapToGrid(newX);
            newY = snapToGrid(newY);

            // Center snap
            int cx = newX + dragging.getScaledWidth() / 2;
            int cy = newY + dragging.getScaledHeight() / 2;
            if (Math.abs(cx - width / 2) < SNAP_DIST) newX = width / 2 - dragging.getScaledWidth() / 2;
            if (Math.abs(cy - height / 2) < SNAP_DIST) newY = height / 2 - dragging.getScaledHeight() / 2;

            dragging.setPositionPixels(newX, newY, width, height);
            return true;
        }

        if (scaling != null) {
            int dx = mx - scaleStartX;
            int dy = my - scaleStartY;
            float delta2 = (dx + dy) / 100.0f;
            scaling.setScale(scaleStartScale + delta2);
            scaling.setPositionPixels(scaling.getX(width), scaling.getY(height), width, height);
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = null;
        scaling = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Ctrl+Z = undo
        if (keyCode == GLFW.GLFW_KEY_Z && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            hudEditor.undo();
            return true;
        }
        // Ctrl+Y = redo
        if (keyCode == GLFW.GLFW_KEY_Y && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            hudEditor.redo();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        // Arrow key nudge
        if (selectedMod != null) {
            float nudge = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0 ? 10f : 1f;
            if (keyCode == GLFW.GLFW_KEY_LEFT)  { selectedMod.setPositionPixels(selectedMod.getX(width) - (int) nudge, selectedMod.getY(height), width, height); return true; }
            if (keyCode == GLFW.GLFW_KEY_RIGHT) { selectedMod.setPositionPixels(selectedMod.getX(width) + (int) nudge, selectedMod.getY(height), width, height); return true; }
            if (keyCode == GLFW.GLFW_KEY_UP)    { selectedMod.setPositionPixels(selectedMod.getX(width), selectedMod.getY(height) - (int) nudge, width, height); return true; }
            if (keyCode == GLFW.GLFW_KEY_DOWN)  { selectedMod.setPositionPixels(selectedMod.getX(width), selectedMod.getY(height) + (int) nudge, width, height); return true; }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // -----------------------------------------------------------------------
    // HELPERS
    // -----------------------------------------------------------------------

    private int snapToGrid(int value) {
        return Math.round((float) value / GRID_SIZE) * GRID_SIZE;
    }

    @Override
    public void close() {
        // Persist HUD element positions/scale so the layout survives restarts.
        GlacierClient.getInstance().getConfigManager().save();
        super.close();
    }

    @Override
    public boolean shouldPause() { return false; }

    @Override
    public boolean shouldCloseOnEsc() { return true; }
}
