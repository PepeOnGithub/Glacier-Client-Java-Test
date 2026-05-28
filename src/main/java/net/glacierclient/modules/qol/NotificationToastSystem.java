package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class NotificationToastSystem extends GlacierMod {

    private final ModeSetting position = new ModeSetting("Position", "Toast position on screen", "TopRight", "TopRight", "TopLeft", "BottomRight", "BottomLeft");
    private final NumberSetting duration = new NumberSetting("Duration", "Toast display duration (ms)", 1000, 10000, 4000);
    private final BooleanSetting sounds = new BooleanSetting("Sounds", "Play sound on notification", true);
    private final NumberSetting maxVisible = new NumberSetting("Max Visible", "Max simultaneous toasts", 1, 10, 5);

    private static class Toast {
        String title, body;
        long showUntil;
        float alpha;
        Toast(String t, String b, long until) { title=t; body=b; showUntil=until; alpha=1f; }
    }

    private final List<Toast> toasts = new ArrayList<>();

    public NotificationToastSystem() {
        super("Notification Toast System", "Display toast notifications on screen", Category.QOL);
        addSettings(position, duration, sounds, maxVisible);
    }

    @Override
    public void onEnable() { toasts.clear(); }

    @Override
    public void onDisable() { toasts.clear(); }

    @Override
    public void onTick() {
        long now = System.currentTimeMillis();
        toasts.removeIf(t -> now > t.showUntil);
    }

    public void show(String title, String body) {
        if (toasts.size() >= (int) maxVisible.getValue()) toasts.remove(0);
        long until = System.currentTimeMillis() + (long) duration.getValue();
        toasts.add(new Toast(title, body, until));
        if (sounds.getValue()) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.getSoundManager() != null) {
                mc.getSoundManager().play(net.minecraft.client.sound.PositionedSoundInstance.master(
                    net.minecraft.sound.SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 2f, 0.4f));
            }
        }
    }

    public void render(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null) return;
        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        int toastW = 160, toastH = 30, pad = 4;
        String pos = position.getValue();
        for (int i = 0; i < toasts.size(); i++) {
            Toast t = toasts.get(i);
            int x, y;
            if (pos.equals("TopRight")) { x = sw - toastW - 4; y = 4 + i * (toastH + pad); }
            else if (pos.equals("TopLeft")) { x = 4; y = 4 + i * (toastH + pad); }
            else if (pos.equals("BottomRight")) { x = sw - toastW - 4; y = sh - (toasts.size() - i) * (toastH + pad); }
            else { x = 4; y = sh - (toasts.size() - i) * (toastH + pad); }
            context.fill(x, y, x + toastW, y + toastH, GlacierTheme.BG_PANEL);
            context.fill(x, y, x + 3, y + toastH, GlacierTheme.ACCENT);
            context.drawText(mc.textRenderer, t.title, x + 6, y + 5, GlacierTheme.TEXT, false);
            context.drawText(mc.textRenderer, t.body, x + 6, y + 17, GlacierTheme.TEXT_DIM, false);
        }
    }
}
