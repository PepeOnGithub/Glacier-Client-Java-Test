package net.glacierclient.modules.expanded.social;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class PartyHealthBarOverlay extends HUDMod {

    private final BooleanSetting showHealth = new BooleanSetting("Show Health", "Show party member health bars", false);
    private final BooleanSetting showArmor = new BooleanSetting("Show Armor", "Show party member armor values", false);
    private final NumberSetting maxMembers = new NumberSetting("Max Members", "Maximum number of party members to display", 5, 1, 10);
    private final ModeSetting style = new ModeSetting("Style", "Display style for health", "Bars", "Bars", "Numbers", "Both");

    public PartyHealthBarOverlay() {
        super("Party Health Bars", "Health bars for party members", 200, 80);
        addSettings(showHealth, showArmor, maxMembers, style);
    }

    @Override
    public void render(DrawContext context, float delta) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        MinecraftClient mc = MinecraftClient.getInstance();

        context.fill(x, y, x + w, y + h, 0xCC1E1E2E);
        context.drawTextWithShadow(mc.textRenderer, "Party", x + 4, y + 4, GlacierTheme.ACCENT);

        int members = (int)(double) maxMembers.getValue();
        int lineY = y + 16;
        int barW = w - 60;

        for (int i = 0; i < members && lineY + 10 <= y + h; i++) {
            float hp = 1.0f - (i * 0.15f);
            int armor = Math.max(0, 20 - i * 4);
            context.drawTextWithShadow(mc.textRenderer, "P" + (i + 1), x + 4, lineY, GlacierTheme.TEXT);

            if (showHealth.getValue() && ("Bars".equals(style.getValue()) || "Both".equals(style.getValue()))) {
                context.fill(x + 24, lineY, x + 24 + barW, lineY + 6, 0x44FFFFFF);
                context.fill(x + 24, lineY, x + 24 + (int)(barW * hp), lineY + 6, 0xFFFF5555);
            }
            if (showHealth.getValue() && ("Numbers".equals(style.getValue()) || "Both".equals(style.getValue()))) {
                context.drawTextWithShadow(mc.textRenderer,
                    String.format("%.0f%%", hp * 100),
                    x + w - 30, lineY, GlacierTheme.TEXT);
            }
            if (showArmor.getValue()) {
                context.drawTextWithShadow(mc.textRenderer, "[" + armor + "]", x + w - 58, lineY, 0xFF9DB2CE);
            }
            lineY += 10;
        }
    }
}
