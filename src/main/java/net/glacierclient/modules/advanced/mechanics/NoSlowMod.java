package net.glacierclient.modules.advanced.mechanics;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
import net.glacierclient.core.event.*;
import net.minecraft.client.MinecraftClient;
public final class NoSlowMod extends GlacierMod {
    private final BooleanSetting inWater = new BooleanSetting("InWater", true);
    private final BooleanSetting eatingDrinking = new BooleanSetting("EatingDrinking", true);
    private final BooleanSetting soulsand = new BooleanSetting("SoulSand", true);
    public NoSlowMod() {
        super("NoSlow", "Prevents speed reduction from various effects", Category.PVP, -1);
        addSettings(inWater, eatingDrinking, soulsand);
    }
    @EventListen
    public void onMove(EventPlayerMove event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (eatingDrinking.get() && mc.player.isUsingItem()) event.setMultiplier(1.0f);
    }
}
