package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;

public class WeightSpeedHUD extends HUDMod {

    private final BooleanSetting showWeight = new BooleanSetting("Show Weight", "Show inventory weight", true);
    private final BooleanSetting showSpeed = new BooleanSetting("Show Speed", "Show movement speed", true);
    private final BooleanSetting showJump = new BooleanSetting("Show Jump", "Show jump height", true);
    private final ModeSetting style = new ModeSetting("Style", "Display style", new String[]{"Bars", "Numbers"}, "Numbers");

    private float speed = 0;
    private float jumpStrength = 0;
    private int weight = 0;

    public WeightSpeedHUD() {
        super("Weight/Speed", "Distribution of weight and speed stats", 160, 30);
        addSettings(showWeight, showSpeed, showJump, style);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        var attr = mc.player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        speed = attr != null ? (float)(double) attr.getValue() * 43.178f : 0;
        jumpStrength = 1.0f; // simplified
        weight = 0;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (!s.isEmpty()) weight += s.getCount();
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        var tr = MinecraftClient.getInstance().textRenderer;
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        int lineY = y + 4;
        if (showSpeed.getValue()) {
            context.drawText(tr, "Speed: " + String.format("%.2f", speed) + " b/s", x + 4, lineY, GlacierTheme.TEXT, false);
            lineY += 10;
        }
        if (showWeight.getValue()) {
            context.drawText(tr, "Items: " + weight, x + 4, lineY, GlacierTheme.TEXT_DIM, false);
            lineY += 10;
        }
        if (showJump.getValue()) {
            context.drawText(tr, "Jump: " + String.format("%.1f", jumpStrength), x + 4, lineY, GlacierTheme.TEXT_DIM, false);
        }
    }
}
