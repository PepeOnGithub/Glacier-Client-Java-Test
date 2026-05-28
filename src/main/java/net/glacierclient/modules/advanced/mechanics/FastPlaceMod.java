package net.glacierclient.modules.advanced.mechanics;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
public final class FastPlaceMod extends GlacierMod {
    private final NumberSetting delay = new NumberSetting("Delay", 0, 0, 4);
    public FastPlaceMod() {
        super("FastPlace", "Reduces the delay between placing blocks", Category.QOL, -1);
        addSettings(delay);
    }
}
