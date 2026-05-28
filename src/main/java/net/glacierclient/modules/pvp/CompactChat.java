package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;

import java.util.LinkedHashMap;
import java.util.Map;

public class CompactChat extends GlacierMod {

    private final NumberSetting maxStack = new NumberSetting("Max Stack", "Max stacked duplicate messages", 2, 99, 99);
    private final BooleanSetting showCount = new BooleanSetting("Show Count", "Show stack count next to message", true);
    private final ColorSetting countColor = new ColorSetting("Count Color", "Color of stack count", GlacierTheme.ACCENT);

    private final LinkedHashMap<String, Integer> messageCounts = new LinkedHashMap<>();

    public CompactChat() {
        super("Compact Chat", "Stack repeated duplicate chat messages", Category.PVP);
        addSettings(maxStack, showCount, countColor);
    }

    @Override
    public void onEnable() { messageCounts.clear(); }

    @Override
    public void onDisable() { messageCounts.clear(); }

    @Override
    public void onTick() {}

    public boolean shouldShowMessage(String msg) {
        int count = messageCounts.getOrDefault(msg, 0);
        if (count >= (int) maxStack.getValue()) return false;
        messageCounts.put(msg, count + 1);
        return true;
    }

    public int getCount(String msg) { return messageCounts.getOrDefault(msg, 1); }
    public int getCountColor() { return countColor.getValue(); }
    public boolean isShowCount() { return showCount.getValue(); }
}
