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
        String mode = style.getValue();
        if (mode.equals("Icons")) {
            int ix = x + 4, iy = y + 4;
            for (StatusEffectInstance effect : effects) {
                int duration = effect.getDuration();
                int c = 0xFF000000 | (effect.getEffectType().getColor() & 0xFFFFFF);
                context.fill(ix, iy, ix + 14, iy + 14, c);
                context.drawBorder(ix, iy, 14, 14, 0x60FFFFFF);
                float pct = Math.min(1.0f, duration / 600f);
                context.fill(ix, iy + 15, ix + (int)(14 * pct), iy + 16, GlacierTheme.ACCENT);
                if (showAmplifier.getValue() && effect.getAmplifier() > 0)
                    context.drawText(mc.textRenderer, toRoman(effect.getAmplifier() + 1), ix + 9, iy + 8, 0xFFFFFFFF, true);
                ix += 18;
                if (ix + 14 > x + getWidth()) { ix = x + 4; iy += 19; }
            }
            return;
        }
        boolean horizontal = mode.equals("Horizontal");
        int lineY = y + 4;
        int colX = x + 4;
        int bw = (int)(double) barWidth.getValue();
        if (horizontal) bw = Math.max(40, (getWidth() - 8) / 2 - 6);
        for (StatusEffectInstance effect : effects) {
            if (!horizontal && lineY + 12 > y + getHeight()) break;
            String name = effect.getEffectType().getName().getString();
            int level = effect.getAmplifier() + 1;
            int duration = effect.getDuration();
            float pct = Math.min(1.0f, duration / 600f);
            int bx = horizontal ? colX : x + 4;
            int by = horizontal ? y + 4 : lineY;
            String label = name + (showAmplifier.getValue() ? " " + toRoman(level) : "") + " " + formatTicks(duration);
            context.drawText(mc.textRenderer, label, bx, by, GlacierTheme.TEXT, false);
            int barY = by + 9;
            context.fill(bx, barY, bx + bw, barY + 3, 0x44FFFFFF);
            int barColor = duration < 100 ? 0xFFF04747 : duration < 200 ? 0xFFFAA61A : GlacierTheme.ACCENT;
            context.fill(bx, barY, bx + (int) (bw * pct), barY + 3, barColor);
            if (horizontal) { colX += bw + 6; if (colX + 20 > x + getWidth()) break; }
            else lineY += 15;
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
