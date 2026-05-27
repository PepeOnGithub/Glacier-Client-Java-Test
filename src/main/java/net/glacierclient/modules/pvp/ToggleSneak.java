package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.minecraft.client.MinecraftClient;

public class ToggleSneak extends GlacierMod {

    private final BooleanSetting toggleMode = new BooleanSetting("Toggle Mode", "Toggle sneak on key press", true);
    private final BooleanSetting sneakInGUI = new BooleanSetting("Sneak In GUI", "Keep sneaking while GUI is open", false);

    private boolean isSneaking = false;

    public ToggleSneak() {
        super("Toggle Sneak", "Toggle sneak on key press instead of hold", Category.PVP);
        addSettings(toggleMode, sneakInGUI);
    }

    @Override
    public void onEnable() { isSneaking = false; }

    @Override
    public void onDisable() {
        isSneaking = false;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) mc.player.input.sneaking = false;
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        boolean inGui = mc.currentScreen != null;
        boolean apply = !inGui || sneakInGUI.getValue();
        if (apply) mc.player.input.sneaking = isSneaking;
    }

    public void onSneakKeyPress() {
        if (toggleMode.getValue()) isSneaking = !isSneaking;
    }
}
