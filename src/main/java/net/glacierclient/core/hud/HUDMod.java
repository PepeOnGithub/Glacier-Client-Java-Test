package net.glacierclient.core.hud;

import net.glacierclient.core.module.Category;
import net.glacierclient.core.module.GlacierMod;
import net.minecraft.client.gui.DrawContext;

public abstract class HUDMod extends GlacierMod {

    private float x;
    private float y;
    private float scale;
    private final int defaultWidth;
    private final int defaultHeight;
    private boolean visible;

    protected HUDMod(String name, String description, int defaultWidth, int defaultHeight) {
        super(name, description, Category.HUD);
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.x = 0.01f;
        this.y = 0.01f;
        this.scale = 1.0f;
        this.visible = true;
    }

    public abstract void render(DrawContext context, float partialTicks);

    public int getX(int screenWidth) { return (int)(x * screenWidth); }
    public int getY(int screenHeight) { return (int)(y * screenHeight); }
    public int getX() { return getX(net.minecraft.client.MinecraftClient.getInstance().getWindow().getScaledWidth()); }
    public int getY() { return getY(net.minecraft.client.MinecraftClient.getInstance().getWindow().getScaledHeight()); }
    public int getWidth()  { return getScaledWidth(); }
    public int getHeight() { return getScaledHeight(); }
    public void setX(float x) { this.x = Math.max(0, Math.min(1, x)); }
    public void setY(float y) { this.y = Math.max(0, Math.min(1, y)); }
    public float getXPercent() { return x; }
    public float getYPercent() { return y; }
    public float getScale() { return scale; }
    public void setScale(float scale) { this.scale = Math.max(0.5f, Math.min(3.0f, scale)); }
    public int getDefaultWidth() { return defaultWidth; }
    public int getDefaultHeight() { return defaultHeight; }
    public int getScaledWidth() { return (int)(defaultWidth * scale); }
    public int getScaledHeight() { return (int)(defaultHeight * scale); }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
}
