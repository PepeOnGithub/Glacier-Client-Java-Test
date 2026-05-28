package net.glacierclient.core.event;

public class EventKeyPress extends GlacierEvent {
    private final int key;
    private final int action;
    public EventKeyPress(int key, int action) { this.key = key; this.action = action; }
    public int getKey() { return key; }
    public int getAction() { return action; }
}
