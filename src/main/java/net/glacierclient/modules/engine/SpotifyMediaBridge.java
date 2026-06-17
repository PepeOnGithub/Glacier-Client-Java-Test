package net.glacierclient.modules.engine;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class SpotifyMediaBridge extends HUDMod {

    private final ModeSetting source = new ModeSetting("Source", "Media source to display", "Spotify", "Spotify", "LocalPlayer", "SystemMedia");
    private final BooleanSetting showAlbumArt = new BooleanSetting("Show Album Art", "Display album/track artwork", false);
    private final BooleanSetting showProgress = new BooleanSetting("Show Progress", "Show track progress bar", false);
    private final BooleanSetting showControls = new BooleanSetting("Show Controls", "Display playback control buttons", false);
    private final NumberSetting controlSize = new NumberSetting("Control Size", "Size of playback control icons", 24, 16, 32);

    public SpotifyMediaBridge() {
        super("Spotify Media Bridge", "In-game Spotify/media player HUD display", 240, 60);
        addSettings(source, showAlbumArt, showProgress, showControls, controlSize);
    }

    @Override
    public void render(DrawContext context, float delta) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        MinecraftClient mc = MinecraftClient.getInstance();
        int textX = x + 6;

        drawBackground(context, x, y, w, h);
        if (showAlbumArt.getValue()) {
            int art = Math.min(h - 12, 40);
            context.fill(x + 6, y + 6, x + 6 + art, y + 6 + art, 0x663C8DFF);
            textX = x + art + 14;
        }

        context.drawTextWithShadow(mc.textRenderer, source.getValue() + " Media", textX, y + 6, GlacierTheme.ACCENT);
        context.drawTextWithShadow(mc.textRenderer, "Bridge idle", textX, y + 18, GlacierTheme.TEXT);

        if (showProgress.getValue()) {
            int barY = y + h - 10;
            context.fill(x + 4, barY, x + w - 4, barY + 4, 0x44FFFFFF);
            context.fill(x + 4, barY, x + 4, barY + 4, GlacierTheme.ACCENT);
        }

        if (showControls.getValue()) {
            int size = controlSize.getValueAsInt();
            int cy = y + h - size / 2 - 6;
            context.drawCenteredTextWithShadow(mc.textRenderer, "<<", x + w - size * 3, cy - 4, GlacierTheme.TEXT_DIM);
            context.drawCenteredTextWithShadow(mc.textRenderer, ">", x + w - size * 2, cy - 4, GlacierTheme.TEXT_DIM);
            context.drawCenteredTextWithShadow(mc.textRenderer, ">>", x + w - size, cy - 4, GlacierTheme.TEXT_DIM);
        }
    }
}
