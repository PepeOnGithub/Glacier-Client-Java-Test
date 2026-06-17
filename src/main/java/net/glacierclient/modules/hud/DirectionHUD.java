package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class DirectionHUD extends HUDMod {

    private final ModeSetting style = new ModeSetting("Style", "Compass display style", "Both", "Compass", "Letters", "Both");
    private final BooleanSetting showDegrees = new BooleanSetting("Show Degrees", "Show yaw in degrees", false);
    private final NumberSetting compassWidth = new NumberSetting("Compass Width", "Width of the compass tape", 80, 200, 160);

    private static final String[] DIRS = {"N","NE","E","SE","S","SW","W","NW"};

    public DirectionHUD() {
        super("Direction HUD", "Compass showing N/S/E/W directions", 160, 20);
        addSettings(style, showDegrees, compassWidth);
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
        if (mc.textRenderer == null || mc.player == null) return;
        float yaw = (mc.player.getYaw() % 360 + 360) % 360;
        int x = getX(), y = getY();
        int cw = (int)(double) compassWidth.getValue();
        String mode = style.getValue();
        boolean compass = mode.equals("Compass") || mode.equals("Both");
        boolean letters = mode.equals("Letters") || mode.equals("Both");
        if (compass) {
            // Draw background bar
            context.fill(x, y, x + cw, y + 16, GlacierTheme.BG_PANEL);
            // Draw cardinal markers
            for (int i = 0; i < 8; i++) {
                float markerYaw = i * 45f;
                float diff = ((markerYaw - yaw + 360 + 180) % 360) - 180;
                int markerX = x + cw / 2 + (int)(diff / 180f * cw / 2);
                if (markerX > x && markerX < x + cw) {
                    int col = (i % 2 == 0) ? getTextColor() : GlacierTheme.TEXT_DIM;
                    context.drawText(mc.textRenderer, DIRS[i], markerX - mc.textRenderer.getWidth(DIRS[i]) / 2, y + 4, col, hasShadow());
                }
            }
            // Center indicator
            context.fill(x + cw / 2, y, x + cw / 2 + 1, y + 16, GlacierTheme.ACCENT);
        }
        if (letters) {
            // Large facing label centred (or left when no compass tape is drawn)
            int idx = Math.round(yaw / 45f) % 8;
            String facing = DIRS[(idx + 8) % 8];
            int lx = compass ? x + cw / 2 - mc.textRenderer.getWidth(facing) / 2 : x;
            int ly = compass ? y + 18 : y + 4;
            context.drawText(mc.textRenderer, facing, lx, ly, getTextColor(), hasShadow());
        }
        if (showDegrees.getValue()) {
            String deg = String.format("%.0f", yaw) + "°";
            int dx = compass ? x + cw + 4 : x + mc.textRenderer.getWidth("WNW") + 6;
            context.drawText(mc.textRenderer, deg, dx, y + 4, GlacierTheme.TEXT_DIM, hasShadow());
        }
    }
}
