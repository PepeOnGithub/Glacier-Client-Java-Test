package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EncounterHistory extends HUDMod {

    private final NumberSetting maxEntries = new NumberSetting("Max Entries", "Maximum encounters shown", 5, 20, 10);
    private final NumberSetting clearAfter = new NumberSetting("Clear After", "Clear entries after N seconds", 60, 3600, 300);
    private final BooleanSetting showTime = new BooleanSetting("Show Time", "Show encounter timestamp", true);

    private static class Encounter {
        String playerName;
        String result;
        long timestamp;
        Encounter(String name, String result) {
            this.playerName = name;
            this.result = result;
            this.timestamp = System.currentTimeMillis();
        }
    }

    private final List<Encounter> encounters = new ArrayList<>();
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public EncounterHistory() {
        super("Encounter History", "List of recent PvP encounters", 200, 100);
        addSettings(maxEntries, clearAfter, showTime);
    }

    @Override public void onEnable() { encounters.clear(); }
    @Override public void onDisable() { encounters.clear(); }

    @Override
    public void onTick() {
        long now = System.currentTimeMillis();
        long clearMs = (long) (clearAfter.getValue() * 1000);
        encounters.removeIf(e -> (now - e.timestamp) > clearMs);
        int max = (int) maxEntries.getValue();
        while (encounters.size() > max) encounters.remove(0);
    }

    public void addEncounter(String playerName, String result) {
        encounters.add(0, new Encounter(playerName, result));
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        var tr = MinecraftClient.getInstance().textRenderer;
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        context.drawText(tr, "Encounters", x + 4, y + 4, GlacierTheme.ACCENT, true);
        int lineY = y + 16;
        for (int i = 0; i < Math.min(encounters.size(), (int) maxEntries.getValue()); i++) {
            Encounter e = encounters.get(i);
            if (lineY + 10 > y + h) break;
            int color = "Kill".equals(e.result) ? 0xFF43B581 : "Death".equals(e.result) ? 0xFFF04747 : GlacierTheme.TEXT;
            String line = e.playerName + " - " + e.result;
            if (showTime.getValue()) {
                LocalTime t = LocalTime.ofSecondOfDay((e.timestamp / 1000) % 86400);
                line += " [" + t.format(TIME_FMT) + "]";
            }
            context.drawText(tr, line, x + 4, lineY, color, true);
            lineY += 10;
        }
        if (encounters.isEmpty()) {
            context.drawText(tr, "No encounters", x + 4, lineY, GlacierTheme.TEXT_DIM, true);
        }
    }
}
