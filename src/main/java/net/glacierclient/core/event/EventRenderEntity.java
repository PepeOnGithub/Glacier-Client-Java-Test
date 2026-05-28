package net.glacierclient.core.event;

import net.minecraft.entity.Entity;

public class EventRenderEntity extends GlacierEvent {
    private final Entity entity;
    private final float tickDelta;
    public EventRenderEntity(Entity entity, float tickDelta) { this.entity = entity; this.tickDelta = tickDelta; }
    public Entity getEntity() { return entity; }
    public float getTickDelta() { return tickDelta; }
}
