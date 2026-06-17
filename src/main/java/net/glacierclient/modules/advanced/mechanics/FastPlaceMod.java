package net.glacierclient.modules.advanced.mechanics;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
public final class FastPlaceMod extends GlacierMod {
    private final NumberSetting delay = new NumberSetting("Delay", 0, 0, 4);
    public FastPlaceMod() {
        super("FastPlace", "Reduces the delay between placing blocks", Category.QOL, -1);
        addSettings(delay);
    }
    /** Ticks of item-use cooldown to allow between placements (0 = no delay). */
    public int getDelay() { return (int) Math.round(delay.get()); }
}
