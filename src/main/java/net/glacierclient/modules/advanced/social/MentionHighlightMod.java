package net.glacierclient.modules.advanced.social;
import net.glacierclient.core.module.*;
import net.glacierclient.core.settings.*;
import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import net.glacierclient.core.theme.GlacierTheme;
import net.minecraft.client.MinecraftClient;
public final class MentionHighlightMod extends GlacierMod {
    private final BooleanSetting sound = new BooleanSetting("Sound", true);
    private final BooleanSetting nameOnly = new BooleanSetting("NameOnly", true);
    public MentionHighlightMod() {
        super("MentionHighlight", "Highlights chat messages that mention your name", Category.QOL, -1);
        addSettings(sound, nameOnly);
    }
    @EventListen
    public void onChat(ChatReceiveEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        String name = mc.player.getName().getString();
        if (event.getMessage().contains(name)) {
            event.setHighlightColor(GlacierTheme.ACCENT);
            if (sound.get()) mc.player.playSound(net.minecraft.sound.SoundEvents.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
        }
    }
}
