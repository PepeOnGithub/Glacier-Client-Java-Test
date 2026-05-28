package net.glacierclient.launcher.ui;

import net.glacierclient.launcher.cdn.*;
import net.glacierclient.launcher.install.*;
import net.glacierclient.launcher.theme.LauncherTheme;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class VersionListPanel extends JPanel {
    private final CDNClient cdn;
    private final ClientInstaller installer;
    private final GameLauncher launcher;
    private final List<VersionCard> cards = new ArrayList<>();
    private VersionEntry selectedEntry;
    private final JLabel statusLabel;
    private final JProgressBar progressBar;

    public VersionListPanel(CDNClient cdn, ClientInstaller installer, GameLauncher launcher) {
        this.cdn = cdn;
        this.installer = installer;
        this.launcher = launcher;
        setLayout(new BorderLayout(0, 0));
        setBackground(LauncherTheme.BG);

        JLabel header = new JLabel("Select Version");
        header.setFont(LauncherTheme.FONT_TITLE);
        header.setForeground(LauncherTheme.TEXT);
        header.setBorder(BorderFactory.createEmptyBorder(24, 24, 16, 24));
        add(header, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBackground(LauncherTheme.BG);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        JScrollPane scroll = new JScrollPane(cardsPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setBackground(LauncherTheme.BG);
        scroll.getViewport().setBackground(LauncherTheme.BG);
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 0));
        bottom.setBackground(LauncherTheme.BG_PANEL);
        bottom.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        statusLabel = new JLabel("Loading versions...");
        statusLabel.setFont(LauncherTheme.FONT_SMALL);
        statusLabel.setForeground(LauncherTheme.TEXT_DIM);

        progressBar = new JProgressBar(0, 100);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(200, 8));
        progressBar.setBackground(LauncherTheme.BG_CARD);
        progressBar.setForeground(LauncherTheme.ACCENT);

        bottom.add(statusLabel, BorderLayout.WEST);
        bottom.add(progressBar, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        loadVersions(cardsPanel);
    }

    private void loadVersions(JPanel cardsPanel) {
        SwingWorker<VersionManifest, Void> worker = new SwingWorker<>() {
            @Override
            protected VersionManifest doInBackground() throws Exception {
                return cdn.fetchManifest();
            }

            @Override
            protected void done() {
                try {
                    VersionManifest manifest = get();
                    cardsPanel.removeAll();
                    cards.clear();
                    for (VersionEntry entry : manifest.getVersions()) {
                        VersionCard card = new VersionCard(entry, installer,
                                () -> selectEntry(entry, cards.indexOf(null)),
                                () -> installEntry(entry, cardsPanel, manifest));
                        cards.add(card);
                        cardsPanel.add(card);
                        cardsPanel.add(Box.createVerticalStrut(4));
                    }
                    cardsPanel.revalidate();
                    cardsPanel.repaint();
                    statusLabel.setText("Ready — " + manifest.getVersions().size() + " versions available");
                } catch (Exception ex) {
                    statusLabel.setText("Failed to load versions: " + ex.getMessage());
                    statusLabel.setForeground(LauncherTheme.RED);
                }
            }
        };
        worker.execute();
    }

    private void selectEntry(VersionEntry entry, int idx) {
        selectedEntry = entry;
        for (VersionCard c : cards) c.setSelected(false);
        if (idx >= 0 && idx < cards.size()) cards.get(idx).setSelected(true);
        statusLabel.setText("Selected: Glacier " + entry.getId());
        statusLabel.setForeground(LauncherTheme.TEXT_DIM);
    }

    private void installEntry(VersionEntry entry, JPanel cardsPanel, VersionManifest manifest) {
        progressBar.setVisible(true);
        progressBar.setValue(0);
        statusLabel.setForeground(LauncherTheme.ACCENT);

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                installer.install(entry,
                        status -> SwingUtilities.invokeLater(() -> statusLabel.setText(status)),
                        pct -> publish((int)(pct * 100)));
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                progressBar.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                progressBar.setValue(100);
                progressBar.setVisible(false);
                try {
                    get();
                    statusLabel.setText("Installed Glacier " + entry.getId() + " — click Play to launch");
                    statusLabel.setForeground(LauncherTheme.GREEN);
                    loadVersions(cardsPanel);
                } catch (Exception ex) {
                    statusLabel.setText("Install failed: " + ex.getMessage());
                    statusLabel.setForeground(LauncherTheme.RED);
                }
            }
        };
        worker.execute();
    }

    public VersionEntry getSelectedEntry() { return selectedEntry; }
}
