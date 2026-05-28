package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.StringSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class EconomyTracker extends HUDMod {

    private final StringSetting coinsPrefix = new StringSetting("Coins Prefix", "Chat prefix for coins", "Coins: ");
    private final BooleanSetting showDelta = new BooleanSetting("Show Delta", "Show coins gained/lost this session", true);
    private final BooleanSetting sessionOnly = new BooleanSetting("Session Only", "Track only since login", true);

    private double currentCoins = 0;
    private double sessionStart = 0;
    private double sessionDelta = 0;
    private boolean started = false;

    public EconomyTracker() {
        super("Economy Tracker", "Tracks coins/money from server chat messages", 160, 60);
        addSettings(coinsPrefix, showDelta, sessionOnly);
    }

    @Override public void onEnable() { started = false; sessionDelta = 0; }
    @Override public void onDisable() {}
    @Override public void onTick() {}

    public void onChatMessage(String message) {
        String prefix = coinsPrefix.getValue();
        int idx = message.indexOf(prefix);
        if (idx < 0) return;
        try {
            String numStr = message.substring(idx + prefix.length()).split("[^0-9.]")[0];
            double parsed = Double.parseDouble(numStr);
            if (!started) { sessionStart = parsed; started = true; }
            sessionDelta = parsed - sessionStart;
            currentCoins = parsed;
        } catch (NumberFormatException ignored) {}
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        var tr = MinecraftClient.getInstance().textRenderer;
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        context.drawText(tr, "Economy", x + 4, y + 4, GlacierTheme.ACCENT, true);
        context.drawText(tr, coinsPrefix.getValue() + String.format("%.0f", currentCoins), x + 4, y + 16, GlacierTheme.TEXT, false);
        if (showDelta.getValue()) {
            int deltaColor = sessionDelta >= 0 ? 0xFF43B581 : 0xFFF04747;
            String sign = sessionDelta >= 0 ? "+" : "";
            context.drawText(tr, "Session: " + sign + String.format("%.0f", sessionDelta), x + 4, y + 28, deltaColor, false);
        }
    }
}
