package net.glacierclient.core.event;

public class EventChat extends CancellableEvent {
    private String message;
    public EventChat(String message) { this.message = message; }
    public String getMessage() { return message; }
    public void setMessage(String m) { this.message = m; }
}
