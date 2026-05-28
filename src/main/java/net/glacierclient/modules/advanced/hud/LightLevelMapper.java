package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.world.LightType;

public class LightLevelMapper extends HUDMod {

    private final NumberSetting spawnThreshold = new NumberSetting("Spawn Threshold", "Light level below which mobs spawn", 0, 15, 7);
    private final BooleanSetting colorCode = new BooleanSetting("Color Code", "Color code light level", true);
    private final ColorSetting safeColor = new ColorSetting("Safe Color", "Color when safe", 0xFF43B581);
    private final ColorSetting dangerColor = new ColorSetting("Danger Color", "Color when mobs can spawn", 0xFFF04747);

    private int lightLevel = 0;
    private int skyLight = 0;
    private int blockLight = 0;

    public LightLevelMapper() {
        super("Light Level", "Shows light level and mob spawn warning", 160, 20);
        addSettings(spawnThreshold, colorCode, safeColor, dangerColor);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        var pos = mc.player.getBlockPos();
        skyLight = mc.world.getLightLevel(LightType.SKY, pos);
        blockLight = mc.world.getLightLevel(LightType.BLOCK, pos);
        lightLevel = mc.world.getLightLevel(pos);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        var tr = MinecraftClient.getInstance().textRenderer;
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        boolean danger = lightLevel <= (int) spawnThreshold.getValue();
        int color = colorCode.getValue() ? (danger ? dangerColor.getValue() : safeColor.getValue()) : GlacierTheme.TEXT;
        String text = "Light: " + lightLevel + " (sky:" + skyLight + " blk:" + blockLight + ")";
        if (danger) text += " SPAWN!";
        context.drawText(tr, text, x + 4, y + 6, color, false);
    }
}
