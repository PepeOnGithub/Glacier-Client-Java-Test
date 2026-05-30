package net.glacierclient.core.hud;

import net.glacierclient.core.module.Category;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
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

    protected HUDMod(String name, String description, int defaultWidth, int defaultHeight) {
        super(name, description, Category.HUD);
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.x = 0.01f;
        this.y = 0.01f;
        this.visible = true;
        addSettings(scaleSetting, colorSetting, backgroundSetting, shadowSetting);
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
    public int getX(int screenWidth) { return (int)(x * screenWidth); }
    public int getY(int screenHeight) { return (int)(y * screenHeight); }
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
    public int getDefaultWidth() { return defaultWidth; }
    public int getDefaultHeight() { return defaultHeight; }
    public int getScaledWidth() { return (int)(defaultWidth * getScale()); }
    public int getScaledHeight() { return (int)(defaultHeight * getScale()); }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
}
