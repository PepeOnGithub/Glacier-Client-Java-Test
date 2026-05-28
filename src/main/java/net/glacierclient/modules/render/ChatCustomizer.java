package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class ChatCustomizer extends GlacierMod {

    private final NumberSetting backgroundAlpha = new NumberSetting("Background Alpha", "Chat background transparency", 0, 255, 80);
    private final BooleanSetting slideAnimation = new BooleanSetting("Slide Animation", "Slide messages in from side", true);
    private final NumberSetting chatOpacity = new NumberSetting("Chat Opacity", "Overall chat opacity", 0, 255, 200);
    private final BooleanSetting compactTimestamps = new BooleanSetting("Compact Timestamps", "Show compact timestamps", false);

    public ChatCustomizer() {
        super("Chat Customizer", "Customize chat background and animations", Category.RENDER);
        addSettings(backgroundAlpha, slideAnimation, chatOpacity, compactTimestamps);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public int getBackgroundAlpha() { return (int)(double) backgroundAlpha.getValue(); }
    public boolean isSlideAnimation() { return slideAnimation.getValue(); }
    public int getChatOpacity() { return (int)(double) chatOpacity.getValue(); }
    public boolean isCompactTimestamps() { return compactTimestamps.getValue(); }
}
