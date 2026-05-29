package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlayerRadar extends HUDMod {

    private final NumberSetting radius = new NumberSetting("Radius", "Detection radius in blocks", 16, 256, 64);
    private final BooleanSetting showDistance = new BooleanSetting("Show Distance", "Show distance to players", true);
    private final BooleanSetting showFriends = new BooleanSetting("Show Friends", "Highlight friends", true);
    private final BooleanSetting hideTeam = new BooleanSetting("Hide Team", "Hide team members", false);

    private final List<String> nearbyPlayers = new ArrayList<>();
    private final List<Double> playerDistances = new ArrayList<>();

    public PlayerRadar() {
        super("Player Radar", "Nearby players list in render distance", 120, 60);
        addSettings(radius, showDistance, showFriends, hideTeam);
    }

    @Override public void onEnable() { nearbyPlayers.clear(); }
    @Override public void onDisable() { nearbyPlayers.clear(); }

    @Override
    public void onTick() {
        nearbyPlayers.clear();
        playerDistances.clear();
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        double rad = radius.getValue();
        List<PlayerEntity> players = new ArrayList<>(mc.world.getPlayers());
        players.sort(Comparator.comparingDouble(p -> p.squaredDistanceTo(mc.player)));
        for (PlayerEntity p : players) {
            if (p == mc.player) continue;
            double dist = p.distanceTo(mc.player);
            if (dist <= rad) {
                nearbyPlayers.add(p.getName().getString());
                playerDistances.add(dist);
            }
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        var tr = MinecraftClient.getInstance().textRenderer;
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        context.drawText(tr, "Players: " + nearbyPlayers.size(), x + 4, y + 4, GlacierTheme.ACCENT, true);
        int lineY = y + 16;
        for (int i = 0; i < nearbyPlayers.size() && lineY + 10 <= y + h; i++) {
            String name = nearbyPlayers.get(i);
            String text = showDistance.getValue()
                ? name + " " + String.format("%.0fm", playerDistances.get(i))
                : name;
            context.drawText(tr, text, x + 4, lineY, GlacierTheme.TEXT, false);
            lineY += 10;
        }
    }
}
