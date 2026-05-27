package net.glacierclient.modules.performance;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EntityCulling extends GlacierMod {

    private final BooleanSetting cullBehindWalls = new BooleanSetting("Cull Behind Walls", "Don't render entities behind walls", true);
    private final NumberSetting cullingDistance = new NumberSetting("Culling Distance", "Max entity render distance (blocks)", 16, 128, 64);
    private final BooleanSetting cullPlayers = new BooleanSetting("Cull Players", "Apply culling to players", false);

    public EntityCulling() {
        super("Entity Culling", "Skip rendering entities that aren't visible", Category.PERFORMANCE);
        addSettings(cullBehindWalls, cullingDistance, cullPlayers);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean shouldCull(Entity entity) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return false;
        if (!cullPlayers.getValue() && entity instanceof net.minecraft.entity.player.PlayerEntity) return false;
        Vec3d playerPos = mc.player.getPos();
        double dist = entity.getPos().distanceTo(playerPos);
        if (dist > cullingDistance.getValue()) return true;
        return false;
    }
}
