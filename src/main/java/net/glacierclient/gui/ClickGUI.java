package net.glacierclient.gui;

import net.glacierclient.gui.screens.ClickGUIScreen;
import net.minecraft.client.MinecraftClient;

public class ClickGUI {

    private static final int DEFAULT_KEYBIND = org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT;

    public void open() {
        MinecraftClient.getInstance().setScreen(new ClickGUIScreen());
    }

    public int getKeybind() { return DEFAULT_KEYBIND; }
}
