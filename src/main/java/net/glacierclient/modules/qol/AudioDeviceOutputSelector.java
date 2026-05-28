package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.ModeSetting;
import net.glacierclient.core.settings.NumberSetting;

public class AudioDeviceOutputSelector extends GlacierMod {

    private final ModeSetting device = new ModeSetting("Device", "Audio output device", "Default", "Default", "Headset", "Speakers", "Virtual");
    private final NumberSetting bufferSize = new NumberSetting("Buffer Size", "Audio buffer size (samples)", 512, 8192, 2048);
    private final BooleanSetting exclusiveMode = new BooleanSetting("Exclusive Mode", "Exclusive audio device access", false);

    public AudioDeviceOutputSelector() {
        super("Audio Device Selector", "Select audio output device for game audio", Category.QOL);
        addSettings(device, bufferSize, exclusiveMode);
    }

    @Override
    public void onEnable() {
        applyDevice();
    }

    @Override
    public void onDisable() {
        // Restore default audio device
    }

    @Override
    public void onTick() {}

    private void applyDevice() {
        // Audio device switching via OpenAL/SoundManager API
    }

    public String getDevice() { return device.getValue(); }
    public int getBufferSize() { return (int)(double) bufferSize.getValue(); }
    public boolean isExclusiveMode() { return exclusiveMode.getValue(); }
}
