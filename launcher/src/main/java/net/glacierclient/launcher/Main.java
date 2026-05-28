package net.glacierclient.launcher;

import net.glacierclient.launcher.ui.LauncherWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(LauncherWindow::new);
    }
}
