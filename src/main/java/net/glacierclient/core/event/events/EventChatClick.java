package net.glacierclient.core.event.events;

import net.glacierclient.core.event.CancellableEvent;

public class EventChatClick extends CancellableEvent {
    private final String message;
    private final boolean shiftHeld;

    public EventChatClick(String message, boolean shiftHeld) {
        this.message = message;
        this.shiftHeld = shiftHeld;
    }

    public String getMessage() { return message; }
    public boolean isShiftHeld() { return shiftHeld; }
}
