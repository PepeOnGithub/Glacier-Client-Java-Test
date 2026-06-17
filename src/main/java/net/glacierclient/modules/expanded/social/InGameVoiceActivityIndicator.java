package net.glacierclient.modules.expanded.social;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class InGameVoiceActivityIndicator extends HUDMod {

    private static final int GREEN = 0xFF55FF55;

    private final ColorSetting speakingColor = new ColorSetting("Speaking Color", "Color when microphone is active", GREEN);
    private final BooleanSetting showNearby = new BooleanSetting("Show Nearby", "Also show nearby players speaking", false);
    private final NumberSetting iconSize = new NumberSetting("Icon Size", "Size of the microphone icon", 24, 16, 48);

    public InGameVoiceActivityIndicator() {
        super("Voice Indicator", "Mic icon when speaking in voice", 40, 40);
        addSettings(speakingColor, showNearby, iconSize);
    }

    @Override
    public void render(DrawContext context, float delta) {
        int x = getX();
        int y = getY();
        int size = (int)(double) iconSize.getValue();
        int cx = x + 20;
        int cy = y + 20;
        MinecraftClient mc = MinecraftClient.getInstance();

        context.fill(x, y, x + 40, y + 40, 0xCC1E1E2E);

        boolean speaking = false; // Placeholder voice detection
        int col = speaking ? speakingColor.getValue() : GlacierTheme.TEXT_DIM;

        // Draw simplified mic icon
        int halfW = size / 6;
        int halfH = size / 3;
        context.fill(cx - halfW, cy - halfH, cx + halfW, cy + halfH / 2, col);
        context.fill(cx - halfW * 2, cy, cx + halfW * 2, cy + 2, col);
        context.fill(cx - 1, cy + halfH / 2, cx + 1, cy + halfH, col);

        // Show Nearby: count other players within voice range.
        if (showNearby.getValue() && mc.player != null && mc.world != null) {
            int nearby = 0;
            for (var p : mc.world.getPlayers())
                if (p != mc.player && p.distanceTo(mc.player) <= 48) nearby++;
            context.drawTextWithShadow(mc.textRenderer, "+" + nearby, x + 26, y + 2, GlacierTheme.TEXT_DIM);
        }
    }
}
