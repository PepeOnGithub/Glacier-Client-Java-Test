package net.glacierclient.core.event;

public class EventChatClick extends CancellableEvent {
    private final String message;
    public EventChatClick(String message) { this.message = message; }
    public String getMessage() { return message; }
}
