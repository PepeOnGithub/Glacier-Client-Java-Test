package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;
import java.util.HashMap;
import java.util.Map;

public class FactionMapOverlay extends HUDMod {

    private final NumberSetting gridSize = new NumberSetting("Grid Size", "Grid cell size in blocks", 8, 64, 16);
    private final NumberSetting opacity = new NumberSetting("Opacity", "Overlay opacity", 0, 255, 120);
    private final BooleanSetting showCoords = new BooleanSetting("Show Coords", "Show chunk coordinates", true);
    private final BooleanSetting showNames = new BooleanSetting("Show Names", "Show territory names", true);

    private final Map<Long, Integer> territoryColors = new HashMap<>();
    private final Map<Long, String> territoryNames = new HashMap<>();

    public FactionMapOverlay() {
        super("Faction Map", "Client-side faction territory grid overlay", 200, 200);
        addSettings(gridSize, opacity, showCoords, showNames);
    }

    @Override public void onEnable() { territoryColors.clear(); territoryNames.clear(); }
    @Override public void onDisable() { territoryColors.clear(); }
    @Override public void onTick() {}

    public void setTerritory(long chunkKey, int color, String name) {
        territoryColors.put(chunkKey, color);
        if (name != null) territoryNames.put(chunkKey, name);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        int alpha = (int)(double) opacity.getValue();
        int bg = (alpha << 24) | 0x1A1A2E;
        context.fill(x, y, x + w, y + h, bg);
        context.fill(x, y, x + w, y + 1, GlacierTheme.ACCENT);
        context.fill(x, y + h - 1, x + w, y + h, GlacierTheme.ACCENT);
        context.fill(x, y, x + 1, y + h, GlacierTheme.ACCENT);
        context.fill(x + w - 1, y, x + w, y + h, GlacierTheme.ACCENT);

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        BlockPos playerPos = mc.player.getBlockPos();
        int cellSize = (int)(double) gridSize.getValue();
        int cellsX = w / cellSize;
        int cellsZ = h / cellSize;
        int startChunkX = (playerPos.getX() >> 4) - cellsX / 2;
        int startChunkZ = (playerPos.getZ() >> 4) - cellsZ / 2;

        var tr = mc.textRenderer;
        for (int cz = 0; cz < cellsZ; cz++) {
            for (int cx = 0; cx < cellsX; cx++) {
                int chunkX = startChunkX + cx;
                int chunkZ = startChunkZ + cz;
                long key = ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
                int rx = x + cx * cellSize;
                int ry = y + cz * cellSize;
                Integer color = territoryColors.get(key);
                if (color != null) {
                    int fillColor = ((alpha / 2) << 24) | (color & 0x00FFFFFF);
                    context.fill(rx, ry, rx + cellSize, ry + cellSize, fillColor);
                    context.fill(rx, ry, rx + cellSize, ry + 1, (alpha << 24) | (color & 0x00FFFFFF));
                    context.fill(rx, ry, rx + 1, ry + cellSize, (alpha << 24) | (color & 0x00FFFFFF));
                }
                if (showCoords.getValue() && cellSize >= 16) {
                    context.drawText(tr, chunkX + "," + chunkZ, rx + 1, ry + 1, 0x88FFFFFF, false);
                }
                if (showNames.getValue() && territoryNames.containsKey(key) && cellSize >= 24) {
                    context.drawText(tr, territoryNames.get(key), rx + 2, ry + 10, GlacierTheme.TEXT, false);
                }
            }
        }
        // Player marker
        int px = x + (cellsX / 2) * cellSize + cellSize / 2;
        int pz = y + (cellsZ / 2) * cellSize + cellSize / 2;
        context.fill(px - 2, pz - 2, px + 2, pz + 2, 0xFFFFFFFF);
    }
}
