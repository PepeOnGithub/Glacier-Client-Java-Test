package net.glacierclient.modules.engine;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
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

        // Background panel
        context.fill(x, y, x + w, y + h, 0xCC1E1E2E);

        // Track title placeholder
        context.drawTextWithShadow(
            net.minecraft.client.MinecraftClient.getInstance().textRenderer,
            "♫ Now Playing",
            x + 6, y + 6,
            GlacierTheme.ACCENT
        );

        context.drawTextWithShadow(
            net.minecraft.client.MinecraftClient.getInstance().textRenderer,
            "Track Name - Artist",
            x + 6, y + 18,
            GlacierTheme.TEXT
        );

        // Progress bar
        if (showProgress.getValue()) {
            int barY = y + h - 10;
            context.fill(x + 4, barY, x + w - 4, barY + 4, 0x44FFFFFF);
            context.fill(x + 4, barY, x + 4 + (int)((w - 8) * 0.4f), barY + 4, GlacierTheme.ACCENT);
        }

        // Source label
        context.drawTextWithShadow(
            net.minecraft.client.MinecraftClient.getInstance().textRenderer,
            source.getValue(),
            x + 6, y + 30,
            GlacierTheme.TEXT_DIM
        );
    }
}
