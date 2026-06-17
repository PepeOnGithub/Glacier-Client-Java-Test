package net.glacierclient.core.hud;

import net.glacierclient.core.module.Category;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/**
 * Base class for HUD modules. Provides a universal, persisted customization spine that every HUD
 * element shares — Scale, Color, Background and Shadow — exposed as settings in the ClickGUI.
 * Subclasses should draw text via {@link #drawString} (and optionally {@link #drawBackground}) so
 * they automatically honour the user's colour / shadow / background choices.
 */
public abstract class HUDMod extends GlacierMod {

    // Distributes successive HUD elements into a non-overlapping default layout so they never all
    // spawn stacked in the top-left corner (and never off-screen).
    private static int spawnIndex = 0;

    private float x;
    private float y;
    private final int defaultWidth;
    private final int defaultHeight;
    private boolean visible;

    // universal customization (persisted via ConfigManager by setting name)
    protected final NumberSetting scaleSetting = new NumberSetting("Scale", "HUD element size", 0.5, 3.0, 1.0, 0.1);
    protected final ColorSetting colorSetting = new ColorSetting("Color", "Primary text colour", 0xFFFFFFFF);
    protected final BooleanSetting backgroundSetting = new BooleanSetting("Background", "Draw a rounded background box", false);
    protected final BooleanSetting shadowSetting = new BooleanSetting("Text Shadow", "Render text with a drop shadow", true);
    protected final ModeSetting anchorSetting = new ModeSetting("Anchor", "Relative screen anchor", "Top Left", "Top Left", "Top Right", "Bottom Left", "Bottom Right", "Center");

    protected HUDMod(String name, String description, int defaultWidth, int defaultHeight) {
        super(name, description, Category.HUD);
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        int idx = spawnIndex++;
        int perColumn = 6;
        this.x = Math.min(0.80f, 0.01f + (idx / perColumn) * 0.16f);
        this.y = Math.min(0.85f, 0.02f + (idx % perColumn) * 0.10f);
        this.visible = true;
        addSettings(scaleSetting, colorSetting, backgroundSetting, shadowSetting, anchorSetting);
    }

    public abstract void render(DrawContext context, float partialTicks);

    // --- universal customization accessors ---
    public int getTextColor() { return colorSetting.getValue(); }
    public boolean hasBackground() { return backgroundSetting.getValue(); }
    public boolean hasShadow() { return shadowSetting.getValue(); }

    /** Draw text honouring the user's colour + shadow choice; returns the text width. */
    protected int drawString(DrawContext ctx, TextRenderer tr, String text, int px, int py) {
        ctx.drawText(tr, text, px, py, getTextColor(), hasShadow());
        return tr.getWidth(text);
    }

    /** Draw the optional rounded background box behind a HUD element of the given size. */
    protected void drawBackground(DrawContext ctx, int px, int py, int w, int h) {
        if (hasBackground()) RenderUtil.drawRoundedRect(ctx, px - 2, py - 2, w + 4, h + 4, 4, 0x90000000);
    }

    // --- geometry ---
    // X/Y are stored as a normalized fraction of the screen, but clamped on read so the *whole*
    // element stays on-screen at the current scale (boundary checking that survives resolution and
    // GUI-scale changes).
    public int getX(int screenWidth) {
        int max = Math.max(0, screenWidth - getScaledWidth());
        int px = Math.round(x * max);
        return switch (anchorSetting.getValue()) {
            case "Top Right", "Bottom Right" -> max - px;
            case "Center" -> Math.max(0, Math.min(max, screenWidth / 2 - getScaledWidth() / 2 + Math.round((x - 0.5f) * max)));
            default -> px;
        };
    }
    public int getY(int screenHeight) {
        int max = Math.max(0, screenHeight - getScaledHeight());
        int py = Math.round(y * max);
        return switch (anchorSetting.getValue()) {
            case "Bottom Left", "Bottom Right" -> max - py;
            case "Center" -> Math.max(0, Math.min(max, screenHeight / 2 - getScaledHeight() / 2 + Math.round((y - 0.5f) * max)));
            default -> py;
        };
    }
    public int getX() { return getX(MinecraftClient.getInstance().getWindow().getScaledWidth()); }
    public int getY() { return getY(MinecraftClient.getInstance().getWindow().getScaledHeight()); }
    public int getWidth() { return getScaledWidth(); }
    public int getHeight() { return getScaledHeight(); }
    public void setX(float x) { this.x = Math.max(0, Math.min(1, x)); }
    public void setY(float y) { this.y = Math.max(0, Math.min(1, y)); }
    public float getXPercent() { return x; }
    public float getYPercent() { return y; }
    public float getScale() { return (float)(double) scaleSetting.getValue(); }
    public void setScale(float scale) { scaleSetting.setValue((double) Math.max(0.5f, Math.min(3.0f, scale))); }
    public void setPositionPixels(int px, int py, int screenWidth, int screenHeight) {
        int maxX = Math.max(0, screenWidth - getScaledWidth());
        int maxY = Math.max(0, screenHeight - getScaledHeight());
        int clampedX = Math.max(0, Math.min(px, maxX));
        int clampedY = Math.max(0, Math.min(py, maxY));
        String anchor = anchorSetting.getValue();
        if ("Top Right".equals(anchor) || "Bottom Right".equals(anchor)) clampedX = maxX - clampedX;
        if ("Bottom Left".equals(anchor) || "Bottom Right".equals(anchor)) clampedY = maxY - clampedY;
        if ("Center".equals(anchor)) {
            clampedX = Math.round((clampedX - (screenWidth / 2f - getScaledWidth() / 2f)) + maxX / 2f);
            clampedY = Math.round((clampedY - (screenHeight / 2f - getScaledHeight() / 2f)) + maxY / 2f);
        }
        x = maxX == 0 ? 0 : Math.max(0f, Math.min(1f, (float) clampedX / maxX));
        y = maxY == 0 ? 0 : Math.max(0f, Math.min(1f, (float) clampedY / maxY));
    }
    public int getDefaultWidth() { return defaultWidth; }
    public int getDefaultHeight() { return defaultHeight; }
    public int getScaledWidth() { return (int)(defaultWidth * getScale()); }
    public int getScaledHeight() { return (int)(defaultHeight * getScale()); }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
}
