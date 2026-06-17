package net.glacierclient.core.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;

public final class RenderUtil {

    private RenderUtil() {}

    public static void drawRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + height, color);
    }

    public static void drawRoundedRect(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        // Clamp radius to prevent coordinate inversion bugs on small dimensions
        radius = Math.min(radius, Math.min(width / 2, height / 2));
        if (radius <= 0) {
            context.fill(x, y, x + width, y + height, color);
            return;
        }

        context.fill(x + radius, y, x + width - radius, y + height, color);
        context.fill(x, y + radius, x + radius, y + height - radius, color);
        context.fill(x + width - radius, y + radius, x + width, y + height - radius, color);
        
        drawCircleQuadrant(context, x + radius, y + radius, radius, 180, color);
        drawCircleQuadrant(context, x + width - radius, y + radius, radius, 270, color);
        drawCircleQuadrant(context, x + radius, y + height - radius, radius, 90, color);
        drawCircleQuadrant(context, x + width - radius, y + height - radius, radius, 0, color);
    }

    private static void drawCircleQuadrant(DrawContext context, int cx, int cy, int r, int startAngle, int color) {
        int steps = 12;
        for (int i = 0; i < steps; i++) {
            double a = Math.toRadians(startAngle + (90.0 / steps) * i);
            int x = cx + (int)Math.round(Math.cos(a) * r);
            int y = cy - (int)Math.round(Math.sin(a) * r);
            
            // Draw a solid filled strip from the outer coordinate to the inner center quadrant point
            context.fill(Math.min(x, cx), Math.min(y, cy), Math.max(x, cx), Math.max(y, cy), color);
        }
    }

    public static void drawGradientRect(DrawContext context, int x, int y, int width, int height, int colorTop, int colorBottom) {
        context.fillGradient(x, y, x + width, y + height, colorTop, colorBottom);
    }

    public static void drawOutline(DrawContext context, int x, int y, int width, int height, int thickness, int color) {
        context.fill(x, y, x + width, y + thickness, color);
        context.fill(x, y + height - thickness, x + width, y + height, color);
        context.fill(x, y, x + thickness, y + height, color);
        context.fill(x + width - thickness, y, x + width, y + height, color);
    }

    public static void drawRoundedOutline(DrawContext context, int x, int y, int width, int height, int radius, int thickness, int color) {
        radius = Math.min(radius, Math.min(width / 2, height / 2));
        if (radius <= 0) {
            drawOutline(context, x, y, width, height, thickness, color);
            return;
        }

        // Flat edges
        context.fill(x + radius, y, x + width - radius, y + thickness, color); // Top
        context.fill(x + radius, y + height - thickness, x + width - radius, y + height, color); // Bottom
        context.fill(x, y + radius, x + thickness, y + height - radius, color); // Left
        context.fill(x + width - thickness, y + radius, x + width, y + height - radius, color); // Right

        // Rounded outlines at the 4 corner quadrants
        drawCircleQuadrantOutline(context, x + radius, y + radius, radius, thickness, 180, color);
        drawCircleQuadrantOutline(context, x + width - radius, y + radius, radius, thickness, 270, color);
        drawCircleQuadrantOutline(context, x + radius, y + height - radius, radius, thickness, 90, color);
        drawCircleQuadrantOutline(context, x + width - radius, y + height - radius, radius, thickness, 0, color);
    }

    private static void drawCircleQuadrantOutline(DrawContext context, int cx, int cy, int r, int thickness, int startAngle, int color) {
        int steps = 12;
        for (int i = 0; i < steps; i++) {
            double a = Math.toRadians(startAngle + (90.0 / steps) * i);
            int x = cx + (int)Math.round(Math.cos(a) * r);
            int y = cy - (int)Math.round(Math.sin(a) * r);
            context.fill(x, y, x + thickness, y + thickness, color);
        }
    }

    public static void drawShadow(DrawContext context, int x, int y, int width, int height, int shadowSize, int shadowColor) {
        for (int i = 1; i <= shadowSize; i++) {
            int alpha = (int)((shadowColor >> 24 & 0xFF) * (1.0 - (double)i / shadowSize));
            int c = (alpha << 24) | (shadowColor & 0x00FFFFFF);
            context.fill(x - i, y - i, x + width + i, y + height + i, c);
        }
    }

    /**
     * Darkens the inner edges of a panel, fading to transparent towards the centre — the "black edge"
     * vignette used by the mod menu + pause panels. Reusable so every custom panel matches.
     */
    public static void drawEdgeVignette(DrawContext context, int x, int y, int width, int height, int depth, int edgeColor) {
        int a = (edgeColor >>> 24) & 0xFF;
        int rgb = edgeColor & 0xFFFFFF;
        // top & bottom edges via the built-in vertical gradient
        context.fillGradient(x, y, x + width, y + depth, edgeColor, rgb);
        context.fillGradient(x, y + height - depth, x + width, y + height, rgb, edgeColor);
        // left & right edges via per-column alpha interpolation (fillGradient is vertical-only)
        for (int i = 0; i < depth; i++) {
            int ca = (int) (a * (1.0 - (double) i / depth));
            int col = (ca << 24) | rgb;
            context.fill(x + i, y, x + i + 1, y + height, col);
            context.fill(x + width - i - 1, y, x + width - i, y + height, col);
        }
    }

    /**
     * Reusable rounded "hoverable surface" template: fills a rounded rect with the base colour, or the
     * hover colour (plus a subtle accent outline) when hovered. Replaces ad-hoc square hover fills so
     * every custom screen has consistent, rounded hover states.
     */
    public static void drawHoverSurface(DrawContext context, int x, int y, int width, int height,
                                        int radius, boolean hovered, int baseColor, int hoverColor, int hoverOutline) {
        drawRoundedRect(context, x, y, width, height, radius, hovered ? hoverColor : baseColor);
        if (hovered && hoverOutline != 0) drawRoundedOutline(context, x, y, width, height, radius, 1, hoverOutline);
    }
}
