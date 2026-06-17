package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class Speedometer extends HUDMod {

    private final ModeSetting unit = new ModeSetting("Unit", "Speed unit to display", "BPS", "BPS", "KPH", "MPH");
    private final BooleanSetting showVertical = new BooleanSetting("Show Vertical", "Show vertical speed", false);

    private double lastX, lastY, lastZ;
    private double hSpeed, vSpeed;

    public Speedometer() {
        super("Speedometer", "Shows player movement speed", 100, 20);
        addSettings(unit, showVertical);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        double cx = mc.player.getX(), cy = mc.player.getY(), cz = mc.player.getZ();
        double dx = cx - lastX, dy = cy - lastY, dz = cz - lastZ;
        hSpeed = Math.sqrt(dx * dx + dz * dz) * 20.0;
        vSpeed = dy * 20.0;
        lastX = cx; lastY = cy; lastZ = cz;
    }

    @Override
    public void render(DrawContext context, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null) return;
        double displaySpeed = hSpeed;
        String unitStr = unit.getValue();
        if ("KPH".equals(unitStr)) displaySpeed *= 3.6;
        else if ("MPH".equals(unitStr)) displaySpeed *= 2.237;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%.2f ", displaySpeed)).append(unitStr);
        if (showVertical.getValue()) sb.append(String.format(" V:%.2f", vSpeed));
        String text = sb.toString();
        drawBackground(context, getX() + 2, getY() + 4, mc.textRenderer.getWidth(text), 9);
        context.drawText(mc.textRenderer, text, getX() + 2, getY() + 4, getTextColor(), hasShadow());
    }
}
