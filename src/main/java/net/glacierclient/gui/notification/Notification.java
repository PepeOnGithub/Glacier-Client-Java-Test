package net.glacierclient.gui.notification;

public class Notification {

    public enum Type { INFO, SUCCESS, WARNING, ERROR }

    private final String title;
    private final String message;
    private final Type type;
    private final long createdAt;
    private final long duration;
    private float alpha;

    public Notification(String title, String message, Type type, long duration) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.createdAt = System.currentTimeMillis();
        this.duration = duration;
        this.alpha = 1.0f;
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public Type getType() { return type; }
    public long getCreatedAt() { return createdAt; }
    public long getDuration() { return duration; }
    public float getAlpha() { return alpha; }
    public void setAlpha(float alpha) { this.alpha = alpha; }

    public boolean isExpired() {
        return System.currentTimeMillis() - createdAt > duration;
    }

    public float getProgress() {
        return 1.0f - Math.min(1.0f, (float)(System.currentTimeMillis() - createdAt) / duration);
    }
}
