package net.glacierclient.core.event;

public abstract class CancellableEvent extends GlacierEvent {
    private boolean cancelled = false;
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    public void cancel() { this.cancelled = true; }
}
