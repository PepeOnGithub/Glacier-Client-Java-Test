package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public class HitmarkerSoundMod extends GlacierMod {

    private final ModeSetting sound = new ModeSetting("Sound", "Hitmarker sound type", "Classic", "Classic", "Modern", "Subtle", "Silent");
    private final NumberSetting volume = new NumberSetting("Volume", "Sound volume", 0.0, 2.0, 1.0);
    private final BooleanSetting onlyOnDamage = new BooleanSetting("Only On Damage", "Play only when dealing damage", false);

    public HitmarkerSoundMod() {
        super("Hit Marker Sound", "Play a sound when you hit an entity", Category.PVP);
        addSettings(sound, volume, onlyOnDamage);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public void playHitSound(boolean didDamage) {
        if (onlyOnDamage.getValue() && !didDamage) return;
        if ("Silent".equals(sound.getValue())) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getSoundManager() == null) return;
        float vol = (float)(double) volume.getValue();
        mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f + (float)Math.random() * 0.1f, vol));
    }
}
