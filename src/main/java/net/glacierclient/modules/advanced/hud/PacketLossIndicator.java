package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import java.util.ArrayDeque;
import java.util.Deque;

public class PacketLossIndicator extends HUDMod {

    private final NumberSetting warnThreshold = new NumberSetting("Warn Threshold", "% packet loss to warn", 1, 50, 5);
    private final ColorSetting normalColor = new ColorSetting("Normal Color", "Color when no loss", 0xFF43B581);
    private final ColorSetting warnColor = new ColorSetting("Warn Color", "Color when losing packets", 0xFFFAA61A);
    private final ColorSetting critColor = new ColorSetting("Crit Color", "Color when critical loss", 0xFFF04747);

    private final Deque<Boolean> packetLog = new ArrayDeque<>();
    private float lossPercent = 0;
    private long lastExpected = System.currentTimeMillis();
    private int sentCount = 0;
    private int receivedCount = 0;

    public PacketLossIndicator() {
        super("Packet Loss", "Shows estimated packet loss percentage", 120, 20);
        addSettings(warnThreshold, normalColor, warnColor, critColor);
    }

    @Override public void onEnable() { packetLog.clear(); lossPercent = 0; }
    @Override public void onDisable() { packetLog.clear(); }

    @Override
    public void onTick() {
        // Track sent packets (each tick = 1 expected)
        sentCount++;
        // Simulate packet tracking; real impl hooks into network layer
        long now = System.currentTimeMillis();
        if (now - lastExpected > 50) {
            boolean received = (now - lastExpected) < 100;
            packetLog.addLast(received);
            if (packetLog.size() > 100) packetLog.pollFirst();
            lastExpected = now;
        }
        if (!packetLog.isEmpty()) {
            long lost = packetLog.stream().filter(b -> !b).count();
            lossPercent = (lost * 100f) / packetLog.size();
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        var tr = MinecraftClient.getInstance().textRenderer;
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        int warn = (int) warnThreshold.getValue();
        int color = lossPercent == 0 ? normalColor.getValue()
            : lossPercent < warn ? normalColor.getValue()
            : lossPercent < warn * 3 ? warnColor.getValue()
            : critColor.getValue();
        context.drawText(tr, "Loss: " + String.format("%.1f%%", lossPercent), x + 4, y + 6, color, lossPercent >= warn);
    }
}
