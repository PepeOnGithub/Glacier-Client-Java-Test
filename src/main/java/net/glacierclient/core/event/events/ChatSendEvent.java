package net.glacierclient.core.event.events;

import net.glacierclient.core.event.CancellableEvent;

public class ChatSendEvent extends CancellableEvent {
    private String message;

    public ChatSendEvent(String message) { this.message = message; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
