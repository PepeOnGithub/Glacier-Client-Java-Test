package net.glacierclient.core.notification;

import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.gui.DrawContext;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public final class NotificationCenter {
    private static final NotificationCenter INSTANCE = new NotificationCenter();
    public static NotificationCenter get() { return INSTANCE; }

    private final List<Notification> active = new CopyOnWriteArrayList<>();

    public void push(String title, String message, NotificationType type) {
        active.add(new Notification(title, message, type, System.currentTimeMillis()));
        active.removeIf(n -> active.size() > 8);
    }

    public void push(String title, String message) { push(title, message, NotificationType.INFO); }

    public void render(DrawContext ctx, int screenWidth, int screenHeight) {
        int y = screenHeight - 12;
        long now = System.currentTimeMillis();
        Iterator<Notification> it = active.iterator();
        List<Notification> toRender = new ArrayList<>();
        while (it.hasNext()) {
            Notification n = it.next();
            if (now - n.createdAt > n.duration) { it.remove(); continue; }
            toRender.add(n);
        }
        for (int i = toRender.size() - 1; i >= 0; i--) {
            Notification n = toRender.get(i);
            int height = 36;
            int width = 200;
            int x = screenWidth - width - 8;
            y -= height + 4;
            float progress = Math.min(1f, (now - n.createdAt) / 200f);
            float fadeOut = now - n.createdAt > n.duration - 200 ? (n.duration - (now - n.createdAt)) / 200f : 1f;
            float alpha = progress * fadeOut;
            int bg = (int)(alpha * 0xCC) << 24 | (GlacierTheme.BG_PANEL & 0x00FFFFFF);
            int accent = n.type.color;
            ctx.fill(x, y, x + 3, y + height, accent);
            ctx.fill(x + 3, y, x + width, y + height, bg);
            ctx.drawTextWithShadow(net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                net.minecraft.text.Text.literal(n.title), x + 8, y + 6, GlacierTheme.TEXT);
            ctx.drawTextWithShadow(net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                net.minecraft.text.Text.literal(n.message), x + 8, y + 20, GlacierTheme.TEXT_DIM);
        }
    }

    public static final class Notification {
        final String title, message;
        final NotificationType type;
        final long createdAt;
        final long duration = 4000L;
        Notification(String title, String message, NotificationType type, long createdAt) {
            this.title = title; this.message = message; this.type = type; this.createdAt = createdAt;
        }
    }

    public enum NotificationType {
        INFO(GlacierTheme.ACCENT), SUCCESS(GlacierTheme.GREEN), WARNING(GlacierTheme.ORANGE), ERROR(GlacierTheme.RED);
        final int color;
        NotificationType(int color) { this.color = color; }
    }
}
