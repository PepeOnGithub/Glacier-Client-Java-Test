package net.glacierclient.modules.render;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class CustomCrosshair extends GlacierMod {

    private final ModeSetting style = new ModeSetting("Style", "Crosshair shape", "Cross", "Default", "Cross", "Dot", "Circle", "Image");
    private final NumberSetting imageIndex = new NumberSetting("Image", "Preset crosshair image (1-33)", 1, 33, 1);
    private final NumberSetting size = new NumberSetting("Size", "Crosshair line length", 1, 32, 8);
    private final ColorSetting color = new ColorSetting("Color", "Crosshair color", GlacierTheme.TEXT);
    private final BooleanSetting dynamicColor = new BooleanSetting("Dynamic Color", "Tint red when aiming at a player", false);
    private final NumberSetting thickness = new NumberSetting("Thickness", "Line thickness", 1, 4, 1);
    private final NumberSetting gap = new NumberSetting("Gap", "Empty space at the center", 0, 20, 0);
    private final BooleanSetting dynamicGap = new BooleanSetting("Dynamic Gap", "Gap reacts to movement and attacks", false);
    private final NumberSetting movingGap = new NumberSetting("Moving Gap", "Gap while moving", 0, 40, 12);
    private final NumberSetting attackGap = new NumberSetting("Attack Gap", "Gap right after an attack", 0, 20, 8);
    private final NumberSetting gapSpeed = new NumberSetting("Gap Speed", "Dynamic gap transition speed", 0.1, 5.0, 2.0);

    private float currentGap;
    private long lastAttackTime = 0;

    public CustomCrosshair() {
        super("Custom Crosshair", "Replace the vanilla crosshair with custom styles and a reactive gap", Category.RENDER);
        addSettings(style, imageIndex, size, color, dynamicColor, thickness, gap, dynamicGap, movingGap, attackGap, gapSpeed);
    }

    @Override
    public void onEnable() {
        currentGap = gap.getValue().floatValue();
    }

    @Override
    public void onTick() {
        if (!dynamicGap.getValue()) {
            currentGap = gap.getValue().floatValue();
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        boolean moving = mc.player.isSprinting() || (mc.player.input != null
                && (mc.player.input.movementForward != 0 || mc.player.input.movementSideways != 0));
        boolean attacking = System.currentTimeMillis() - lastAttackTime < 300;
        float target;
        if (attacking) target = attackGap.getValue().floatValue();
        else if (moving) target = movingGap.getValue().floatValue();
        else target = gap.getValue().floatValue();
        currentGap += (target - currentGap) * (float) (gapSpeed.getValue() * 0.15);
    }

    public boolean replacesVanilla() {
        return !"Default".equals(style.getValue());
    }

    public void onAttack() {
        lastAttackTime = System.currentTimeMillis();
    }

    public void render(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int cx = mc.getWindow().getScaledWidth() / 2;
        int cy = mc.getWindow().getScaledHeight() / 2;
        int sz = size.getValue().intValue();
        int thick = thickness.getValue().intValue();
        int g = Math.round(currentGap);
        int c = currentColor(mc);
        String s = style.getValue();
        if ("Default".equals(s)) return;
        if ("Image".equals(s)) {
            int n = imageIndex.getValue().intValue();
            int draw = Math.max(4, sz * 2 + g * 2);
            net.glacierclient.core.util.Sprites.drawCentered(context, "crosshairs/" + n, cx, cy, draw);
            return;
        }
        if ("Dot".equals(s)) {
            context.fill(cx - thick, cy - thick, cx + thick, cy + thick, c);
        } else if ("Cross".equals(s)) {
            context.fill(cx - g - sz, cy - thick, cx - g, cy + thick, c);
            context.fill(cx + g, cy - thick, cx + g + sz, cy + thick, c);
            context.fill(cx - thick, cy - g - sz, cx + thick, cy - g, c);
            context.fill(cx - thick, cy + g, cx + thick, cy + g + sz, c);
        } else if ("Circle".equals(s)) {
            int r = sz + g;
            for (int i = 0; i < 360; i += 10) {
                double rad = Math.toRadians(i);
                int px = cx + (int) (Math.cos(rad) * r);
                int py = cy + (int) (Math.sin(rad) * r);
                context.fill(px, py, px + thick, py + thick, c);
            }
        }
    }

    private int currentColor(MinecraftClient mc) {
        int base = color.getValue();
        if (dynamicColor.getValue() && mc.targetedEntity instanceof net.minecraft.entity.player.PlayerEntity) {
            return 0xFFFF4040;
        }
        return base;
    }
}
