package net.glacierclient.modules.pvp;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.core.settings.StringSetting;
import net.minecraft.client.MinecraftClient;

public class AutoLeave extends GlacierMod {

    private final BooleanSetting onDeath = new BooleanSetting("On Death", "Leave on death", false);
    private final BooleanSetting onGameEnd = new BooleanSetting("On Game End", "Leave when game ends", true);
    private final NumberSetting delay = new NumberSetting("Delay", "Delay before leaving (ms)", 0, 5000, 2000);
    private final StringSetting command = new StringSetting("Command", "Command to run on leave", "/lobby");

    private boolean pending = false;
    private long leaveAt = 0;

    public AutoLeave() {
        super("Auto Leave", "Automatically leave game when it ends", Category.PVP);
        addSettings(onDeath, onGameEnd, delay, command);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() { pending = false; }

    @Override
    public void onTick() {
        if (pending && System.currentTimeMillis() >= leaveAt) {
            pending = false;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null) mc.player.networkHandler.sendChatMessage(command.getValue());
        }
    }

    public void triggerLeave(boolean isDeath) {
        if (isDeath && !onDeath.getValue()) return;
        if (!isDeath && !onGameEnd.getValue()) return;
        pending = true;
        leaveAt = System.currentTimeMillis() + (long) delay.getValue();
    }
}
