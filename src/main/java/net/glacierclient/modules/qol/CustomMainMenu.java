package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.StringSetting;

public class CustomMainMenu extends GlacierMod {

    private final BooleanSetting showBackground = new BooleanSetting("Show Background", "Show custom background", true);
    private final BooleanSetting showSplash = new BooleanSetting("Show Splash", "Show splash text", true);
    private final StringSetting splashText = new StringSetting("Splash Text", "Custom splash text", "Glacier Client");
    private final BooleanSetting showVersion = new BooleanSetting("Show Version", "Show client version", true);

    public CustomMainMenu() {
        super("Custom Main Menu", "Customize the main menu appearance", Category.QOL);
        addSettings(showBackground, showSplash, splashText, showVersion);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isShowBackground() { return showBackground.getValue(); }
    public boolean isShowSplash() { return showSplash.getValue(); }
    public String getSplashText() { return splashText.getValue(); }
    public boolean isShowVersion() { return showVersion.getValue(); }
}
