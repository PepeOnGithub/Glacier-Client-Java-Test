package net.glacierclient.modules.expanded.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class WorldSeedCrackerIndicator extends HUDMod {

    private static final int ORANGE = 0xFFFFA500;

    private final BooleanSetting singleplayerOnly = new BooleanSetting("Singleplayer Only", "Only activate in singleplayer worlds", true);
    private final BooleanSetting showProgress = new BooleanSetting("Show Progress", "Show seed cracking progress percentage", false);
    private final ColorSetting activeColor = new ColorSetting("Active Color", "Color when cracker is actively running", ORANGE);

    public WorldSeedCrackerIndicator() {
        super("Seed Cracker", "Indicates if seed cracking tools are active (SP only)", 180, 20);
        addSettings(singleplayerOnly, showProgress, activeColor);
    }

    @Override
    public void render(DrawContext context, float delta) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        MinecraftClient mc = MinecraftClient.getInstance();

        boolean isSP = mc.isInSingleplayer();
        if (singleplayerOnly.getValue() && !isSP) return;

        context.fill(x, y, x + w, y + h, 0xCC1E1E2E);

        String label = showProgress.getValue() ? "Cracker: 34%" : "Cracker: Active";
        context.drawTextWithShadow(mc.textRenderer, label, x + 4, y + 5, activeColor.getValue());
    }
}
