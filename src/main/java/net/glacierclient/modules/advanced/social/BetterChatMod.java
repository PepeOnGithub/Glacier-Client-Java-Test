package net.glacierclient.modules.advanced.social;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
public final class BetterChatMod extends GlacierMod {
    private final NumberSetting history = new NumberSetting("HistorySize", 200, 50, 1000);
    private final BooleanSetting infiniteHistory = new BooleanSetting("InfiniteHistory", true);
    private final BooleanSetting smooth = new BooleanSetting("SmoothScroll", true);
    private final BooleanSetting compact = new BooleanSetting("CompactEmptyLines", true);
    public BetterChatMod() {
        super("BetterChat", "Enhances the chat window with more history and smooth scrolling", Category.QOL, -1);
        addSettings(history, infiniteHistory, smooth, compact);
    }
}
