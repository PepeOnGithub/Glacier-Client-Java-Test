package net.glacierclient.bridge.v1165;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

/**
 * 1.16.5 has no {@code DrawContext}; rectangle fills live on {@link DrawableHelper} as protected
 * members. Subclassing exposes them so the bridge can draw HUD backgrounds.
 */
final class Draw1165 extends DrawableHelper {
    static final Draw1165 I = new Draw1165();
    private Draw1165() {}

    void rect(MatrixStack m, int x1, int y1, int x2, int y2, int color) {
        fill(m, x1, y1, x2, y2, color);
    }

    void gradient(MatrixStack m, int x1, int y1, int x2, int y2, int top, int bottom) {
        fillGradient(m, x1, y1, x2, y2, top, bottom);
    }
}
