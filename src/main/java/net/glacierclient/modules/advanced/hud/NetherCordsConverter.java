package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetherCordsConverter extends HUDMod {

    private final BooleanSetting showBoth = new BooleanSetting("Show Both", "Show both dimension coords", true);
    private final BooleanSetting alwaysShow = new BooleanSetting("Always Show", "Show even outside nether", false);

    public NetherCordsConverter() {
        super("Nether Converter", "Shows nether/overworld coordinate conversion", 200, 40);
        addSettings(showBoth, alwaysShow);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
    @Override public void onTick() {}

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        var tr = MinecraftClient.getInstance().textRenderer;
        MinecraftClient mc = MinecraftClient.getInstance();
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        if (mc.player == null || mc.world == null) {
            context.drawText(tr, "No World", x + 4, y + 14, GlacierTheme.TEXT_DIM, false);
            return;
        }
        BlockPos pos = mc.player.getBlockPos();
        RegistryKey<World> dim = mc.world.getRegistryKey();
        boolean inNether = dim == World.NETHER;
        boolean inOverworld = dim == World.OVERWORLD;
        if (!alwaysShow.getValue() && !inNether && !inOverworld) {
            context.drawText(tr, "Not applicable", x + 4, y + 14, GlacierTheme.TEXT_DIM, false);
            return;
        }
        if (inNether) {
            int owX = pos.getX() * 8, owZ = pos.getZ() * 8;
            context.drawText(tr, "Nether: " + pos.getX() + ", " + pos.getZ(), x + 4, y + 4, 0xFFFF5555, false);
            context.drawText(tr, "Overworld: " + owX + ", " + owZ, x + 4, y + 14, 0xFF55FF55, false);
        } else {
            int nX = pos.getX() / 8, nZ = pos.getZ() / 8;
            context.drawText(tr, "Overworld: " + pos.getX() + ", " + pos.getZ(), x + 4, y + 4, 0xFF55FF55, false);
            if (showBoth.getValue()) {
                context.drawText(tr, "Nether: " + nX + ", " + nZ, x + 4, y + 14, 0xFFFF5555, false);
            }
        }
    }
}
