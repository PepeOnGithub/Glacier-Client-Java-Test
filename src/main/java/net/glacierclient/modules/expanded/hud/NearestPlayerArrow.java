package net.glacierclient.modules.expanded.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class NearestPlayerArrow extends HUDMod {

    private final NumberSetting maxRange = new NumberSetting("Max Range", "Maximum detection range in blocks", 128, 16, 512);
    private final ColorSetting arrowColor = new ColorSetting("Arrow Color", "Color of the directional arrow", GlacierTheme.ACCENT);
    private final BooleanSetting showDistance = new BooleanSetting("Show Distance", "Display distance to nearest player", false);
    private final BooleanSetting showName = new BooleanSetting("Show Name", "Display name of nearest player", false);
    private final BooleanSetting onlyFriends = new BooleanSetting("Only Friends", "Only point to players on friends list", false);

    public NearestPlayerArrow() {
        super("Player Arrow", "Compass arrow pointing to nearest player", 60, 60);
        addSettings(maxRange, arrowColor, showDistance, showName, onlyFriends);
    }

    @Override
    public void render(DrawContext context, float delta) {
        int x = getX();
        int y = getY();
        int cx = x + 30;
        int cy = y + 30;
        MinecraftClient mc = MinecraftClient.getInstance();

        context.fill(x, y, x + 60, y + 60, 0xCC1E1E2E);

        // Arrow pointing up as placeholder
        int col = arrowColor.getValue();
        context.fill(cx - 2, cy - 20, cx + 2, cy + 10, col);
        context.fill(cx - 8, cy - 12, cx + 8, cy - 8, col);

        if (showDistance.getValue()) {
            context.drawCenteredTextWithShadow(mc.textRenderer, "42m", cx, y + 48, GlacierTheme.TEXT_DIM);
        }
        if (showName.getValue()) {
            context.drawCenteredTextWithShadow(mc.textRenderer, "Player", cx, y + 2, GlacierTheme.TEXT);
        }
    }
}
