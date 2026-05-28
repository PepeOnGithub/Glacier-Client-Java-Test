package net.glacierclient.modules.expanded.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;

public class ArmorBreakWarning extends GlacierMod {

    private static final int RED = 0xFFFF5555;

    private final NumberSetting threshold = new NumberSetting("Threshold", "Durability percent below which to warn", 30, 10, 100);
    private final BooleanSetting soundAlert = new BooleanSetting("Sound Alert", "Play alert sound when threshold hit", false);
    private final BooleanSetting flash = new BooleanSetting("Flash", "Flash screen when armor is critically low", false);
    private final ColorSetting warningColor = new ColorSetting("Warning Color", "Color of warning indicator", RED);
    private final ModeSetting alertFor = new ModeSetting("Alert For", "Which armor slots to monitor", "Equipped", "All", "Equipped", "Hotbar");

    public ArmorBreakWarning() {
        super("Armor Break Warning", "Alert when armor durability below threshold", Category.PVP);
        addSettings(threshold, soundAlert, flash, warningColor, alertFor);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public int getThreshold() { return (int) threshold.getValue(); }
    public boolean isSoundAlert() { return soundAlert.getValue(); }
    public boolean isFlash() { return flash.getValue(); }
    public int getWarningColor() { return warningColor.getValue(); }
    public String getAlertFor() { return alertFor.getValue(); }
}
