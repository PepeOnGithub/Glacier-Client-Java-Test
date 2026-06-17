package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ReachDisplay extends HUDMod {

    private final NumberSetting decimalPlaces = new NumberSetting("Decimal Places", "Number of decimal places", 1, 4, 2);
    private final BooleanSetting showOnHit = new BooleanSetting("Show On Hit", "Only show when hitting", false);

    private double lastReach = 0.0;
    private long lastHitTime = 0;

    public ReachDisplay() {
        super("Reach Display", "Shows last attack reach distance", 100, 20);
        addSettings(decimalPlaces, showOnHit);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public void onHit(double reach) {
        lastReach = reach;
        lastHitTime = System.currentTimeMillis();
    }

    @Override
    public void render(DrawContext context, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null) return;
        boolean show = !showOnHit.getValue() || (System.currentTimeMillis() - lastHitTime < 2000);
        if (!show) return;
        int dp = (int)(double) decimalPlaces.getValue();
        String fmt = "Reach: %." + dp + "f blocks";
        String text = String.format(fmt, lastReach);
        drawBackground(context, getX() + 2, getY() + 4, mc.textRenderer.getWidth(text), 9);
        context.drawText(mc.textRenderer, text, getX() + 2, getY() + 4, getTextColor(), hasShadow());
    }
}
