package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ChunkLoadViewer extends HUDMod {

    private final BooleanSetting showProgress = new BooleanSetting("Show Progress", "Show load progress bar", true);
    private final BooleanSetting showETA = new BooleanSetting("Show ETA", "Show ETA for chunk loading", false);
    private final NumberSetting warnThreshold = new NumberSetting("Warn Threshold", "Queue size warning threshold", 100, 10000, 1000);

    private int pendingChunks = 0;
    private int maxSeen = 1;
    private long loadStart = 0;
    private int lastCount = 0;
    private long lastMs = 0;
    private double chunksPerSec = 0;

    public ChunkLoadViewer() {
        super("Chunk Load Viewer", "Shows pending chunk render queue size", 160, 20);
        addSettings(showProgress, showETA, warnThreshold);
    }

    @Override public void onEnable() { maxSeen = 1; loadStart = System.currentTimeMillis(); }
    @Override public void onDisable() {}

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.worldRenderer != null) {
            pendingChunks = mc.worldRenderer.getCompletedChunkCount();
            if (pendingChunks > maxSeen) maxSeen = pendingChunks;
            long now = System.currentTimeMillis();
            if (lastMs != 0 && now > lastMs) {
                double inst = (pendingChunks - lastCount) * 1000.0 / (now - lastMs);
                chunksPerSec = chunksPerSec * 0.7 + inst * 0.3; // smoothed
            }
            lastCount = pendingChunks;
            lastMs = now;
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        var tr = MinecraftClient.getInstance().textRenderer;
        boolean warn = pendingChunks > (int)(double) warnThreshold.getValue();
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        int textColor = warn ? 0xFFFAA61A : GlacierTheme.TEXT;
        String label = "Chunks: " + pendingChunks + (warn ? " !" : "");
        if (showETA.getValue()) {
            int remaining = Math.max(0, maxSeen - pendingChunks);
            if (chunksPerSec > 0.5 && remaining > 0) {
                int eta = (int) Math.ceil(remaining / chunksPerSec);
                label += "  ETA " + (eta >= 60 ? (eta / 60) + "m" + (eta % 60) + "s" : eta + "s");
            } else {
                label += "  ETA --";
            }
        }
        context.drawText(tr, label, x + 4, y + 6, textColor, false);
        if (showProgress.getValue() && maxSeen > 0) {
            float pct = Math.min(1.0f, pendingChunks / (float) maxSeen);
            int barW = w - 8;
            context.fill(x + 4, y + h - 4, x + 4 + barW, y + h - 1, 0x44FFFFFF);
            context.fill(x + 4, y + h - 4, x + 4 + (int) (barW * pct), y + h - 1,
                warn ? 0xFFFAA61A : GlacierTheme.ACCENT);
        }
    }
}
