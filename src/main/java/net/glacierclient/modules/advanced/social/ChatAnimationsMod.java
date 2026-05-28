package net.glacierclient.modules.advanced.social;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
public final class ChatAnimationsMod extends GlacierMod {
    private final ModeSetting style = new ModeSetting("Style", List.of("Slide", "Fade", "Pop"), "Slide");
    private final NumberSetting speed = new NumberSetting("Speed", 150, 50, 500);
    public ChatAnimationsMod() {
        super("ChatAnimations", "Animates chat messages as they appear", Category.QOL, -1);
        addSettings(style, speed);
    }
}
