package net.glacierclient.modules.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EquipmentSlot;

public class TargetHUD extends HUDMod {

    private final ModeSetting style = new ModeSetting("Style", "Display style", "Full", "Simple", "Full", "Minimal");
    private final BooleanSetting showArmor = new BooleanSetting("Show Armor", "Show target armor", true);
    private final BooleanSetting showHealth = new BooleanSetting("Show Health", "Show target health", true);
    private final ColorSetting healthColor = new ColorSetting("Health Color", "Color of health bar", GlacierTheme.GREEN);

    public TargetHUD() {
        super("Target HUD", "Shows info about targeted entity", 180, 50);
        addSettings(style, showArmor, showHealth, healthColor);
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
        if (mc.textRenderer == null || mc.targetedEntity == null) return;
        if (!(mc.targetedEntity instanceof LivingEntity target)) return;
        int x = getX() + 2, y = getY() + 2;
        String name = target.getName().getString();
        context.drawText(mc.textRenderer, name, x, y, GlacierTheme.TEXT, false);
        y += 10;
        if (showHealth.getValue()) {
            float hp = target.getHealth();
            float maxHp = target.getMaxHealth();
            int barW = getWidth() - 4;
            int filled = maxHp > 0 ? (int)(hp / maxHp * barW) : 0;
            context.fill(x, y, x + barW, y + 6, GlacierTheme.BG_PANEL);
            context.fill(x, y, x + filled, y + 6, healthColor.getValue());
            if ("Full".equals(style.getValue())) {
                context.drawText(mc.textRenderer, String.format("%.1f / %.1f", hp, maxHp), x, y + 8, GlacierTheme.TEXT_DIM, false);
            }
            y += 18;
        }
        if (showArmor.getValue() && target instanceof PlayerEntity player) {
            int armor = player.getArmor();
            context.drawText(mc.textRenderer, "Armor: " + armor, x, y, GlacierTheme.TEXT_DIM, false);
        }
    }
}
