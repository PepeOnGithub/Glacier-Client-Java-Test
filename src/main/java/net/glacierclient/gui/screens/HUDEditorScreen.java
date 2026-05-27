package net.glacierclient.gui.screens;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.theme.GlacierTheme;
import net.glacierclient.core.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * HUD Editor Screen — opened via the HOME key by default.
 *
 * Features:
 *  - All active HUD elements shown as draggable bordered boxes.
 *  - Snap-to-grid (8 px) with visible grid lines while dragging.
 *  - Scale handle at the bottom-right corner of the selected element.
 *  - Undo / redo with Ctrl+Z / Ctrl+Y (up to 64 states).
 *  - "Done" button closes the screen.
 */
public class HUDEditorScreen extends Screen {

    // -----------------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------------
    private static final int SNAP          = 8;
    private static final int HANDLE_SIZE   = 8;
    private static final int DONE_W        = 60;
    private static final int DONE_H        = 20;
    private static final int HEADER_H      = 24;
    private static final int MAX_HISTORY   = 64;

    // Glacier theme colours (mirrors GlacierTheme for convenience)
    private static final int C_BG          = GlacierTheme.BG;           // #23272A
    private static final int C_PANEL       = GlacierTheme.BG_PANEL;     // #2C2F33
    private static final int C_ITEM        = GlacierTheme.BG_ITEM;      // 4 % white
    private static final int C_ITEM_HOVER  = GlacierTheme.BG_ITEM_HOVER;
    private static final int C_ACCENT      = GlacierTheme.ACCENT;       // #7289DA
    private static final int C_ACCENT_BG   = GlacierTheme.ACCENT_BG;
    private static final int C_TEXT        = GlacierTheme.TEXT;
    private static final int C_TEXT_DIM    = GlacierTheme.TEXT_DIM;
    private static final int C_GREEN       = GlacierTheme.GREEN;
    private static final int C_RED         = GlacierTheme.RED;

    // Grid line colour — very subtle
    private static final int C_GRID        = 0x18FFFFFF;

    // -----------------------------------------------------------------------
    // State
    // -----------------------------------------------------------------------

    /** Snapshot of every HUD element's position/scale — used for undo/redo. */
    private record ElementState(HUDMod mod, int x, int y, float scale) {}

    private final Deque<List<ElementState>> undoStack = new ArrayDeque<>();
    private final Deque<List<ElementState>> redoStack = new ArrayDeque<>();

    // Drag state
    private HUDMod draggingMod    = null;
    private int    dragOffsetX    = 0;
    private int    dragOffsetY    = 0;

    // Scale-handle state
    private HUDMod scalingMod     = null;
    private double scaleStartX    = 0;
    private float  scaleStartVal  = 1f;

    // Selected (right-click or currently interacting)
    private HUDMod selectedMod    = null;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    public HUDEditorScreen() {
        super(Text.literal("HUD Editor"));
    }

    // -----------------------------------------------------------------------
    // Screen lifecycle
    // -----------------------------------------------------------------------

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // -----------------------------------------------------------------------
    // Rendering
    // -----------------------------------------------------------------------

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Semi-transparent overlay
        context.fill(0, 0, width, height, 0xB0000000);

        // Grid lines (only while dragging or scaling)
        if (draggingMod != null || scalingMod != null) {
            drawGrid(context);
        }

        // Render each HUD element box
        List<HUDMod> huds = getHUDMods();
        for (HUDMod hud : huds) {
            drawHUDBox(context, hud, mouseX, mouseY);
        }

        // Header bar
        context.fill(0, 0, width, HEADER_H, C_PANEL);
        context.fill(0, HEADER_H - 1, width, HEADER_H, C_ACCENT_BG);
        context.drawCenteredTextWithShadow(textRenderer, "HUD Editor", width / 2, 6, C_ACCENT);

        // Hint text
        String hint = "Drag to move  |  Drag ▣ to scale  |  Ctrl+Z undo  |  Ctrl+Y redo";
        context.drawText(textRenderer, hint, 6, 7, C_TEXT_DIM, false);

        // Done button
        int doneX = width  - DONE_W - 8;
        int doneY = (HEADER_H - DONE_H) / 2;
        boolean doneHover = isOver(mouseX, mouseY, doneX, doneY, DONE_W, DONE_H);
        context.fill(doneX, doneY, doneX + DONE_W, doneY + DONE_H, doneHover ? C_ACCENT : C_ACCENT_BG);
        RenderUtil.drawOutline(context, doneX, doneY, DONE_W, DONE_H, 1, C_ACCENT);
        context.drawCenteredTextWithShadow(textRenderer, "Done", doneX + DONE_W / 2, doneY + 6, C_TEXT);

