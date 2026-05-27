package net.glacierclient.core.event.events;

import net.glacierclient.core.event.CancellableEvent;

public class MouseScrollEvent extends CancellableEvent {
    private final double scrollX;
    private final double scrollY;

    public MouseScrollEvent(double scrollX, double scrollY) {
        this.scrollX = scrollX;
        this.scrollY = scrollY;
    }

    public double getScrollX() { return scrollX; }
    public double getScrollY() { return scrollY; }
}
