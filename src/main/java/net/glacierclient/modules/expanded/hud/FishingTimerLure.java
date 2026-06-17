package net.glacierclient.modules.expanded.hud;

import net.glacierclient.core.hud.HUDMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Items;

public class FishingTimerLure extends HUDMod {

    private final BooleanSetting showRing = new BooleanSetting("Show Ring", "Display progress ring for bite window", false);
    private final ColorSetting ringColor = new ColorSetting("Ring Color", "Color of the bite progress ring", GlacierTheme.ACCENT);
    private final BooleanSetting soundAlert = new BooleanSetting("Sound Alert", "Play sound when bite window is predicted", false);
    private final BooleanSetting autoRecast = new BooleanSetting("Auto Recast", "Automatically recast rod after catching", false);

    private boolean bitePlayed = false;
    private boolean wasCast = false;
    private int recastCooldown = 0;

    public FishingTimerLure() {
        super("Fishing Timer", "Fishing bite window predictor with progress ring", 160, 40);
        addSettings(showRing, ringColor, soundAlert, autoRecast);
    }

    @Override public void onEnable() { bitePlayed = false; wasCast = false; recastCooldown = 0; }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        var hook = mc.player.fishHook;
        boolean nowCast = hook != null;
        boolean holdingRod = mc.player.getMainHandStack().isOf(Items.FISHING_ROD) || mc.player.getOffHandStack().isOf(Items.FISHING_ROD);

        if (nowCast) {
            // Bite detection: the bobber dips with a sharp downward velocity.
            if (soundAlert.getValue() && !bitePlayed && hook.getVelocity().y < -0.15) {
                mc.player.playSound(net.minecraft.sound.SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1.0f, 1.6f);
                bitePlayed = true;
            }
        } else {
            bitePlayed = false;
        }

        // Auto recast: when a cast ends (reel-in/catch), throw the line again shortly after.
        if (autoRecast.getValue() && wasCast && !nowCast && holdingRod) recastCooldown = 12;
        if (recastCooldown > 0 && --recastCooldown == 0 && holdingRod && mc.interactionManager != null) {
            net.minecraft.util.Hand hand = mc.player.getMainHandStack().isOf(Items.FISHING_ROD)
                ? net.minecraft.util.Hand.MAIN_HAND : net.minecraft.util.Hand.OFF_HAND;
            mc.interactionManager.interactItem(mc.player, hand);
        }
        wasCast = nowCast;
    }

    @Override
    public void render(DrawContext context, float delta) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        MinecraftClient mc = MinecraftClient.getInstance();
        boolean holdingRod = mc.player != null && (mc.player.getMainHandStack().isOf(Items.FISHING_ROD) || mc.player.getOffHandStack().isOf(Items.FISHING_ROD));
        boolean cast = mc.player != null && mc.player.fishHook != null;
        float progress = cast ? Math.min(1f, (mc.player.fishHook.age % 600) / 600f) : 0f;

        drawBackground(context, x, y, w, h);
        context.drawTextWithShadow(mc.textRenderer, "Fishing Timer", x + 4, y + 4, GlacierTheme.ACCENT);

        if (showRing.getValue()) {
            int barY = y + 18;
            context.fill(x + 4, barY, x + w - 4, barY + 6, 0x44FFFFFF);
            context.fill(x + 4, barY, x + 4 + (int)((w - 8) * progress), barY + 6, ringColor.getValue());
        }

        String status = cast ? "Line cast" : (holdingRod ? "Rod ready" : "Equip fishing rod");
        context.drawTextWithShadow(mc.textRenderer, status, x + 4, y + 28, GlacierTheme.TEXT_DIM);
    }
}
