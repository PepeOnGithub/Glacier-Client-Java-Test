package net.glacierclient.modules.expanded.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;

public class InGameNotepad extends GlacierMod {

    private final BooleanSetting persistAcrossSessions = new BooleanSetting("Persist Sessions", "Save notepad content across play sessions", false);
    private final NumberSetting width = new NumberSetting("Width", "Notepad overlay width in pixels", 300, 100, 500);
    private final NumberSetting height = new NumberSetting("Height", "Notepad overlay height in pixels", 200, 80, 400);
    private final ColorSetting bgColor = new ColorSetting("Background Color", "Notepad background color", 0xCC2C2F33);

    public InGameNotepad() {
        super("In-Game Notepad", "Draggable resizable text editor with profile saving", Category.QOL);
        addSettings(persistAcrossSessions, width, height, bgColor);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public boolean isPersistAcrossSessions() { return persistAcrossSessions.getValue(); }
    public int getWidth() { return (int) width.getValue(); }
    public int getHeight() { return (int) height.getValue(); }
    public int getBgColor() { return bgColor.getValue(); }
}
