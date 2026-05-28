package net.glacierclient.core.event.events;

import net.glacierclient.core.event.GlacierEvent;

public class TickEvent extends GlacierEvent {
    public enum Phase { PRE, POST }
    private final Phase phase;

    public TickEvent(Phase phase) { this.phase = phase; }
    public Phase getPhase() { return phase; }
}
