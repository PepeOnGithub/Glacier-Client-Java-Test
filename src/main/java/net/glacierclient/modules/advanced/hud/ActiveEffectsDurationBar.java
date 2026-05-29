package net.glacierclient.modules.advanced.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import java.util.Collection;

public class ActiveEffectsDurationBar extends HUDMod {

    private final ModeSetting style = new ModeSetting("Style", "Bar layout style", new String[]{"Vertical", "Horizontal", "Icons"}, "Vertical");
    private final BooleanSetting showAmplifier = new BooleanSetting("Show Level", "Show effect amplifier", true);
    private final NumberSetting barWidth = new NumberSetting("Bar Width", "Width of each duration bar", 80, 200, 120);

    public ActiveEffectsDurationBar() {
        super("Effects Duration", "Remaining duration bars for active effects", 160, 60);
        addSettings(style, showAmplifier, barWidth);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
    @Override public void onTick() {}

    @Override
    public void render(DrawContext context, float tickDelta) {
        int x = getX(), y = getY(), w = getWidth();
        MinecraftClient mc = MinecraftClient.getInstance();
        context.fill(x, y, x + w, y + getHeight(), 0xAA1A1A2E);
        if (mc.player == null) return;
        Collection<StatusEffectInstance> effects = mc.player.getStatusEffects();
        if (effects.isEmpty()) {
            context.drawText(mc.textRenderer, "No Effects", x + 4, y + 6, GlacierTheme.TEXT_DIM, false);
            return;
        }
        int lineY = y + 4;
        int bw = (int)(double) barWidth.getValue();
        for (StatusEffectInstance effect : effects) {
            if (lineY + 12 > y + getHeight()) break;
            String name = effect.getEffectType().getName().getString();
            int level = effect.getAmplifier() + 1;
            int duration = effect.getDuration();
            int maxDuration = 600;
            float pct = Math.min(1.0f, duration / (float) maxDuration);
            // Draw label
            String label = name + (showAmplifier.getValue() ? " " + toRoman(level) : "") + " " + formatTicks(duration);
            context.drawText(mc.textRenderer, label, x + 4, lineY, GlacierTheme.TEXT, false);
            lineY += 9;
            // Draw bar
            context.fill(x + 4, lineY, x + 4 + bw, lineY + 3, 0x44FFFFFF);
            int barColor = duration < 100 ? 0xFFF04747 : duration < 200 ? 0xFFFAA61A : GlacierTheme.ACCENT;
            context.fill(x + 4, lineY, x + 4 + (int) (bw * pct), lineY + 3, barColor);
            lineY += 6;
        }
    }

    private String formatTicks(int ticks) {
        int seconds = ticks / 20;
        return seconds >= 60 ? (seconds / 60) + "m" + (seconds % 60) + "s" : seconds + "s";
    }

    private String toRoman(int n) {
        return switch (n) { case 1 -> "I"; case 2 -> "II"; case 3 -> "III"; case 4 -> "IV"; default -> String.valueOf(n); };
    }
}
