package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.hud.HUDMod;
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
        boolean bars = style.getValue().equals("Bars");
        int lineY = y + 4;
        if (showSpeed.getValue()) {
            drawStat(context, tr, x, lineY, w, "Speed", String.format("%.2f b/s", speed), Math.min(1f, speed / 10f), GlacierTheme.ACCENT, bars);
            lineY += bars ? 13 : 10;
        }
        if (showWeight.getValue()) {
            drawStat(context, tr, x, lineY, w, "Items", String.valueOf(weight), Math.min(1f, weight / 360f), 0xFF7A8CA3, bars);
            lineY += bars ? 13 : 10;
        }
        if (showJump.getValue()) {
            drawStat(context, tr, x, lineY, w, "Jump", String.format("%.1f", jumpStrength), Math.min(1f, jumpStrength / 2f), 0xFF7A8CA3, bars);
        }
    }

    private void drawStat(DrawContext ctx, net.minecraft.client.font.TextRenderer tr, int x, int yy, int w, String label, String value, float pct, int col, boolean bars) {
        ctx.drawText(tr, label + ": " + value, x + 4, yy, GlacierTheme.TEXT, false);
        if (bars) {
            int by = yy + 9, bw = w - 8;
            ctx.fill(x + 4, by, x + 4 + bw, by + 2, 0x44FFFFFF);
            ctx.fill(x + 4, by, x + 4 + (int)(bw * pct), by + 2, col);
        }
    }
}
