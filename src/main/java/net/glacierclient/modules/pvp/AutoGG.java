package net.glacierclient.modules.pvp;

import net.glacierclient.core.event.EventListen;
import net.glacierclient.core.event.events.ChatReceiveEvent;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.settings.StringSetting;
import net.minecraft.client.MinecraftClient;

import java.util.Locale;

public class AutoGG extends GlacierMod {

    private final StringSetting message = new StringSetting("Message", "Message to send", "gg");
    private final NumberSetting delay = new NumberSetting("Delay", "Delay in milliseconds", 0, 5000, 1000);
    private final BooleanSetting onWin = new BooleanSetting("On Win", "Send on win", true);
    private final BooleanSetting onLoss = new BooleanSetting("On Loss", "Send on loss", true);
    private final BooleanSetting onDraw = new BooleanSetting("On Draw", "Send on draw", false);

    // Game-end chat patterns (covers common minigame servers like Hypixel/Bedwars/Duels).
    private static final String[] WIN_PATTERNS  = {"victory", "you won", "1st place", "winner:", " winner ", "you placed #1"};
    private static final String[] LOSS_PATTERNS = {"defeat", "you lost", "game over", "you died", "2nd place", "3rd place"};
    private static final String[] DRAW_PATTERNS = {"draw", "tie game", "stalemate"};

    private boolean pendingSend = false;
    private long sendAt = 0;
    private long lastTrigger = 0; // de-dupe burst of end-of-game lines

    public AutoGG() {
        super("Auto GG", "Send GG message when game ends", Category.PVP);
        addSettings(message, delay, onWin, onLoss, onDraw);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() { pendingSend = false; }

    @Override
    public void onTick() {
        if (pendingSend && System.currentTimeMillis() >= sendAt) {
            pendingSend = false;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null) mc.player.networkHandler.sendChatMessage(message.getValue());
        }
    }

    /** Detects game-end lines in chat and schedules the configured message. */
    @EventListen
    public void onChatReceive(ChatReceiveEvent event) {
        if (event.getMessage() == null) return;
        // Only react once per ~8s so a burst of end-screen lines doesn't queue multiple sends.
        if (pendingSend || System.currentTimeMillis() - lastTrigger < 8000) return;
        String msg = event.getMessage().toLowerCase(Locale.ROOT);
        if (containsAny(msg, WIN_PATTERNS)) triggerGG("win");
        else if (containsAny(msg, DRAW_PATTERNS)) triggerGG("draw");
        else if (containsAny(msg, LOSS_PATTERNS)) triggerGG("loss");
    }

    private static boolean containsAny(String haystack, String[] needles) {
        for (String n : needles) if (haystack.contains(n)) return true;
        return false;
    }

    public void triggerGG(String outcome) {
        if ("win".equals(outcome) && !onWin.getValue()) return;
        if ("loss".equals(outcome) && !onLoss.getValue()) return;
        if ("draw".equals(outcome) && !onDraw.getValue()) return;
        pendingSend = true;
        lastTrigger = System.currentTimeMillis();
        sendAt = lastTrigger + (long)(double) delay.getValue();
    }
}
