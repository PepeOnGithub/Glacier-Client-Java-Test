package net.glacierclient.modules.advanced.social;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
import net.glacierclient.core.event.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;
public final class ChatSoundsMod extends GlacierMod {
    private final NumberSetting volume = new NumberSetting("Volume", 0.5f, 0.0f, 1.0f);
    public ChatSoundsMod() {
        super("ChatSounds", "Plays a sound when a chat message is received", Category.QOL, -1);
        addSettings(volume);
    }
    @EventListen
    public void onChat(EventChat event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) mc.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), (float) volume.get(), 1.0f);
    }
}
