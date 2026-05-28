package net.glacierclient.modules.expanded.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;

public class TotemPopTracker extends GlacierMod {

    private static final int ORANGE = 0xFFFFA500;

    private final BooleanSetting showInChat = new BooleanSetting("Show In Chat", "Show totem pop notification in chat", false);
    private final BooleanSetting showHeadIcon = new BooleanSetting("Show Head Icon", "Show player head icon overlay on totem pop", false);
    private final NumberSetting iconDuration = new NumberSetting("Icon Duration", "Milliseconds to display head icon", 5000, 1000, 10000);
    private final ColorSetting popColor = new ColorSetting("Pop Color", "Color of totem pop notification", ORANGE);

    public TotemPopTracker() {
        super("Totem Pop Tracker", "List totem pops in chat + head icon overlay", Category.PVP);
        addSettings(showInChat, showHeadIcon, iconDuration, popColor);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public boolean isShowInChat() { return showInChat.getValue(); }
    public boolean isShowHeadIcon() { return showHeadIcon.getValue(); }
    public int getIconDuration() { return (int)(double) iconDuration.getValue(); }
    public int getPopColor() { return popColor.getValue(); }
}
