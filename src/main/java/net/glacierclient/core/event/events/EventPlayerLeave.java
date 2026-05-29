package net.glacierclient.core.event.events;

import net.glacierclient.core.event.GlacierEvent;

public class EventPlayerLeave extends GlacierEvent {
    private final String playerName;

    public EventPlayerLeave(String playerName) { this.playerName = playerName; }
    public String getPlayerName() { return playerName; }
}
