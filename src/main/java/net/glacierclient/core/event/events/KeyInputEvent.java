package net.glacierclient.core.event.events;

import net.glacierclient.core.event.CancellableEvent;

public class KeyInputEvent extends CancellableEvent {
    private final int key;
    private final int action;
    private final int mods;

    public KeyInputEvent(int key, int action, int mods) {
        this.key = key;
        this.action = action;
        this.mods = mods;
    }

    public int getKey() { return key; }
    public int getAction() { return action; }
    public int getMods() { return mods; }
}
