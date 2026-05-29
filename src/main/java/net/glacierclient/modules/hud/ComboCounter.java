package net.glacierclient.modules.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ComboCounter extends HUDMod {

    private final NumberSetting comboTimeout = new NumberSetting("Combo Timeout", "Seconds before combo resets", 0.5, 5.0, 2.0);
    private final BooleanSetting showRecord = new BooleanSetting("Show Record", "Show highest combo", true);
    private final ColorSetting comboColor = new ColorSetting("Combo Color", "Color of combo text", GlacierTheme.ACCENT);

    private int combo = 0;
    private int record = 0;
    private long lastHitTime = 0;

    public ComboCounter() {
        super("Combo Counter", "Shows hit combo count", 100, 30);
        addSettings(comboTimeout, showRecord, comboColor);
    }

    @Override
    public void onEnable() {
        combo = 0;
        record = 0;
    }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        long now = System.currentTimeMillis();
        double timeoutMs = comboTimeout.getValue() * 1000.0;
        if (combo > 0 && (now - lastHitTime) > timeoutMs) {
            combo = 0;
        }
    }

    public void onHit() {
        combo++;
        if (combo > record) record = combo;
        lastHitTime = System.currentTimeMillis();
    }

    @Override
    public void render(DrawContext context, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null) return;
        long elapsed = System.currentTimeMillis() - lastHitTime;
        double timeoutMs = comboTimeout.getValue() * 1000.0;
        float alpha = combo > 0 ? (float)(1.0 - elapsed / timeoutMs) : 0f;
        alpha = Math.max(0f, Math.min(1f, alpha));
        int alphaInt = (int)(alpha * 255) << 24;
        int color = (comboColor.getValue() & 0x00FFFFFF) | alphaInt;
        context.drawText(mc.textRenderer, "Combo: " + combo, getX() + 2, getY() + 4, color, false);
        if (showRecord.getValue()) {
            context.drawText(mc.textRenderer, "Best: " + record, getX() + 2, getY() + 16, GlacierTheme.TEXT_DIM, false);
        }
    }
}
