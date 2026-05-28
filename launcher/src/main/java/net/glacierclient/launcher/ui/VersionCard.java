package net.glacierclient.launcher.ui;

import net.glacierclient.launcher.cdn.VersionEntry;
import net.glacierclient.launcher.install.ClientInstaller;
import net.glacierclient.launcher.theme.LauncherTheme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public final class VersionCard extends JPanel {
    private boolean selected = false;
    private boolean hovered = false;

    public VersionCard(VersionEntry entry, ClientInstaller installer, Runnable onSelect, Runnable onInstall) {
        setLayout(new BorderLayout(12, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        setPreferredSize(new Dimension(0, 72));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel version = new JLabel(entry.getId());
        version.setFont(LauncherTheme.FONT_HEADING);
        version.setForeground(LauncherTheme.TEXT);

        JLabel loader = new JLabel(entry.getLoaderLabel() + " • Java " + entry.getJavaVersion());
        loader.setFont(LauncherTheme.FONT_SMALL);
        loader.setForeground(LauncherTheme.TEXT_DIM);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);
        textPanel.add(version);
        textPanel.add(loader);

        boolean installed = installer.isInstalled(entry);
        Color btnBase = installed ? LauncherTheme.GREEN : LauncherTheme.ACCENT;
        Color btnHover = installed ? LauncherTheme.GREEN.brighter() : LauncherTheme.ACCENT_HOVER;
        String btnText = installed ? "Play" : "Install";
        GlacierButton btn = new GlacierButton(btnText, btnBase, btnHover);
        btn.setPreferredSize(new Dimension(80, 36));
        btn.addActionListener(e -> {
            if (installed) onSelect.run();
            else onInstall.run();
        });

        add(textPanel, BorderLayout.CENTER);
        add(btn, BorderLayout.EAST);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
            @Override public void mouseClicked(MouseEvent e) { onSelect.run(); }
        });
    }

    public void setSelected(boolean sel) { this.selected = sel; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color bg = selected ? new Color(0x7289DA20, true) : hovered ? new Color(0xFFFFFF0A, true) : LauncherTheme.BG_CARD;
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        if (selected) {
            g2.setColor(LauncherTheme.ACCENT);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 12, 12));
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
