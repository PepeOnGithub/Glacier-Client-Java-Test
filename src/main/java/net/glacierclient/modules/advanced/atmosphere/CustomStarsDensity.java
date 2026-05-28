package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import java.util.Random;

public class CustomStarsDensity extends GlacierMod {

    private final NumberSetting density = new NumberSetting("Density", "Number of stars", 0, 10000, 1500);
    private final NumberSetting brightness = new NumberSetting("Brightness", "Star brightness multiplier", 0.1, 3.0, 1.0);
    private final BooleanSetting twinkle = new BooleanSetting("Twinkle", "Enable star twinkling", true);
    private final NumberSetting twinkleSpeed = new NumberSetting("Twinkle Speed", "Twinkling animation speed", 0.1, 5.0, 1.0);

    private float twinkleOffset = 0;
    private int[] starPositions;
    private boolean needsRegen = false;
    private int lastDensity = -1;

    public CustomStarsDensity() {
        super("Custom Stars", "Adjust star density and brightness", Category.RENDER);
        addSettings(density, brightness, twinkle, twinkleSpeed);
    }

    @Override
    public void onEnable() {
        needsRegen = true;
    }

    @Override
    public void onDisable() { starPositions = null; }

    @Override
    public void onTick() {
        if (twinkle.getValue()) {
            twinkleOffset += (float) (twinkleSpeed.getValue() * 0.02);
        }
        int d = (int)(double) density.getValue();
        if (d != lastDensity || needsRegen) {
            generateStars(d);
            lastDensity = d;
            needsRegen = false;
        }
    }

    private void generateStars(int count) {
        Random rand = new Random(42L);
        starPositions = new int[count * 3];
        for (int i = 0; i < count; i++) {
            starPositions[i * 3] = rand.nextInt(360);
            starPositions[i * 3 + 1] = rand.nextInt(180) - 90;
            starPositions[i * 3 + 2] = rand.nextInt(100);
        }
    }

    public int[] getStarPositions() { return starPositions; }
    public float getTwinkleOffset() { return twinkleOffset; }
    public float getBrightness() { return (float)(double) brightness.getValue(); }
    public boolean isTwinkleEnabled() { return twinkle.getValue(); }
}
