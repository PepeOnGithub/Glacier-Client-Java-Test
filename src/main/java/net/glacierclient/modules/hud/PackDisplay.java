package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.resource.ResourcePackManager;

import java.util.stream.Collectors;

public class PackDisplay extends HUDMod {

    private final NumberSetting maxLength = new NumberSetting("Max Length", "Max characters to display", 10, 50, 30);
    private final BooleanSetting showVersion = new BooleanSetting("Show Version", "Show pack version", false);

    public PackDisplay() {
        super("Pack Display", "Shows active resource pack name", 160, 20);
        addSettings(maxLength, showVersion);
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
        ResourcePackManager rpm = mc.getResourcePackManager();
        String packName = rpm.getEnabledProfiles().stream()
            .map(p -> p.getDisplayName().getString())
            .filter(n -> !n.equals("Default"))
            .findFirst().orElse("Default");
        int maxLen = (int)(double) maxLength.getValue();
        if (packName.length() > maxLen) packName = packName.substring(0, maxLen - 3) + "...";
        context.drawText(mc.textRenderer, "Pack: " + packName, getX() + 2, getY() + 4, GlacierTheme.TEXT, false);
    }
}
