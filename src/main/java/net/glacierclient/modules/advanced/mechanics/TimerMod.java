package net.glacierclient.modules.advanced.mechanics;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
public final class TimerMod extends GlacierMod {
    private final NumberSetting speed = new NumberSetting("Speed", 1.0f, 0.1f, 10.0f);
    public TimerMod() {
        super("Timer", "Modifies the game timer speed", Category.ADVANCED, -1);
        addSettings(speed);
    }
}
