package net.glacierclient.modules.engine;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class GlacierBrowser extends GlacierMod {

    private final BooleanSetting showOverlay = new BooleanSetting("Show Overlay", "Display the browser overlay in-game", false);
    private final NumberSetting width = new NumberSetting("Width", "Browser overlay width in pixels", 600, 200, 1200);
    private final NumberSetting height = new NumberSetting("Height", "Browser overlay height in pixels", 400, 150, 900);
    private final NumberSetting opacity = new NumberSetting("Opacity", "Overlay background opacity", 200, 50, 255);
    private final NumberSetting posX = new NumberSetting("Position X", "Horizontal position as screen percentage", 50, 0, 100);
    private final NumberSetting posY = new NumberSetting("Position Y", "Vertical position as screen percentage", 50, 0, 100);

    public GlacierBrowser() {
        super("Glacier Browser", "Sandboxed in-game web view for guides and player stats", Category.ENGINE);
        addSettings(showOverlay, width, height, opacity, posX, posY);
    }

    @Override
    public void onEnable() {
        // Initialize sandboxed web view
    }

    @Override
    public void onDisable() {
        // Destroy web view instance
    }

    public boolean isShowOverlay() { return showOverlay.getValue(); }
    public int getWidth() { return (int)(double) width.getValue(); }
    public int getHeight() { return (int)(double) height.getValue(); }
    public int getOpacity() { return (int)(double) opacity.getValue(); }
    public int getPosX() { return (int)(double) posX.getValue(); }
    public int getPosY() { return (int)(double) posY.getValue(); }
}
