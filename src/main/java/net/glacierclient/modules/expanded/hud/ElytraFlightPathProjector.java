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
        context.drawTextWithShadow(mc.textRenderer,
            "ETA: " + (int) lookAhead.getValue() + "s",
            x + 4, y + 5,
            lineColor.getValue());
    }
}
