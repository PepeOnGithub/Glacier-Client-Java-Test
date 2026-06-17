package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrashReportSaver extends GlacierMod {

    private final BooleanSetting autoSave = new BooleanSetting("Auto Save", "Automatically save crash reports", true);
    private final NumberSetting maxSaved = new NumberSetting("Max Saved", "Max crash reports to keep", 5, 50, 20);
    private final BooleanSetting includeLogs = new BooleanSetting("Include Logs", "Include log files in crash report", true);
    private final BooleanSetting openOnCrash = new BooleanSetting("Open On Crash", "Open folder on crash", false);

    public CrashReportSaver() {
        super("Crash Report Saver", "Save and manage crash reports", Category.QOL);
        addSettings(autoSave, maxSaved, includeLogs, openOnCrash);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {}

    public void saveCrashReport(String content) {
        if (!autoSave.getValue()) return;
        File dir = new File("crash-reports/glacier");
        dir.mkdirs();
        String name = "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".txt";
        StringBuilder body = new StringBuilder(content);
        // Include Logs: append the tail of latest.log for context.
        if (includeLogs.getValue()) {
            File log = new File("logs/latest.log");
            if (log.exists()) {
                try {
                    java.util.List<String> lines = java.nio.file.Files.readAllLines(log.toPath());
                    int from = Math.max(0, lines.size() - 200);
                    body.append("\n\n===== latest.log (tail) =====\n");
                    for (int i = from; i < lines.size(); i++) body.append(lines.get(i)).append('\n');
                } catch (Exception ignored) {}
            }
        }
        try (FileWriter fw = new FileWriter(new File(dir, name))) {
            fw.write(body.toString());
        } catch (IOException ignored) {}
        pruneOldReports(dir);
        // Open On Crash: reveal the saved-reports folder in the OS file browser.
        if (openOnCrash.getValue()) {
            try {
                if (java.awt.Desktop.isDesktopSupported())
                    java.awt.Desktop.getDesktop().open(dir);
            } catch (Throwable ignored) {}
        }
    }

    private void pruneOldReports(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;
        java.util.Arrays.sort(files, java.util.Comparator.comparingLong(File::lastModified));
        int max = (int)(double) maxSaved.getValue();
        for (int i = 0; i < files.length - max; i++) files[i].delete();
    }
}
