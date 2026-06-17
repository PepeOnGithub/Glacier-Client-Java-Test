package net.glacierclient.gui;

import net.glacierclient.gui.screens.ClickGUIScreen;
import net.minecraft.client.MinecraftClient;

public class ClickGUI {

    private static final int DEFAULT_KEYBIND = org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT;

    public void open() {
        MinecraftClient mc = MinecraftClient.getInstance();
        // Remember the screen we came from (e.g. the Glacier title screen) so closing returns there,
        // not to a freshly created vanilla title screen.
        mc.setScreen(new ClickGUIScreen(mc.currentScreen));
    }

    public int getKeybind() { return DEFAULT_KEYBIND; }
}
