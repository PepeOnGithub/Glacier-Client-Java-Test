package net.glacierclient.core.event;

public class EventRenderWorld extends GlacierEvent {
    private final float tickDelta;
    public EventRenderWorld(float tickDelta) { this.tickDelta = tickDelta; }
    public float getTickDelta() { return tickDelta; }
}
