package net.glacierclient.core.client;

import net.glacierclient.api.bridge.VersionBridge;
import net.glacierclient.api.bridge.VersionBridgeProvider;
import net.glacierclient.core.module.CoreModule;

import java.util.List;

/**
 * Version-agnostic ClickGUI controller. A per-version {@code Screen} forwards render and click
 * events here; everything is drawn through the {@link VersionBridge}, so the same menu works on
 * every supported Minecraft version. Lists every {@link CoreModule} as a toggle row.
 */
public final class GlacierGui {

    private static final int PANEL_W  = 220;
    private static final int HEADER_H = 26;
    private static final int ROW_H    = 18;

    private GlacierGui() {}

    private static int panelH() { return HEADER_H + GlacierCore.get().getModules().size() * ROW_H + 12; }
    private static int panelX(VersionBridge b) { return (b.getScreenWidth() - PANEL_W) / 2; }
    private static int panelY(VersionBridge b) { return (b.getScreenHeight() - panelH()) / 2; }

    /** Render the menu. {@code ctx} is the version draw context passed through from the Screen. */
    public static void render(Object ctx, int mouseX, int mouseY) {
        if (!VersionBridgeProvider.isReady()) return;
        VersionBridge b = VersionBridgeProvider.get();

        b.drawRect(ctx, 0, 0, b.getScreenWidth(), b.getScreenHeight(), 0x88000000);

        int px = panelX(b), py = panelY(b), pw = PANEL_W, ph = panelH();
        b.drawRect(ctx, px, py, pw, ph, 0xF01E2024);
        b.drawRect(ctx, px, py, pw, HEADER_H, 0xFF26292E);
        b.drawText(ctx, "Glacier", px + 10, py + 9, 0xFF7289DA, true);
        b.drawText(ctx, b.getMinecraftVersion(), px + 10 + (int) b.getTextWidth("Glacier "), py + 9, 0xFFFFFFFF, true);

        List<CoreModule> mods = GlacierCore.get().getModules();
        int ry = py + HEADER_H + 3;
        for (CoreModule m : mods) {
            boolean hov = mouseX >= px && mouseX <= px + pw && mouseY >= ry && mouseY <= ry + ROW_H;
            if (hov) b.drawRect(ctx, px, ry, pw, ROW_H, 0x22FFFFFF);
            b.drawText(ctx, m.getName(), px + 10, ry + 5, m.isEnabled() ? 0xFFFFFFFF : 0xFF99AAB5, true);

            String st = m.isEnabled() ? "ON" : "OFF";
            int stw = (int) b.getTextWidth(st);
            b.drawRect(ctx, px + pw - stw - 18, ry + 3, stw + 12, ROW_H - 6, m.isEnabled() ? 0x3343B581 : 0x22FFFFFF);
            b.drawText(ctx, st, px + pw - stw - 12, ry + 5, m.isEnabled() ? 0xFF43B581 : 0xFF6E7681, true);
            ry += ROW_H;
        }
        b.drawText(ctx, "Right Shift / Esc to close", px + 10, py + ph - 11, 0xFF6E7681, false);
    }

    /** @return true if the click toggled a module row. */
    public static boolean mouseClicked(double mx, double my, int button) {
        if (!VersionBridgeProvider.isReady()) return false;
        VersionBridge b = VersionBridgeProvider.get();
        int px = panelX(b), py = panelY(b), pw = PANEL_W;
        int ry = py + HEADER_H + 3;
        for (CoreModule m : GlacierCore.get().getModules()) {
            if (mx >= px && mx <= px + pw && my >= ry && my <= ry + ROW_H) {
                m.toggle();
                return true;
            }
            ry += ROW_H;
        }
        return false;
    }
}
