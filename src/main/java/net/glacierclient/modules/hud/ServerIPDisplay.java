package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ServerIPDisplay extends HUDMod {

    private final BooleanSetting hideForRecording = new BooleanSetting("Hide For Recording", "Mask IP when recording", false);
    private final ModeSetting style = new ModeSetting("Style", "How to display the IP", "Full", "Full", "Domain", "Masked");

    public ServerIPDisplay() {
        super("Server IP", "Shows current server address", 160, 20);
        addSettings(hideForRecording, style);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    @Override
    public void render(DrawContext context, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null) return;
        String ip = mc.getCurrentServerEntry() != null ? mc.getCurrentServerEntry().address : "Singleplayer";
        String display;
        if (hideForRecording.getValue() || "Masked".equals(style.getValue())) {
            display = "***.***.***.***";
        } else if ("Domain".equals(style.getValue())) {
            String[] parts = ip.split(":");
            String host = parts[0];
            String[] domainParts = host.split("\\.");
            if (domainParts.length >= 2) display = domainParts[domainParts.length - 2] + "." + domainParts[domainParts.length - 1];
            else display = host;
        } else {
            display = ip;
        }
        context.drawText(mc.textRenderer, "IP: " + display, getX() + 2, getY() + 4, GlacierTheme.TEXT, false);
    }
}
