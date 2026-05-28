package net.glacierclient.modules.advanced.atmosphere;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;

public class CustomHitSounds extends GlacierMod {

    private final ModeSetting sound = new ModeSetting("Sound", "Hit sound style", new String[]{"Classic", "Modern", "Crunch", "Pop", "Silent"}, "Classic");
    private final NumberSetting volume = new NumberSetting("Volume", "Hit sound volume", 0.0, 2.0, 1.0);
    private final NumberSetting pitch = new NumberSetting("Pitch", "Hit sound pitch", 0.5, 2.0, 1.0);

    public CustomHitSounds() {
        super("Custom Hit Sounds", "Replace hit sound with custom effects", Category.RENDER);
        addSettings(sound, volume, pitch);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
    @Override public void onTick() {}

    public void playHitSound() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        if ("Silent".equals(sound.getValue())) return;
        float vol = (float)(double) volume.getValue();
        float pit = (float)(double) pitch.getValue();
        var soundEvent = switch (sound.getValue()) {
            case "Classic" -> SoundEvents.ENTITY_PLAYER_HURT;
            case "Modern" -> SoundEvents.ENTITY_GENERIC_HURT;
            case "Crunch" -> SoundEvents.ENTITY_BONE_MEAL_USE;
            case "Pop" -> SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON;
            default -> SoundEvents.ENTITY_PLAYER_HURT;
        };
        mc.player.playSound(soundEvent, vol, pit);
    }
}
