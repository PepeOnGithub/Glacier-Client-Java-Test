package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

public class EntityCounter extends HUDMod {

    private final BooleanSetting showMobs = new BooleanSetting("Show Mobs", "Count nearby mobs", true);
    private final BooleanSetting showPlayers = new BooleanSetting("Show Players", "Count nearby players", true);
    private final BooleanSetting showItems = new BooleanSetting("Show Items", "Count nearby item entities", true);
    private final NumberSetting radius = new NumberSetting("Radius", "Detection radius", 16, 256, 64);

    private int mobCount = 0, playerCount = 0, itemCount = 0;

    public EntityCounter() {
        super("Entity Counter", "Breakdown of mobs/items/players nearby", 160, 80);
        addSettings(showMobs, showPlayers, showItems, radius);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}

    @Override
    public void onTick() {
        mobCount = 0; playerCount = 0; itemCount = 0;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        double rad = radius.getValue();
        for (Entity e : mc.world.getEntities()) {
            if (e.distanceTo(mc.player) > rad) continue;
            if (e instanceof MobEntity) mobCount++;
            else if (e instanceof PlayerEntity && e != mc.player) playerCount++;
            else if (e instanceof ItemEntity) itemCount++;
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        var tr = MinecraftClient.getInstance().textRenderer;
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        context.drawText(tr, "Entities", x + 4, y + 4, GlacierTheme.ACCENT, true);
        int lineY = y + 16;
        if (showMobs.getValue()) {
            context.drawText(tr, "Mobs: " + mobCount, x + 4, lineY, GlacierTheme.TEXT, false);
            lineY += 12;
        }
        if (showPlayers.getValue()) {
            context.drawText(tr, "Players: " + playerCount, x + 4, lineY, GlacierTheme.TEXT, false);
            lineY += 12;
        }
        if (showItems.getValue()) {
            context.drawText(tr, "Items: " + itemCount, x + 4, lineY, GlacierTheme.TEXT, false);
        }
    }
}
