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
        context.fill(x + radius, y, x + width - radius, y + height, color);
        context.fill(x, y + radius, x + radius, y + height - radius, color);
        context.fill(x + width - radius, y + radius, x + width, y + height - radius, color);
        drawCircleQuadrant(context, x + radius, y + radius, radius, 180, color);
        drawCircleQuadrant(context, x + width - radius, y + radius, radius, 270, color);
        drawCircleQuadrant(context, x + radius, y + height - radius, radius, 90, color);
        drawCircleQuadrant(context, x + width - radius, y + height - radius, radius, 0, color);
    }

    private static void drawCircleQuadrant(DrawContext context, int cx, int cy, int r, int startAngle, int color) {
        int steps = 8;
        for (int i = 0; i < steps; i++) {
            double a1 = Math.toRadians(startAngle + (90.0 / steps) * i);
            double a2 = Math.toRadians(startAngle + (90.0 / steps) * (i + 1));
            int x1 = cx + (int)(Math.cos(a1) * r);
            int y1 = cy - (int)(Math.sin(a1) * r);
            int x2 = cx + (int)(Math.cos(a2) * r);
            int y2 = cy - (int)(Math.sin(a2) * r);
            context.fill(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2), color);
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

    public static void drawShadow(DrawContext context, int x, int y, int width, int height, int shadowSize, int shadowColor) {
        for (int i = 1; i <= shadowSize; i++) {
            int alpha = (int)((shadowColor >> 24 & 0xFF) * (1.0 - (double)i / shadowSize));
            int c = (alpha << 24) | (shadowColor & 0x00FFFFFF);
            context.fill(x - i, y - i, x + width + i, y + height + i, c);
        }
    }
}
