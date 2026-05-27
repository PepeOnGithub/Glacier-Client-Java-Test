package net.glacierclient.core.event.events;

import net.glacierclient.core.event.CancellableEvent;
import net.minecraft.entity.Entity;

public class AttackEntityEvent extends CancellableEvent {
    private final Entity target;
    private float reach;

    public AttackEntityEvent(Entity target, float reach) {
        this.target = target;
        this.reach = reach;
    }

    public Entity getTarget() { return target; }
    public float getReach() { return reach; }
    public void setReach(float reach) { this.reach = reach; }
}
