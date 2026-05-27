package net.glacierclient.modules.hud;

import net.glacierclient.core.module.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.Collection;

public class PotionsStatusHUD extends HUDMod {

    private final ModeSetting style = new ModeSetting("Style", "Display style", "Text", "Icons", "Text", "Both");
    private final BooleanSetting showDuration = new BooleanSetting("Show Duration", "Show remaining duration", true);
    private final BooleanSetting showAmplifier = new BooleanSetting("Show Amplifier", "Show effect level", true);

    public PotionsStatusHUD() {
        super("Potions Status", "Shows active potion effects", 120, 80);
        addSettings(style, showDuration, showAmplifier);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    @Override
    public void render(DrawContext context, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null || mc.player == null) return;
        Collection<StatusEffectInstance> effects = mc.player.getStatusEffects();
        int x = getX() + 2, y = getY() + 2;
        for (StatusEffectInstance effect : effects) {
            StringBuilder sb = new StringBuilder();
            String name = effect.getEffectType().getName().getString();
            sb.append(name);
            if (showAmplifier.getValue() && effect.getAmplifier() > 0)
                sb.append(" ").append(effect.getAmplifier() + 1);
            if (showDuration.getValue()) {
                int ticks = effect.getDuration();
                if (ticks < 32767) {
                    int secs = ticks / 20;
                    sb.append(String.format(" %d:%02d", secs / 60, secs % 60));
                }
            }
            context.drawText(mc.textRenderer, sb.toString(), x, y, GlacierTheme.TEXT, false);
            y += 10;
            if (y > getY() + getHeight() - 2) break;
        }
    }
}
