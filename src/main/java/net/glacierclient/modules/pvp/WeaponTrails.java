package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ColorSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class WeaponTrails extends GlacierMod {

    private final ModeSetting style = new ModeSetting("Style", "Trail style", "Particles", "Particles", "Line", "Glow");
    private final ColorSetting color = new ColorSetting("Color", "Trail color", 0xFF7289DA);
    private final NumberSetting duration = new NumberSetting("Duration", "Trail duration (seconds)", 0.1, 1.0, 0.3);
    private final BooleanSetting rainbowMode = new BooleanSetting("Rainbow Mode", "Rainbow cycling trail", false);

    private float hue = 0f;

    public WeaponTrails() {
        super("Weapon Trails", "Show particle trails when swinging weapons", Category.PVP);
        addSettings(style, color, duration, rainbowMode);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        if (rainbowMode.getValue()) hue = (hue + 0.02f) % 1f;

        // Spawn a coloured dust trail along the swing arc while the hand is mid-swing.
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity p = mc.player;
        if (p == null || mc.world == null || !p.handSwinging) return;

        int col = getTrailColor();
        Vector3f rgb = new Vector3f(((col >> 16) & 0xFF) / 255f, ((col >> 8) & 0xFF) / 255f, (col & 0xFF) / 255f);
        float scale = "Glow".equals(style.getValue()) ? 2.0f : "Line".equals(style.getValue()) ? 0.8f : 1.3f;
        DustParticleEffect dust = new DustParticleEffect(rgb, scale);

        // A short arc of points in front of the hand, swept by the current swing progress.
        Vec3d look = p.getRotationVec(1f);
        double baseX = p.getX() + look.x * 0.9;
        double baseY = p.getEyeY() - 0.25 + look.y * 0.9;
        double baseZ = p.getZ() + look.z * 0.9;
        Vec3d right = new Vec3d(-look.z, 0, look.x).normalize();
        int points = "Particles".equals(style.getValue()) ? 4 : 6;
        for (int i = 0; i < points; i++) {
            double t = (i / (double) (points - 1) - 0.5) * 1.2; // -0.6..0.6 along the swing
            mc.world.addParticle(dust, baseX + right.x * t, baseY, baseZ + right.z * t, 0, 0, 0);
        }
    }

    public int getTrailColor() {
        if (rainbowMode.getValue()) return 0xFF000000 | java.awt.Color.HSBtoRGB(hue, 1f, 1f);
        return color.getValue();
    }

    public String getStyle() { return style.getValue(); }
    public float getDuration() { return (float)(double) duration.getValue(); }
}
