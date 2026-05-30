package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

public class CoordinatesHUD extends HUDMod {

    private final BooleanSetting showBiome = new BooleanSetting("Show Biome", "Show current biome", true);
    private final BooleanSetting showDirection = new BooleanSetting("Show Direction", "Show facing direction", true);
    private final BooleanSetting showNether = new BooleanSetting("Show Nether", "Show nether coordinates", true);
    private final NumberSetting textSize = new NumberSetting("Text Size", "Size of the text", 8, 16, 10);

    public CoordinatesHUD() {
        super("Coordinates", "Shows X/Y/Z coordinates and more", 160, 50);
        addSettings(showBiome, showDirection, showNether, textSize);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    private String getDirection(float yaw) {
        int d = (int)((yaw % 360 + 360) % 360 / 45);
        String[] dirs = {"S","SW","W","NW","N","NE","E","SE"};
        return dirs[d % 8];
    }

    @Override
    public void render(DrawContext context, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null || mc.player == null) return;
        int x = getX() + 2;
        int y = getY() + 2;
        int lineH = 10;
        double px = mc.player.getX();
        double py = mc.player.getY();
        double pz = mc.player.getZ();
        int dim = (getTextColor() & 0xFFFFFF) | 0xAA000000; // dimmed variant of the chosen colour
        boolean sh = hasShadow();
        // background sized to the visible lines
        int lines = 1 + (showDirection.getValue() ? 1 : 0) + (showNether.getValue() ? 1 : 0) + (showBiome.getValue() ? 1 : 0);
        drawBackground(context, x, y, 150, lines * lineH);
        context.drawText(mc.textRenderer, String.format("X: %.1f  Y: %.1f  Z: %.1f", px, py, pz), x, y, getTextColor(), sh);
        y += lineH;
        if (showDirection.getValue()) {
            String dir = getDirection(mc.player.getYaw());
            context.drawText(mc.textRenderer, "Facing: " + dir + String.format(" (%.1f)", mc.player.getYaw()), x, y, dim, sh);
            y += lineH;
        }
        if (showNether.getValue() && mc.world != null) {
            boolean isNether = mc.world.getRegistryKey() == World.NETHER;
            String label = isNether ? "Overworld" : "Nether";
            double nx = isNether ? px * 8 : px / 8;
            double nz = isNether ? pz * 8 : pz / 8;
            context.drawText(mc.textRenderer, label + ": " + (int)nx + ", " + (int)nz, x, y, dim, sh);
            y += lineH;
        }
        if (showBiome.getValue() && mc.world != null) {
            BlockPos bp = mc.player.getBlockPos();
            String biome = mc.world.getBiome(bp).getKey().map(k -> k.getValue().getPath()).orElse("unknown");
            context.drawText(mc.textRenderer, "Biome: " + biome, x, y, dim, sh);
        }
    }
}
