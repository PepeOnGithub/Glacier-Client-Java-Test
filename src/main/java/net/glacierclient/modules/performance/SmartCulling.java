package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class SmartCulling extends GlacierMod {

    private final BooleanSetting cullEntities = new BooleanSetting("Cull Entities", "Skip rendering entities out of view or range", true);
    private final BooleanSetting cullBehindWalls = new BooleanSetting("Cull Behind Walls", "Don't render entities behind walls", true);
    private final NumberSetting entityDistance = new NumberSetting("Entity Distance", "Max entity render distance (blocks)", 16, 128, 64);
    private final BooleanSetting cullPlayers = new BooleanSetting("Cull Players", "Apply entity culling to players", false);

    private final BooleanSetting limitTileEntities = new BooleanSetting("Limit Tile Entities", "Cap rendered tile entities", true);
    private final NumberSetting maxTileEntities = new NumberSetting("Max Tile Entities", "Max rendered tile entities", 1, 2000, 500);
    private final BooleanSetting skipChests = new BooleanSetting("Skip Chests", "Skip chest tile entities", false);

    private final BooleanSetting hideFarPlayers = new BooleanSetting("Hide Far Players", "Stop rendering distant players", false);
    private final NumberSetting playerHideDistance = new NumberSetting("Player Hide Distance", "Distance to hide players (blocks)", 16, 256, 128);
    private final BooleanSetting hideNametags = new BooleanSetting("Hide Nametags", "Hide player nametags at distance", true);

    private int renderedTiles = 0;

    public SmartCulling() {
        super("Smart Culling", "Unified entity, tile-entity and far-player culling", Category.PERFORMANCE);
        addSettings(cullEntities, cullBehindWalls, entityDistance, cullPlayers,
                limitTileEntities, maxTileEntities, skipChests,
                hideFarPlayers, playerHideDistance, hideNametags);
    }

    @Override
    public void onTick() {
        renderedTiles = 0;
    }

    public boolean shouldCull(Entity entity) {
        if (!cullEntities.getValue()) return false;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return false;
        if (!cullPlayers.getValue() && entity instanceof PlayerEntity) return false;
        return entity.getPos().distanceTo(mc.player.getPos()) > entityDistance.getValue();
    }

    public boolean cullsBehindWalls() {
        return cullEntities.getValue() && cullBehindWalls.getValue();
    }

    public boolean shouldRenderTile(boolean isChest) {
        if (!limitTileEntities.getValue()) return true;
        if (isChest && skipChests.getValue()) return false;
        if (renderedTiles >= maxTileEntities.getValue().intValue()) return false;
        renderedTiles++;
        return true;
    }

    public boolean shouldHidePlayer(PlayerEntity player) {
        if (!hideFarPlayers.getValue()) return false;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || player == mc.player) return false;
        return player.distanceTo(mc.player) > playerHideDistance.getValue().floatValue();
    }

    public boolean isHideNametags() {
        return hideFarPlayers.getValue() && hideNametags.getValue();
    }
}
