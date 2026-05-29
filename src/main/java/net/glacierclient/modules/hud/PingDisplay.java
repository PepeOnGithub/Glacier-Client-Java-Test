package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;

public class PingDisplay extends HUDMod {

    private final ModeSetting style = new ModeSetting("Style", "Display style", "Simple", "Simple", "Colored", "Bar");
    private final BooleanSetting showPingText = new BooleanSetting("Show Text", "Show ping label text", true);

    public PingDisplay() {
        super("Ping Display", "Shows current server ping", 90, 20);
        addSettings(style, showPingText);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    private int getPing() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayNetworkHandler handler = mc.getNetworkHandler();
        if (handler == null || mc.player == null) return -1;
        PlayerListEntry entry = handler.getPlayerListEntry(mc.player.getUuid());
        return entry != null ? entry.getLatency() : -1;
    }

    @Override
    public void render(DrawContext context, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null) return;
        int ping = getPing();
        if (ping < 0) return;
        int color;
        if ("Colored".equals(style.getValue()) || "Bar".equals(style.getValue())) {
            if (ping < 80) color = GlacierTheme.GREEN;
            else if (ping < 150) color = 0xFFFAA61A;
            else color = GlacierTheme.RED;
        } else {
            color = GlacierTheme.TEXT;
        }
        String label = showPingText.getValue() ? "Ping: " + ping + "ms" : ping + "ms";
        context.drawText(mc.textRenderer, label, getX() + 2, getY() + 4, color, false);
        if ("Bar".equals(style.getValue())) {
            int barWidth = getWidth() - 4;
            int filled = Math.max(0, barWidth - (ping / 5));
            context.fill(getX() + 2, getY() + 14, getX() + 2 + barWidth, getY() + 17, GlacierTheme.BG_PANEL);
            context.fill(getX() + 2, getY() + 14, getX() + 2 + filled, getY() + 17, color);
        }
    }
}
