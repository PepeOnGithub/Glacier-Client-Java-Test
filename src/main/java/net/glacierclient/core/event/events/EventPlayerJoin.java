package net.glacierclient.core.event.events;

import net.glacierclient.core.event.GlacierEvent;

public class EventPlayerJoin extends GlacierEvent {
    private final String playerName;

    public EventPlayerJoin(String playerName) { this.playerName = playerName; }
    public String getPlayerName() { return playerName; }
}
