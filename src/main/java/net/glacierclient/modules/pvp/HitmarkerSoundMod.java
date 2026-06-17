package net.glacierclient.modules.pvp;

import net.glacierclient.core.event.EventListen;
import net.glacierclient.core.event.events.AttackEntityEvent;
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

    /** Plays the configured hitmarker sound whenever the local player lands a melee attack. */
    @EventListen
    public void onAttack(AttackEntityEvent event) {
        playHitSound(true);
    }

    public void playHitSound(boolean didDamage) {
        if (onlyOnDamage.getValue() && !didDamage) return;
        String mode = sound.getValue();
        if ("Silent".equals(mode)) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getSoundManager() == null) return;
        float vol = (float)(double) volume.getValue();
        float pitch = switch (mode) {
            case "Modern" -> 1.4f;
            case "Subtle" -> 1.9f;
            default        -> 1.0f + (float) Math.random() * 0.1f;
        };
        mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, pitch, vol));
    }
}
