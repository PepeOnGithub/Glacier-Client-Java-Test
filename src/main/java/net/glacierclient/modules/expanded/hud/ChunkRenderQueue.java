package net.glacierclient.modules.expanded.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ChunkRenderQueue extends HUDMod {

    private static final int ORANGE = 0xFFFFA500;

    private final BooleanSetting showQueue = new BooleanSetting("Show Queue", "Display pending chunk rebuild count", false);
    private final BooleanSetting showETA = new BooleanSetting("Show ETA", "Estimate time until queue is clear", false);
    private final NumberSetting warnThreshold = new NumberSetting("Warn Threshold", "Queue depth to trigger busy color", 200, 50, 1000);
    private final ColorSetting busyColor = new ColorSetting("Busy Color", "Color when queue exceeds threshold", ORANGE);

    public ChunkRenderQueue() {
        super("Chunk Queue", "Pending chunk load/rebuild count display", 160, 20);
        addSettings(showQueue, showETA, warnThreshold, busyColor);
    }

    @Override
    public void render(DrawContext context, float delta) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        MinecraftClient mc = MinecraftClient.getInstance();

        context.fill(x, y, x + w, y + h, 0xCC1E1E2E);

        int queueDepth = 128; // Placeholder value
        boolean busy = queueDepth > (int)(double) warnThreshold.getValue();
        int col = busy ? busyColor.getValue() : GlacierTheme.TEXT;

        StringBuilder sb = new StringBuilder();
        if (showQueue.getValue()) sb.append("Q: ").append(queueDepth);
        if (showETA.getValue()) sb.append(" ETA: 2s");

        context.drawTextWithShadow(mc.textRenderer, sb.toString().trim(), x + 4, y + 5, col);
    }
}
