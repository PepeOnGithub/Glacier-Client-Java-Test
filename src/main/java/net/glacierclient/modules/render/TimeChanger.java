package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class TimeChanger extends GlacierMod {

    private final NumberSetting time = new NumberSetting("Time", "Visual time of day (ticks)", 0, 24000, 6000);
    private final BooleanSetting cycle = new BooleanSetting("Cycle", "Cycle time automatically", false);
    private final NumberSetting cycleSpeed = new NumberSetting("Cycle Speed", "Speed of time cycling", 0.1, 10.0, 1.0);

    private double currentTime;

    public TimeChanger() {
        super("Time Changer", "Lock the visual time of day client-side", Category.RENDER);
        addSettings(time, cycle, cycleSpeed);
    }

    @Override
    public void onEnable() {
        currentTime = time.getValue();
    }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        if (cycle.getValue()) {
            currentTime = (currentTime + cycleSpeed.getValue()) % 24000.0;
        } else {
            currentTime = time.getValue();
        }
    }

    public long getVisualTime() {
        return (long) currentTime;
    }
}