        super.render(context, mouseX, mouseY, delta);
    }

    /** Draw a subtle 8-px snap grid across the whole screen. */
    private void drawGrid(DrawContext context) {
        for (int x = 0; x < width; x += SNAP) {
            context.fill(x, HEADER_H, x + 1, height, C_GRID);
        }
        for (int y = HEADER_H; y < height; y += SNAP) {
            context.fill(0, y, width, y + 1, C_GRID);
        }
    }

    /** Draw an individual HUD element as a labelled, bordered box. */
    private void drawHUDBox(DrawContext context, HUDMod hud, int mouseX, int mouseY) {
        int x = hud.getHudX();
        int y = hud.getHudY();
        int w = hud.getHudWidth();
        int h = hud.getHudHeight();

        boolean isSelected = hud == selectedMod;
        boolean hovered    = isOver(mouseX, mouseY, x, y, w, h);

        // Background
        int bg = isSelected ? C_ITEM_HOVER : (hovered ? C_ITEM_HOVER : C_ITEM);
        context.fill(x, y, x + w, y + h, bg);

        // Border
        int borderColor = isSelected ? C_ACCENT : (hovered ? 0x66FFFFFF : 0x33FFFFFF);
        RenderUtil.drawOutline(context, x, y, w, h, 1, borderColor);

        // Accent left edge if selected
        if (isSelected) {
            context.fill(x, y, x + 2, y + h, C_ACCENT);
        }

        // Module name label
        context.drawText(textRenderer, hud.getName(), x + 4, y + (h / 2) - 4, isSelected ? C_ACCENT : C_TEXT_DIM, false);

        // Scale handle at bottom-right
        int hx = x + w - HANDLE_SIZE;
        int hy = y + h - HANDLE_SIZE;
        boolean handleHover = isOver(mouseX, mouseY, hx, hy, HANDLE_SIZE, HANDLE_SIZE);
        context.fill(hx, hy, hx + HANDLE_SIZE, hy + HANDLE_SIZE,
                handleHover ? C_ACCENT : C_ACCENT_BG);
        RenderUtil.drawOutline(context, hx, hy, HANDLE_SIZE, HANDLE_SIZE, 1, C_ACCENT);

        // Position / scale info when selected
        if (isSelected) {
            String info = x + ", " + y + "  " + String.format("%.2f×", hud.getHudScale());
            context.drawText(textRenderer, info, x + 2, y + h + 2, C_TEXT_DIM, false);
        }
    }

    // -----------------------------------------------------------------------
    // Mouse interaction
    // -----------------------------------------------------------------------

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = (int) mouseX;
        int my = (int) mouseY;

        // Done button
        int doneX = width  - DONE_W - 8;
        int doneY = (HEADER_H - DONE_H) / 2;
        if (isOver(mx, my, doneX, doneY, DONE_W, DONE_H)) {
            this.close();
            return true;
        }

        // Check scale handles first, then drag
        for (HUDMod hud : getHUDMods()) {
            int x = hud.getHudX();
            int y = hud.getHudY();
            int w = hud.getHudWidth();
            int h = hud.getHudHeight();
            int hx = x + w - HANDLE_SIZE;
            int hy = y + h - HANDLE_SIZE;

            if (button == 0 && isOver(mx, my, hx, hy, HANDLE_SIZE, HANDLE_SIZE)) {
                pushUndo();
                scalingMod    = hud;
                scaleStartX   = mouseX;
                scaleStartVal = hud.getHudScale();
                selectedMod   = hud;
                return true;
            }

            if (button == 0 && isOver(mx, my, x, y, w, h)) {
                pushUndo();
                draggingMod = hud;
                dragOffsetX = mx - x;
                dragOffsetY = my - y;
                selectedMod = hud;
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

        if (draggingMod != null) {
            int nx = snap(mx - dragOffsetX);
            int ny = snap(my - dragOffsetY);
            // Clamp within screen bounds
            nx = Math.max(0, Math.min(width  - draggingMod.getHudWidth(),  nx));
            ny = Math.max(HEADER_H, Math.min(height - draggingMod.getHudHeight(), ny));
            draggingMod.setHudPosition(nx, ny);
            return true;
        }

        if (scalingMod != null) {
            double dx    = mouseX - scaleStartX;
            float  scale = Math.max(0.5f, Math.min(3.0f, scaleStartVal + (float)(dx / 100.0)));
            scalingMod.setHudScale(scale);
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingMod = null;
        scalingMod  = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    // -----------------------------------------------------------------------
    // Keyboard interaction — Ctrl+Z / Ctrl+Y
    // -----------------------------------------------------------------------

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean ctrl = (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;

        if (ctrl && keyCode == GLFW.GLFW_KEY_Z) {
            undo();
            return true;
        }
        if (ctrl && keyCode == GLFW.GLFW_KEY_Y) {
            redo();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // -----------------------------------------------------------------------
    // Undo / Redo helpers
    // -----------------------------------------------------------------------

    private void pushUndo() {
        List<ElementState> snapshot = captureState();
        undoStack.push(snapshot);
        if (undoStack.size() > MAX_HISTORY) undoStack.pollLast();
        redoStack.clear();
    }

    private void undo() {
        if (undoStack.isEmpty()) return;
        redoStack.push(captureState());
        applyState(undoStack.pop());
    }

    private void redo() {
        if (redoStack.isEmpty()) return;
        undoStack.push(captureState());
        applyState(redoStack.pop());
    }

    private List<ElementState> captureState() {
        List<ElementState> states = new ArrayList<>();
        for (HUDMod hud : getHUDMods()) {
            states.add(new ElementState(hud, hud.getHudX(), hud.getHudY(), hud.getHudScale()));
        }
        return states;
    }

    private void applyState(List<ElementState> states) {
        for (ElementState s : states) {
            s.mod().setHudPosition(s.x(), s.y());
            s.mod().setHudScale(s.scale());
        }
    }

    // -----------------------------------------------------------------------
    // Utility helpers
    // -----------------------------------------------------------------------

    /** Collect all enabled HUD modules. */
    private List<HUDMod> getHUDMods() {
        List<HUDMod> list = new ArrayList<>();
        for (GlacierMod mod : GlacierClient.getInstance().getModuleManager().getModules()) {
            if (mod instanceof HUDMod hud && hud.isEnabled()) {
                list.add(hud);
            }
        }
        return list;
    }

    private int snap(int value) {
        return Math.round((float) value / SNAP) * SNAP;
    }

    private boolean isOver(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
}
