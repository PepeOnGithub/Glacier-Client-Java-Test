package net.glacierclient.modules.advanced.mechanics;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
import net.glacierclient.core.event.*;
public final class AntiKnockbackMod extends GlacierMod {
    private final NumberSetting reduction = new NumberSetting("Reduction", 100, 0, 100);
    public AntiKnockbackMod() {
        super("AntiKnockback", "Reduces knockback received from attacks", Category.PVP, -1);
        addSettings(reduction);
    }
    @EventListen
    public void onKnockback(EventKnockback event) {
        double factor = 1.0 - (reduction.get() / 100.0);
        event.setX(event.getX() * factor);
        event.setZ(event.getZ() * factor);
    }
}
