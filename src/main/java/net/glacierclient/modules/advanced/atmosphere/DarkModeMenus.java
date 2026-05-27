package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;

public class DarkModeMenus extends GlacierMod {

    private final ColorSetting backgroundColor = new ColorSetting("BG Color", "Menu background color", 0xCC23272A);
    private final ColorSetting textColor = new ColorSetting("Text Color", "Menu text color", 0xFFFFFFFF);
    private final BooleanSetting affectChest = new BooleanSetting("Affect Chest", "Apply to chest GUI", true);
    private final BooleanSetting affectCrafting = new BooleanSetting("Affect Crafting", "Apply to crafting GUI", true);

    public DarkModeMenus() {
        super("Dark Mode Menus", "Apply dark theme to all vanilla GUI screens", Category.RENDER);
        addSettings(backgroundColor, textColor, affectChest, affectCrafting);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
    @Override public void onTick() {}

    public int getBackgroundColor() { return backgroundColor.getValue(); }
    public int getTextColor() { return textColor.getValue(); }
    public boolean shouldAffectChest() { return affectChest.getValue(); }
    public boolean shouldAffectCrafting() { return affectCrafting.getValue(); }
}
