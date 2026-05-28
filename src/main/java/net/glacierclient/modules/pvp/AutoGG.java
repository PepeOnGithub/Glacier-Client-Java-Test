package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.settings.StringSetting;
import net.minecraft.client.MinecraftClient;

public class AutoGG extends GlacierMod {

    private final StringSetting message = new StringSetting("Message", "Message to send", "gg");
    private final NumberSetting delay = new NumberSetting("Delay", "Delay in milliseconds", 0, 5000, 1000);
    private final BooleanSetting onWin = new BooleanSetting("On Win", "Send on win", true);
    private final BooleanSetting onLoss = new BooleanSetting("On Loss", "Send on loss", true);
    private final BooleanSetting onDraw = new BooleanSetting("On Draw", "Send on draw", false);

    private boolean pendingSend = false;
    private long sendAt = 0;

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

    public void triggerGG(String outcome) {
        if ("win".equals(outcome) && !onWin.getValue()) return;
        if ("loss".equals(outcome) && !onLoss.getValue()) return;
        if ("draw".equals(outcome) && !onDraw.getValue()) return;
        pendingSend = true;
        sendAt = System.currentTimeMillis() + (long)(double) delay.getValue();
    }
}
