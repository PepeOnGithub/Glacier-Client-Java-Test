package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;

public class BorderlessWindowedToggle extends GlacierMod {

    private final BooleanSetting borderless = new BooleanSetting("Borderless", "Enable borderless windowed mode", true);
    private final BooleanSetting rememberPosition = new BooleanSetting("Remember Position", "Remember window position", true);
    private final NumberSetting windowX = new NumberSetting("Window X", "Window X position", 0, 3840, 0);
    private final NumberSetting windowY = new NumberSetting("Window Y", "Window Y position", 0, 2160, 0);

    public BorderlessWindowedToggle() {
        super("Borderless Windowed", "Toggle borderless windowed mode", Category.QOL);
        addSettings(borderless, rememberPosition, windowX, windowY);
    }

    @Override
    public void onEnable() {
        // Window mode change via LWJGL handled by mixin
    }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isBorderless() { return borderless.getValue(); }
    public int getWindowX() { return (int)(double) windowX.getValue(); }
    public int getWindowY() { return (int)(double) windowY.getValue(); }
}
