package net.glacierclient.modules.expanded.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class BlockBreakEfficiencyOverlay extends HUDMod {

    private static final int GREEN = 0xFF55FF55;
    private static final int RED = 0xFFFF5555;

    private final BooleanSetting showBPS = new BooleanSetting("Show BPS", "Show blocks per second mining speed", false);
    private final BooleanSetting showEfficiency = new BooleanSetting("Show Efficiency", "Show mining efficiency percentage", false);
    private final BooleanSetting showEnchantBonus = new BooleanSetting("Show Enchant Bonus", "Show efficiency enchantment bonus", false);
    private final ColorSetting goodColor = new ColorSetting("Good Color", "Color for high efficiency indicator", GREEN);
    private final ColorSetting badColor = new ColorSetting("Bad Color", "Color for low efficiency indicator", RED);

    public BlockBreakEfficiencyOverlay() {
        super("Break Efficiency", "Real-time BPS mining speed indicator", 180, 30);
        addSettings(showBPS, showEfficiency, showEnchantBonus, goodColor, badColor);
    }

    @Override
    public void render(DrawContext context, float delta) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        MinecraftClient mc = MinecraftClient.getInstance();

        context.fill(x, y, x + w, y + h, 0xCC1E1E2E);

        int effLvl = 0;
        if (mc.player != null) {
            effLvl = net.minecraft.enchantment.EnchantmentHelper.getLevel(
                net.minecraft.enchantment.Enchantments.EFFICIENCY, mc.player.getMainHandStack());
        }
        boolean good = effLvl >= 3;
        float bps = 1.0f + effLvl * 0.65f; // proxy: each Efficiency level speeds mining

        StringBuilder sb = new StringBuilder();
        if (showBPS.getValue()) sb.append(String.format("BPS: %.1f ", bps));
        if (showEfficiency.getValue()) sb.append(good ? "Eff: High " : "Eff: Low ");
        if (showEnchantBonus.getValue()) sb.append("+Eff").append(effLvl);

        int col = showEfficiency.getValue() ? (good ? goodColor.getValue() : badColor.getValue()) : GlacierTheme.TEXT;
        context.drawTextWithShadow(mc.textRenderer, sb.toString().trim(), x + 4, y + 10, col);
    }
}
