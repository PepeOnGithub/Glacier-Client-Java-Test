package net.glacierclient.launcher.ui;

import net.glacierclient.launcher.theme.LauncherTheme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public final class GlacierButton extends JButton {
    private boolean hovered = false;
    private final Color baseColor;
    private final Color hoverColor;

    public GlacierButton(String text, Color base, Color hover) {
        super(text);
        this.baseColor = base;
        this.hoverColor = hover;
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(LauncherTheme.TEXT);
        setFont(LauncherTheme.FONT_BODY);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
        });
    }

    public GlacierButton(String text) {
        this(text, LauncherTheme.ACCENT, LauncherTheme.ACCENT_HOVER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color bg = hovered ? hoverColor : baseColor;
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g2);
        g2.dispose();
    }
}
