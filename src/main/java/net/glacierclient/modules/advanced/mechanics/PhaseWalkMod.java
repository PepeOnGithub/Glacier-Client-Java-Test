package net.glacierclient.modules.advanced.mechanics;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
public final class PhaseWalkMod extends GlacierMod {
    private final BooleanSetting water = new BooleanSetting("ThroughWater", false);
    private final BooleanSetting web = new BooleanSetting("ThroughCobweb", true);
    public PhaseWalkMod() {
        super("PhaseWalk", "Allows moving through certain materials", Category.ADVANCED, -1);
        addSettings(water, web);
    }
    public boolean isWeb() { return web.get(); }
    public boolean isWater() { return water.get(); }
}
