package net.glacierclient.launcher.theme;

import java.awt.*;

public final class LauncherTheme {
    public static final Color BG           = new Color(0x23272A);
    public static final Color BG_PANEL     = new Color(0x2C2F33);
    public static final Color BG_CARD      = new Color(0x32363B);
    public static final Color ACCENT       = new Color(0x7289DA);
    public static final Color ACCENT_HOVER = new Color(0x8EA0E0);
    public static final Color ACCENT_BG    = new Color(0x7289DA).darker().darker();
    public static final Color TEXT         = Color.WHITE;
    public static final Color TEXT_DIM     = new Color(0x99AAB5);
    public static final Color GREEN        = new Color(0x43B581);
    public static final Color RED          = new Color(0xF04747);
    public static final Color ORANGE       = new Color(0xFAA61A);
    public static final Color SEPARATOR    = new Color(0x40444B);

    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO    = new Font("Consolas", Font.PLAIN, 11);

    public static void applyDefaults() {
        javax.swing.UIManager.put("Panel.background", BG);
        javax.swing.UIManager.put("OptionPane.background", BG_PANEL);
        javax.swing.UIManager.put("TextField.background", BG_CARD);
        javax.swing.UIManager.put("TextField.foreground", TEXT);
        javax.swing.UIManager.put("TextField.caretForeground", ACCENT);
        javax.swing.UIManager.put("Label.foreground", TEXT);
        javax.swing.UIManager.put("ScrollPane.background", BG);
        javax.swing.UIManager.put("Viewport.background", BG);
    }

    private LauncherTheme() {}
}
