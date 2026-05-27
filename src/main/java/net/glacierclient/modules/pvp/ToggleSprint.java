package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.minecraft.client.MinecraftClient;

public class ToggleSprint extends GlacierMod {

    private final BooleanSetting toggleMode = new BooleanSetting("Toggle Mode", "Toggle sprint on key press instead of hold", false);
    private final BooleanSetting sprintInWater = new BooleanSetting("Sprint In Water", "Continue sprinting while swimming", true);
    private final BooleanSetting sprintWhileBlocking = new BooleanSetting("Sprint While Blocking", "Sprint while holding a shield", false);

    private boolean toggled = false;

    public ToggleSprint() {
        super("Toggle Sprint", "Hold sprint key to always sprint", Category.PVP);
        addSettings(toggleMode, sprintInWater, sprintWhileBlocking);
    }

    @Override
    public void onEnable() { toggled = false; }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) mc.player.setSprinting(false);
        toggled = false;
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        boolean shouldSprint = true;
        if (!sprintInWater.getValue() && mc.player.isTouchingWater()) shouldSprint = false;
        if (!sprintWhileBlocking.getValue() && mc.player.isBlocking()) shouldSprint = false;
        if (mc.player.getHungerManager().getFoodLevel() <= 6) shouldSprint = false;
        if (shouldSprint) mc.player.setSprinting(true);
    }
}
