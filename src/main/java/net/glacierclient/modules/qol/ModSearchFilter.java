package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

import java.util.List;
import java.util.stream.Collectors;

public class ModSearchFilter extends GlacierMod {

    private final BooleanSetting fuzzySearch = new BooleanSetting("Fuzzy Search", "Use fuzzy string matching", true);
    private final BooleanSetting searchDescription = new BooleanSetting("Search Description", "Also search mod descriptions", true);
    private final BooleanSetting recentFirst = new BooleanSetting("Recent First", "Show recently used mods first", false);
    private final NumberSetting maxResults = new NumberSetting("Max Results", "Maximum search results", 5, 50, 20);

    public ModSearchFilter() {
        super("Mod Search Filter", "Filter and search through modules quickly", Category.QOL);
        addSettings(fuzzySearch, searchDescription, recentFirst, maxResults);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public List<GlacierMod> filter(List<GlacierMod> mods, String query) {
        if (query == null || query.isEmpty()) return mods;
        String q = query.toLowerCase();
        return mods.stream()
            .filter(m -> {
                boolean nameMatch = fuzzySearch.getValue() ? fuzzyMatch(m.getName().toLowerCase(), q) : m.getName().toLowerCase().contains(q);
                boolean descMatch = searchDescription.getValue() && m.getDescription().toLowerCase().contains(q);
                return nameMatch || descMatch;
            })
            .limit((int)(double) maxResults.getValue())
            .collect(Collectors.toList());
    }

    private boolean fuzzyMatch(String text, String pattern) {
        int ti = 0, pi = 0;
        while (ti < text.length() && pi < pattern.length()) {
            if (text.charAt(ti) == pattern.charAt(pi)) pi++;
            ti++;
        }
        return pi == pattern.length();
    }
}
