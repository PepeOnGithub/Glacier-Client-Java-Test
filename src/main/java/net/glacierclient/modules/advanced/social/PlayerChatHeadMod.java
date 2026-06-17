package net.glacierclient.modules.advanced.social;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
public final class PlayerChatHeadMod extends GlacierMod {
    private final NumberSetting size = new NumberSetting("Size", 8, 4, 16);
    private final BooleanSetting animate = new BooleanSetting("Animate", true);
    public PlayerChatHeadMod() {
        super("PlayerChatHead", "Shows player skin head icon next to chat messages", Category.QOL, -1);
        addSettings(size, animate);
    }

    public int getSize() { return (int)(double) size.getValue(); }
    public boolean isAnimate() { return animate.getValue(); }
}
