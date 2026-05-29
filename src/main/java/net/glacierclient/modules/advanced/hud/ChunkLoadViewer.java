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
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        var tr = MinecraftClient.getInstance().textRenderer;
        boolean warn = pendingChunks > (int)(double) warnThreshold.getValue();
        context.fill(x, y, x + w, y + h, 0xAA1A1A2E);
        int textColor = warn ? 0xFFFAA61A : GlacierTheme.TEXT;
        context.drawText(tr, "Chunks: " + pendingChunks + (warn ? " !" : ""), x + 4, y + 6, textColor, false);
        if (showProgress.getValue() && maxSeen > 0) {
            float pct = Math.min(1.0f, pendingChunks / (float) maxSeen);
            int barW = w - 8;
            context.fill(x + 4, y + h - 4, x + 4 + barW, y + h - 1, 0x44FFFFFF);
            context.fill(x + 4, y + h - 4, x + 4 + (int) (barW * pct), y + h - 1,
                warn ? 0xFFFAA61A : GlacierTheme.ACCENT);
        }
    }
}
