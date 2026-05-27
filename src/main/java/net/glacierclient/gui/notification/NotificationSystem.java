package net.glacierclient.gui.notification;

import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class NotificationSystem {

    private final List<Notification> notifications = new ArrayList<>();
    private static final int WIDTH = 200;
    private static final int HEIGHT = 40;
    private static final int PADDING = 8;
    private boolean doNotDisturb = false;

    public void send(String title, String message, Notification.Type type) {
        send(title, message, type, 4000);
    }

    public void send(String title, String message, Notification.Type type, long duration) {
        if (doNotDisturb) return;
        notifications.add(new Notification(title, message, type, duration));
    }

    public void render(DrawContext context, int screenWidth, int screenHeight) {
        notifications.removeIf(Notification::isExpired);
        int y = screenHeight - PADDING;
        for (int i = notifications.size() - 1; i >= 0; i--) {
            Notification n = notifications.get(i);
            y -= HEIGHT + PADDING;
            renderNotification(context, n, screenWidth - WIDTH - PADDING, y);
        }
    }

    private void renderNotification(DrawContext context, Notification n, int x, int y) {
        int accentColor = switch (n.getType()) {
            case SUCCESS -> GlacierTheme.GREEN;
            case WARNING -> GlacierTheme.ORANGE;
            case ERROR -> GlacierTheme.RED;
            default -> GlacierTheme.ACCENT;
        };
        int bgAlpha = (int)(n.getAlpha() * 220);
        int bg = (bgAlpha << 24) | (GlacierTheme.BG_PANEL & 0x00FFFFFF);
        context.fill(x, y, x + WIDTH, y + HEIGHT, bg);
        context.fill(x, y, x + 3, y + HEIGHT, accentColor);
        int progressWidth = (int)(WIDTH * n.getProgress());
        context.fill(x, y + HEIGHT - 2, x + progressWidth, y + HEIGHT, accentColor);
    }

    public void setDoNotDisturb(boolean dnd) { this.doNotDisturb = dnd; }
    public boolean isDoNotDisturb() { return doNotDisturb; }
    public List<Notification> getNotifications() { return notifications; }
}
