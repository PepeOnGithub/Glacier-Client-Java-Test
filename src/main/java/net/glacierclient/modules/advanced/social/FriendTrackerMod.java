package net.glacierclient.modules.advanced.social;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
import net.glacierclient.core.event.*;
import java.util.*;
public final class FriendTrackerMod extends GlacierMod {
    private final StringSetting friends = new StringSetting("Friends", "");
    private final BooleanSetting joinNotify = new BooleanSetting("JoinNotify", true);
    private final Set<String> online = new HashSet<>();
    public FriendTrackerMod() {
        super("FriendTracker", "Notifies when friends join or leave the server", Category.QOL, -1);
        addSettings(friends, joinNotify);
    }
    @EventListen
    public void onPlayerJoin(EventPlayerJoin event) {
        if (!joinNotify.get()) return;
        String name = event.getPlayerName();
        Set<String> list = new HashSet<>(Arrays.asList(friends.get().split(",")));
        if (list.contains(name) && online.add(name)) {
            net.minecraft.client.MinecraftClient.getInstance().player
                    .sendMessage(net.minecraft.text.Text.literal("[Glacier] Friend online: " + name), false);
        }
    }
    @EventListen
    public void onPlayerLeave(EventPlayerLeave event) { online.remove(event.getPlayerName()); }
}
