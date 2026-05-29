package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class BountyNotifier extends HUDMod {

    private final NumberSetting threshold = new NumberSetting("Threshold", "Minimum bounty to alert", 0, 100000, 1000);
    private final BooleanSetting soundAlert = new BooleanSetting("Sound Alert", "Play sound on bounty alert", true);
    private final ColorSetting alertColor = new ColorSetting("Alert Color", "Alert text color", 0xFFFAA61A);

    private String lastBountyPlayer = "";
    private double lastBountyAmount = 0;
    private long lastBountyTime = 0;
    private boolean active = false;

    public BountyNotifier() {
        super("Bounty Notifier", "Shows player bounty information from chat", 200, 40);
        addSettings(threshold, soundAlert, alertColor);
    }

    @Override public void onEnable() { active = false; }
    @Override public void onDisable() { active = false; }
    @Override public void onTick() {
        if (active && System.currentTimeMillis() - lastBountyTime > 8000) active = false;
    }

    public void onBountyDetected(String player, double amount) {
        if (amount >= threshold.getValue()) {
            lastBountyPlayer = player;
            lastBountyAmount = amount;
            lastBountyTime = System.currentTimeMillis();
            active = true;
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        var tr = MinecraftClient.getInstance().textRenderer;
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        if (!active) {
            context.drawText(tr, "Bounty: None", x + 4, y + 14, GlacierTheme.TEXT_DIM, false);
            return;
        }
        context.fill(x, y, x + w, y + 1, alertColor.getValue());
        context.drawText(tr, "Bounty on " + lastBountyPlayer + ": " + String.format("%.0f", lastBountyAmount),
            x + 4, y + 14, alertColor.getValue(), true);
    }
}
