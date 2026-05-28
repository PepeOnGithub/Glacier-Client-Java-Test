package net.glacierclient.launcher.ui;

import net.glacierclient.launcher.cdn.CDNClient;
import net.glacierclient.launcher.cdn.VersionEntry;
import net.glacierclient.launcher.install.ClientInstaller;
import net.glacierclient.launcher.install.GameLauncher;
import net.glacierclient.launcher.theme.LauncherTheme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public final class LauncherWindow extends JFrame {
    private final CDNClient cdn = new CDNClient();
    private final ClientInstaller installer = new ClientInstaller(cdn);
    private final GameLauncher gameLauncher = new GameLauncher(installer);

    private VersionListPanel versionList;
    private SettingsPanel settingsPanel;
    private JLabel titleLabel;

    public LauncherWindow() {
        super("Glacier Launcher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 620);
        setMinimumSize(new Dimension(720, 500));
        setLocationRelativeTo(null);
        setUndecorated(true);
        setBackground(LauncherTheme.BG);

        LauncherTheme.applyDefaults();

        initUI();
        setVisible(true);

        addDragging();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(LauncherTheme.BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        root.setOpaque(false);
        setContentPane(root);

        JPanel titleBar = buildTitleBar();
        JPanel sidebar = buildSidebar();

        versionList = new VersionListPanel(cdn, installer, gameLauncher);
        settingsPanel = new SettingsPanel();
        settingsPanel.setVisible(false);

        JPanel content = new JPanel(new CardLayout());
        content.setBackground(LauncherTheme.BG);
        content.add(versionList, "versions");
        content.add(settingsPanel, "settings");

        root.add(titleBar, BorderLayout.NORTH);
        root.add(sidebar, BorderLayout.WEST);
        root.add(content, BorderLayout.CENTER);

        JPanel bottomBar = buildBottomBar(content);
        root.add(bottomBar, BorderLayout.SOUTH);

        getRootPane().setBorder(BorderFactory.createLineBorder(LauncherTheme.SEPARATOR, 1));
    }

    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(LauncherTheme.BG_PANEL);
        bar.setPreferredSize(new Dimension(0, 48));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 12));

        JLabel logo = new JLabel("❄ Glacier");
        logo.setFont(LauncherTheme.FONT_HEADING);
        logo.setForeground(LauncherTheme.ACCENT);

        JPanel windowButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        windowButtons.setOpaque(false);
        JButton min = windowButton("—", LauncherTheme.ORANGE);
        JButton close = windowButton("✕", LauncherTheme.RED);
        min.addActionListener(e -> setState(JFrame.ICONIFIED));
        close.addActionListener(e -> System.exit(0));
        windowButtons.add(min);
        windowButtons.add(close);

        bar.add(logo, BorderLayout.WEST);
        bar.add(windowButtons, BorderLayout.EAST);
        return bar;
    }

    private JButton windowButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg.darker());
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(20, 20));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        return btn;
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(LauncherTheme.BG_PANEL);
        side.setPreferredSize(new Dimension(180, 0));
        side.setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));

        JLabel navLabel = new JLabel("NAVIGATION");
        navLabel.setFont(LauncherTheme.FONT_SMALL);
        navLabel.setForeground(LauncherTheme.TEXT_DIM);
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(navLabel);
        side.add(Box.createVerticalStrut(8));

        String[] navItems = {"Versions", "Settings", "News"};
        for (String item : navItems) {
            GlacierButton btn = new GlacierButton(item, LauncherTheme.BG_PANEL, LauncherTheme.BG_CARD);
            btn.setForeground(LauncherTheme.TEXT);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            side.add(btn);
            side.add(Box.createVerticalStrut(4));
        }

        side.add(Box.createVerticalGlue());

        JLabel ver = new JLabel("Glacier Launcher v1.0.0");
        ver.setFont(LauncherTheme.FONT_SMALL);
        ver.setForeground(LauncherTheme.TEXT_DIM);
        ver.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(ver);

        return side;
    }

    private JPanel buildBottomBar(JPanel content) {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setBackground(LauncherTheme.BG_PANEL);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        bar.setPreferredSize(new Dimension(0, 56));

        GlacierButton playBtn = new GlacierButton("Launch Game");
        playBtn.setPreferredSize(new Dimension(160, 36));
        playBtn.addActionListener(e -> {
            VersionEntry selected = versionList.getSelectedEntry();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a version first.", "No version selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!installer.isInstalled(selected)) {
                JOptionPane.showMessageDialog(this, "Please install this version first.", "Not installed", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                gameLauncher.launch(selected, settingsPanel.getJavaPath(), settingsPanel.getRamMb());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Launch failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        bar.add(playBtn, BorderLayout.EAST);
        return bar;
    }

    private Point dragOrigin;

    private void addDragging() {
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { dragOrigin = e.getPoint(); }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (dragOrigin != null) {
                    Point loc = getLocation();
                    setLocation(loc.x + e.getX() - dragOrigin.x, loc.y + e.getY() - dragOrigin.y);
                }
            }
        });
    }
}
