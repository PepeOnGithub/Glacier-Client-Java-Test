package net.glacierclient.modules.expanded.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;

public class NearestPlayerArrow extends HUDMod {

    private final NumberSetting maxRange = new NumberSetting("Max Range", "Maximum detection range in blocks", 128, 16, 512);
    private final ColorSetting arrowColor = new ColorSetting("Arrow Color", "Color of the directional arrow", GlacierTheme.ACCENT);
    private final BooleanSetting showDistance = new BooleanSetting("Show Distance", "Display distance to nearest player", false);
    private final BooleanSetting showName = new BooleanSetting("Show Name", "Display name of nearest player", false);
    private final BooleanSetting onlyFriends = new BooleanSetting("Only Friends", "Only point to players on friends list", false);

    public NearestPlayerArrow() {
        super("Player Arrow", "Compass arrow pointing to nearest player", 60, 60);
        addSettings(maxRange, arrowColor, showDistance, showName, onlyFriends);
    }

    @Override
    public void render(DrawContext context, float delta) {
        int x = getX();
        int y = getY();
        int cx = x + 30;
        int cy = y + 30;
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity target = nearestPlayer(mc);

        drawBackground(context, x, y, 60, 60);
        if (mc.player == null || target == null) {
            context.drawCenteredTextWithShadow(mc.textRenderer, "--", cx, cy - 4, GlacierTheme.TEXT_DIM);
            return;
        }

        int col = arrowColor.getValue();
        double dx = target.getX() - mc.player.getX();
        double dz = target.getZ() - mc.player.getZ();
        float angle = (float) Math.toDegrees(Math.atan2(dz, dx)) - mc.player.getYaw() + 90f;
        context.getMatrices().push();
        context.getMatrices().translate(cx, cy, 0);
        context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(angle));
        context.drawCenteredTextWithShadow(mc.textRenderer, "^", 0, -7, col);
        context.getMatrices().pop();

        if (showDistance.getValue()) {
            context.drawCenteredTextWithShadow(mc.textRenderer, Math.round(mc.player.distanceTo(target)) + "m", cx, y + 48, GlacierTheme.TEXT_DIM);
        }
        if (showName.getValue()) {
            context.drawCenteredTextWithShadow(mc.textRenderer, target.getName().getString(), cx, y + 2, GlacierTheme.TEXT);
        }
    }

    private PlayerEntity nearestPlayer(MinecraftClient mc) {
        if (mc.world == null || mc.player == null || onlyFriends.getValue()) return null;
        PlayerEntity closest = null;
        double closestSq = maxRange.getValue() * maxRange.getValue();
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            double distSq = player.squaredDistanceTo(mc.player);
            if (distSq <= closestSq) {
                closestSq = distSq;
                closest = player;
            }
        }
        return closest;
    }
}
