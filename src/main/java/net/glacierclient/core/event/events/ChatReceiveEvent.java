package net.glacierclient.core.event.events;

import net.glacierclient.core.event.CancellableEvent;

public class ChatReceiveEvent extends CancellableEvent {
    private String message;

    public ChatReceiveEvent(String message) { this.message = message; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
