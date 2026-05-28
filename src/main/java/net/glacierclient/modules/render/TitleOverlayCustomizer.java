package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class TitleOverlayCustomizer extends GlacierMod {

    private final NumberSetting textScale = new NumberSetting("Text Scale", "Title text scale", 0.5, 3.0, 1.0);
    private final NumberSetting posY = new NumberSetting("Position Y", "Vertical position %", 10, 90, 40);
    private final BooleanSetting hideTitle = new BooleanSetting("Hide Title", "Hide main title", false);
    private final BooleanSetting hideSubtitle = new BooleanSetting("Hide Subtitle", "Hide subtitle", false);
    private final BooleanSetting hideActionBar = new BooleanSetting("Hide Action Bar", "Hide action bar messages", false);

    public TitleOverlayCustomizer() {
        super("Title Overlay", "Customize title message appearance", Category.RENDER);
        addSettings(textScale, posY, hideTitle, hideSubtitle, hideActionBar);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public float getTextScale() { return (float)(double) textScale.getValue(); }
    public float getPosY(int height) { return height * (float)(double) posY.getValue() / 100f; }
    public boolean isTitleHidden() { return hideTitle.getValue(); }
    public boolean isSubtitleHidden() { return hideSubtitle.getValue(); }
    public boolean isActionBarHidden() { return hideActionBar.getValue(); }
}
