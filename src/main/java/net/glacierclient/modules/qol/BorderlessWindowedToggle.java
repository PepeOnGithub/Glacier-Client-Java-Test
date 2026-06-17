package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

/**
 * Toggles a borderless-fullscreen style window by stripping the GLFW window decorations and stretching
 * the window across the primary monitor. Restores the original decorations / bounds on disable.
 */
public class BorderlessWindowedToggle extends GlacierMod {

    private final BooleanSetting borderless = new BooleanSetting("Borderless", "Strip window border and fill the monitor", true);

    private int savedX, savedY, savedW, savedH;
    private boolean applied;

    public BorderlessWindowedToggle() {
        super("Borderless Windowed", "Toggle borderless windowed mode", Category.QOL);
        addSettings(borderless);
    }

    private long handle() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return (mc != null && mc.getWindow() != null) ? mc.getWindow().getHandle() : 0L;
    }

    @Override
    public void onEnable() {
        long h = handle();
        if (h == 0L || !borderless.getValue()) return;
        try {
            int[] x = new int[1], y = new int[1], w = new int[1], hgt = new int[1];
            GLFW.glfwGetWindowPos(h, x, y);
            GLFW.glfwGetWindowSize(h, w, hgt);
            savedX = x[0]; savedY = y[0]; savedW = w[0]; savedH = hgt[0];

            long monitor = GLFW.glfwGetPrimaryMonitor();
            GLFWVidMode vid = GLFW.glfwGetVideoMode(monitor);
            if (vid == null) return;
            int[] mx = new int[1], my = new int[1];
            GLFW.glfwGetMonitorPos(monitor, mx, my);

            GLFW.glfwSetWindowAttrib(h, GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
            GLFW.glfwSetWindowPos(h, mx[0], my[0]);
            GLFW.glfwSetWindowSize(h, vid.width(), vid.height());
            applied = true;
        } catch (Throwable ignored) {}
    }

    @Override
    public void onDisable() {
        long h = handle();
        if (h == 0L || !applied) return;
        try {
            GLFW.glfwSetWindowAttrib(h, GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
            if (savedW > 0 && savedH > 0) {
                GLFW.glfwSetWindowPos(h, Math.max(0, savedX), Math.max(0, savedY));
                GLFW.glfwSetWindowSize(h, savedW, savedH);
            }
        } catch (Throwable ignored) {}
        applied = false;
    }

    @Override
    public void onTick() {}

    public boolean isBorderless() { return borderless.getValue(); }
}
