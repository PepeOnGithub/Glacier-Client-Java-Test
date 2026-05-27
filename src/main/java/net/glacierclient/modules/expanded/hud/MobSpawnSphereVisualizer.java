package net.glacierclient.modules.expanded.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class MobSpawnSphereVisualizer extends HUDMod {

    private final BooleanSetting show24Block = new BooleanSetting("Show 24 Block", "Overlay the 24-block mob spawn exclusion radius", true);
    private final BooleanSetting show128Block = new BooleanSetting("Show 128 Block", "Overlay the 128-block mob despawn radius", false);
    private final BooleanSetting colorByLight = new BooleanSetting("Color By Light", "Color overlay by light level", false);
    private final BooleanSetting showBlockLight = new BooleanSetting("Show Block Light", "Show block light values on overlay", false);

    public MobSpawnSphereVisualizer() {
        super("Spawn Sphere", "Light level and spawn radius overlay", 160, 20);
        addSettings(show24Block, show128Block, colorByLight, showBlockLight);
    }

    @Override
    public void render(DrawContext context, float delta) {
        int x = getX();
        int y = getY();
        MinecraftClient mc = MinecraftClient.getInstance();

        context.fill(x, y, x + getWidth(), y + getHeight(), 0xCC1E1E2E);

        StringBuilder sb = new StringBuilder("Spawn: ");
        if (show24Block.getValue()) sb.append("24 ");
        if (show128Block.getValue()) sb.append("128 ");

        context.drawTextWithShadow(mc.textRenderer, sb.toString().trim(), x + 4, y + 5, GlacierTheme.TEXT);
    }
}
