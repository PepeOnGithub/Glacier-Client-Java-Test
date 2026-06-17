package net.glacierclient.modules.expanded.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ElytraFlightPathProjector extends HUDMod {

    private final BooleanSetting show = new BooleanSetting("Show", "Enable elytra trajectory projection", false);
    private final NumberSetting lookAhead = new NumberSetting("Look Ahead", "Seconds of trajectory to project", 20, 5, 50);
    private final ColorSetting lineColor = new ColorSetting("Line Color", "Projected path line color", GlacierTheme.ACCENT);
    private final NumberSetting lineWidth = new NumberSetting("Line Width", "Width of projection line", 2.0, 1.0, 4.0);
    private final BooleanSetting groundIndicator = new BooleanSetting("Ground Indicator", "Show landing point marker", false);

    public ElytraFlightPathProjector() {
        super("Elytra Projector", "Dotted trajectory line for elytra landings", 160, 20);
        addSettings(show, lookAhead, lineColor, lineWidth, groundIndicator);
    }

    @Override
    public void render(DrawContext context, float delta) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        MinecraftClient mc = MinecraftClient.getInstance();

        if (!show.getValue()) return;

        context.fill(x, y, x + w, y + h, 0xCC1E1E2E);

        // Sample trajectory line, rendered at the configured width.
        int lw = Math.max(1, (int) Math.round((double) lineWidth.getValue()));
        int ly = y + h - 4;
        context.fill(x + 4, ly - lw / 2, x + w - 4, ly + (lw + 1) / 2, lineColor.getValue());

        String t = "ETA: " + (int)(double) lookAhead.getValue() + "s";
        if (groundIndicator.getValue() && mc.player != null) {
            double hs = Math.hypot(mc.player.getVelocity().x, mc.player.getVelocity().z);
            int dist = (int) (hs * 20 * (double) lookAhead.getValue());
            t += "  Land ~" + dist + "m";
        }
        context.drawTextWithShadow(mc.textRenderer, t, x + 4, y + 5, lineColor.getValue());
    }
}
