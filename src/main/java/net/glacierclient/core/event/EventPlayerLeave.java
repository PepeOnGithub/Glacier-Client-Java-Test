package net.glacierclient.core.event;

public class EventPlayerLeave extends GlacierEvent {
    private final String playerName;
    public EventPlayerLeave(String playerName) { this.playerName = playerName; }
    public String getPlayerName() { return playerName; }
}
