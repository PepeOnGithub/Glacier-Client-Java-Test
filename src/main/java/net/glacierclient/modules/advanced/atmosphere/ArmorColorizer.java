package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.ModeSetting;

public class ArmorColorizer extends GlacierMod {

    private final BooleanSetting colorTeam = new BooleanSetting("Color Team", "Apply team colors to armor", true);
    private final BooleanSetting colorSelf = new BooleanSetting("Color Self", "Apply color to own armor", true);
    private final ColorSetting selfColor = new ColorSetting("Self Color", "Color for own armor", 0xFF7289DA);
    private final ModeSetting teamMode = new ModeSetting("Team Mode", "Team color mode", new String[]{"Auto", "Manual", "Rainbow"}, "Auto");

    private float rainbowHue = 0;

    public ArmorColorizer() {
        super("Armor Colorizer", "Client-side team armor coloring", Category.RENDER);
        addSettings(colorTeam, colorSelf, selfColor, teamMode);
    }

    @Override public void onEnable() { rainbowHue = 0; }
    @Override public void onDisable() {}

    @Override
    public void onTick() {
        if ("Rainbow".equals(teamMode.getValue())) {
            rainbowHue += 0.01f;
            if (rainbowHue > 1f) rainbowHue = 0;
        }
    }

    public int getColorForPlayer(net.minecraft.entity.player.PlayerEntity player) {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.player == null) return 0xFFFFFFFF;
        if (colorSelf.getValue() && player == mc.player) return selfColor.getValue();
        if (colorTeam.getValue()) {
            if ("Rainbow".equals(teamMode.getValue())) {
                return net.minecraft.util.math.ColorHelper.Argb.getArgb(255,
                    (int)(Math.abs(Math.sin(rainbowHue * Math.PI * 2)) * 255),
                    (int)(Math.abs(Math.sin(rainbowHue * Math.PI * 2 + 2)) * 255),
                    (int)(Math.abs(Math.sin(rainbowHue * Math.PI * 2 + 4)) * 255));
            }
        }
        return 0xFFFFFFFF;
    }
}
