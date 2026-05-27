package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.gui.DrawContext;

public class MenuBackgroundVideoShader extends GlacierMod {

    private final ModeSetting shader = new ModeSetting("Shader", "Background shader style",
        new String[]{"Glacier", "Matrix", "Particles", "Starfield"}, "Glacier");
    private final NumberSetting intensity = new NumberSetting("Intensity", "Shader intensity", 0.1, 2.0, 1.0);
    private final NumberSetting speed = new NumberSetting("Speed", "Shader animation speed", 0.1, 5.0, 1.0);

    private float time = 0;
    private final java.util.Random rand = new java.util.Random(1337);
    private final float[] particleX = new float[100];
    private final float[] particleY = new float[100];
    private final float[] particleVX = new float[100];
    private final float[] particleVY = new float[100];

    public MenuBackgroundVideoShader() {
        super("Menu Background Shader", "Custom shader for main menu background", Category.RENDER);
        addSettings(shader, intensity, speed);
        for (int i = 0; i < 100; i++) {
            particleX[i] = rand.nextFloat();
            particleY[i] = rand.nextFloat();
            particleVX[i] = (rand.nextFloat() - 0.5f) * 0.002f;
            particleVY[i] = (rand.nextFloat() - 0.5f) * 0.002f;
        }
    }

    @Override public void onEnable() { time = 0; }
    @Override public void onDisable() {}

    @Override
    public void onTick() {
        time += (float) (speed.getValue() * 0.02);
        for (int i = 0; i < 100; i++) {
            particleX[i] += particleVX[i] * (float) speed.getValue();
            particleY[i] += particleVY[i] * (float) speed.getValue();
            if (particleX[i] < 0 || particleX[i] > 1) particleVX[i] = -particleVX[i];
            if (particleY[i] < 0 || particleY[i] > 1) particleVY[i] = -particleVY[i];
        }
    }

    public void renderBackground(DrawContext context, int w, int h) {
        float intens = (float) intensity.getValue();
        String shaderVal = shader.getValue();
        if ("Glacier".equals(shaderVal)) {
            // Aurora bands
            for (int i = 0; i < 5; i++) {
                float offset = (float) Math.sin(time + i * 1.2f);
                int bandY = (int) (h * (0.3 + i * 0.1 + offset * 0.05));
                int alpha = (int) (intens * 30);
                context.fill(0, bandY, w, bandY + 20, (alpha << 24) | 0x7289DA);
            }
        } else if ("Particles".equals(shaderVal)) {
            for (int i = 0; i < 100; i++) {
                int px = (int) (particleX[i] * w);
                int py = (int) (particleY[i] * h);
                int alpha = (int) (intens * 180);
                context.fill(px, py, px + 2, py + 2, (alpha << 24) | 0x7289DA);
            }
        } else if ("Starfield".equals(shaderVal)) {
            for (int i = 0; i < 100; i++) {
                int sx = (int) ((particleX[i] + time * 0.01f * i * 0.001f) % 1.0f * w);
                int sy = (int) (particleY[i] * h);
                int alpha = (int) (intens * 200);
                context.fill(sx, sy, sx + 1, sy + 1, (alpha << 24) | 0xFFFFFF);
            }
        } else if ("Matrix".equals(shaderVal)) {
            for (int i = 0; i < 40; i++) {
                int bx = (int) (particleX[i % 100] * w);
                int by = (int) ((particleY[i % 100] + time * 0.1f) % 1.0f * h);
                int alpha = (int) (intens * 160);
                context.fill(bx, by, bx + 2, by + 8, (alpha << 24) | 0x43B581);
            }
        }
    }
}
