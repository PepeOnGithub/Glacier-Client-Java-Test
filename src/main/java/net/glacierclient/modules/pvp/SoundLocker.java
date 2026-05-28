package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class SoundLocker extends GlacierMod {

    private final BooleanSetting muteExplosions = new BooleanSetting("Mute Explosions", "Mute explosion sounds", false);
    private final BooleanSetting muteFirework = new BooleanSetting("Mute Firework", "Mute firework sounds", false);
    private final BooleanSetting muteAmbient = new BooleanSetting("Mute Ambient", "Mute ambient sounds", false);
    private final BooleanSetting muteCreeper = new BooleanSetting("Mute Creeper", "Mute creeper sounds", false);
    private final BooleanSetting mutePhantom = new BooleanSetting("Mute Phantom", "Mute phantom sounds", false);
    private final NumberSetting masterMultiplier = new NumberSetting("Master Volume", "Master volume multiplier", 0.0, 2.0, 1.0);

    public SoundLocker() {
        super("Sound Locker", "Mute specific annoying game sounds", Category.PVP);
        addSettings(muteExplosions, muteFirework, muteAmbient, muteCreeper, mutePhantom, masterMultiplier);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public boolean isMuted(String soundId) {
        if (muteExplosions.getValue() && soundId.contains("explode")) return true;
        if (muteFirework.getValue() && soundId.contains("firework")) return true;
        if (muteAmbient.getValue() && soundId.contains("ambient")) return true;
        if (muteCreeper.getValue() && soundId.contains("creeper")) return true;
        if (mutePhantom.getValue() && soundId.contains("phantom")) return true;
        return false;
    }

    public float getMasterMultiplier() { return (float)(double) masterMultiplier.getValue(); }
}
