package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import java.util.List;

public class MinimapModule extends HUDMod {

    private final NumberSetting zoom = new NumberSetting("Zoom", "Minimap zoom level", 1, 8, 4);
    private final BooleanSetting showPlayers = new BooleanSetting("Show Players", "Show players on map", true);
    private final BooleanSetting showWaypoints = new BooleanSetting("Show Waypoints", "Show waypoints", true);
    private final ModeSetting rotation = new ModeSetting("Rotation", "Map rotation mode", new String[]{"Fixed", "Player", "North"}, "Fixed");
    private final NumberSetting opacity = new NumberSetting("Opacity", "Map opacity", 100, 255, 200);

    private static class Waypoint { String name; int x, z; int color; }
    private final List<Waypoint> waypoints = new ArrayList<>();

    public MinimapModule() {
        super("Minimap", "Chunk-based minimap with waypoints", 128, 128);
        addSettings(zoom, showPlayers, showWaypoints, rotation, opacity);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
    @Override public void onTick() {}

    public void addWaypoint(String name, int x, int z, int color) {
        Waypoint wp = new Waypoint();
        wp.name = name; wp.x = x; wp.z = z; wp.color = color;
        waypoints.add(wp);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        MinecraftClient mc = MinecraftClient.getInstance();
        int alpha = (int)(double) opacity.getValue();
        context.fill(x, y, x + w, y + h, (alpha << 24) | 0x1A1A2E);
        // Border
        context.fill(x, y, x + w, y + 1, GlacierTheme.ACCENT);
        context.fill(x, y + h - 1, x + w, y + h, GlacierTheme.ACCENT);
        context.fill(x, y, x + 1, y + h, GlacierTheme.ACCENT);
        context.fill(x + w - 1, y, x + w, y + h, GlacierTheme.ACCENT);

        if (mc.player == null || mc.world == null) return;
        BlockPos playerPos = mc.player.getBlockPos();
        int z2 = (int)(double) zoom.getValue();
        // Draw grid lines
        int centerX = x + w / 2, centerZ = y + h / 2;
        for (int cx = centerX; cx < x + w; cx += 16 * z2) context.fill(cx, y, cx + 1, y + h, 0x22FFFFFF);
        for (int cx = centerX; cx >= x; cx -= 16 * z2) context.fill(cx, y, cx + 1, y + h, 0x22FFFFFF);
        for (int cz = centerZ; cz < y + h; cz += 16 * z2) context.fill(x, cz, x + w, cz + 1, 0x22FFFFFF);
        for (int cz = centerZ; cz >= y; cz -= 16 * z2) context.fill(x, cz, x + w, cz + 1, 0x22FFFFFF);

        // Show other players
        if (showPlayers.getValue()) {
            for (PlayerEntity p : mc.world.getPlayers()) {
                if (p == mc.player) continue;
                int dx = (int) ((p.getX() - mc.player.getX()) * z2);
                int dz = (int) ((p.getZ() - mc.player.getZ()) * z2);
                int px = centerX + dx, pz2 = centerZ + dz;
                if (px >= x && px < x + w && pz2 >= y && pz2 < y + h) {
                    context.fill(px - 2, pz2 - 2, px + 2, pz2 + 2, 0xFFFF5555);
                }
            }
        }
        // Waypoints
        if (showWaypoints.getValue()) {
            for (Waypoint wp : waypoints) {
                int dx = (int) ((wp.x - playerPos.getX()) * z2);
                int dz = (int) ((wp.z - playerPos.getZ()) * z2);
                int wx = centerX + dx, wz = centerZ + dz;
                if (wx >= x && wx < x + w && wz >= y && wz < y + h) {
                    context.fill(wx - 3, wz - 3, wx + 3, wz + 3, wp.color);
                }
            }
        }
        // Player dot
        context.fill(centerX - 3, centerZ - 3, centerX + 3, centerZ + 3, 0xFFFFFFFF);
        // Coords
        context.drawText(mc.textRenderer,
            playerPos.getX() + ", " + playerPos.getZ(),
            x + 2, y + 2, 0xAAFFFFFF, false);
    }
}
