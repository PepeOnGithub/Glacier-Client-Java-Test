package net.glacierclient.launcher.ui;

import net.glacierclient.launcher.theme.LauncherTheme;
import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import java.util.prefs.Preferences;

public final class SettingsPanel extends JPanel {
    private static final Preferences PREFS = Preferences.userNodeForPackage(SettingsPanel.class);
    private static final String KEY_RAM  = "ram_mb";
    private static final String KEY_JAVA = "java_path";

    private final JSlider ramSlider;
    private final JLabel ramLabel;
    private final JTextField javaPathField;

    public SettingsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(LauncherTheme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        addHeading("Settings");
        addSeparator();

        addSubHeading("Memory Allocation");
        int savedRam = PREFS.getInt(KEY_RAM, 2048);
        ramSlider = new JSlider(512, 8192, savedRam);
        ramSlider.setMajorTickSpacing(1024);
        ramSlider.setSnapToTicks(false);
        ramSlider.setBackground(LauncherTheme.BG);
        ramSlider.setForeground(LauncherTheme.ACCENT);
        ramLabel = new JLabel(savedRam + " MB");
        ramLabel.setFont(LauncherTheme.FONT_BODY);
        ramLabel.setForeground(LauncherTheme.TEXT);
        ramSlider.addChangeListener(e -> {
            ramLabel.setText(ramSlider.getValue() + " MB");
            PREFS.putInt(KEY_RAM, ramSlider.getValue());
        });

        JPanel ramRow = new JPanel(new BorderLayout(12, 0));
        ramRow.setBackground(LauncherTheme.BG);
        ramRow.add(ramSlider, BorderLayout.CENTER);
        ramRow.add(ramLabel, BorderLayout.EAST);
        ramRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        add(ramRow);
        add(Box.createVerticalStrut(16));

        addSubHeading("Java Executable Path");
        addNote("Leave blank to use the bundled JVM");
        String savedJava = PREFS.get(KEY_JAVA, "");
        javaPathField = new JTextField(savedJava);
        javaPathField.setFont(LauncherTheme.FONT_MONO);
        javaPathField.setBackground(LauncherTheme.BG_CARD);
        javaPathField.setForeground(LauncherTheme.TEXT);
        javaPathField.setCaretColor(LauncherTheme.ACCENT);
        javaPathField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LauncherTheme.SEPARATOR),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        javaPathField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        javaPathField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { save(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { save(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { save(); }
            private void save() { PREFS.put(KEY_JAVA, javaPathField.getText()); }
        });
        add(javaPathField);
        add(Box.createVerticalStrut(8));

        GlacierButton browseBtn = new GlacierButton("Browse...", LauncherTheme.BG_CARD, LauncherTheme.BG_PANEL);
        browseBtn.setForeground(LauncherTheme.TEXT_DIM);
        browseBtn.setPreferredSize(new Dimension(100, 32));
        browseBtn.setMaximumSize(new Dimension(120, 32));
        browseBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        browseBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                javaPathField.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });
        add(browseBtn);
        add(Box.createVerticalGlue());
    }

    private void addHeading(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(LauncherTheme.FONT_TITLE);
        lbl.setForeground(LauncherTheme.TEXT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lbl);
        add(Box.createVerticalStrut(8));
    }

    private void addSubHeading(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(LauncherTheme.FONT_HEADING);
        lbl.setForeground(LauncherTheme.TEXT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lbl);
        add(Box.createVerticalStrut(4));
    }

    private void addNote(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(LauncherTheme.FONT_SMALL);
        lbl.setForeground(LauncherTheme.TEXT_DIM);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lbl);
        add(Box.createVerticalStrut(6));
    }

    private void addSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(LauncherTheme.SEPARATOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        add(sep);
        add(Box.createVerticalStrut(16));
    }

    public int getRamMb() { return ramSlider.getValue(); }
    public String getJavaPath() { return javaPathField.getText().trim(); }
}
