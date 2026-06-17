package net.glacierclient.modules.advanced.mechanics;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
public final class AntiKnockbackMod extends GlacierMod {
    private final NumberSetting reduction = new NumberSetting("Reduction", 100, 0, 100);
    public AntiKnockbackMod() {
        super("AntiKnockback", "Reduces knockback received from attacks", Category.PVP, -1);
        addSettings(reduction);
    }
    /** Multiplier applied to incoming knockback strength: 1.0 = full knockback, 0.0 = none. */
    public double getFactor() { return 1.0 - (reduction.get() / 100.0); }
}
