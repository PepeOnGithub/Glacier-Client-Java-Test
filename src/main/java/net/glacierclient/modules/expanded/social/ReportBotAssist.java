package net.glacierclient.modules.expanded.social;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.event.EventListen;
import net.glacierclient.core.event.events.ChatReceiveEvent;
import net.glacierclient.core.event.events.ChatSendEvent;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;
import net.glacierclient.gui.notification.Notification;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ScreenshotRecorder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Collects evidence when the player issues a {@code /report} command: a timestamped log of recent
 * chat plus session context, an optional in-game screenshot, and optional zip packaging. Everything
 * is written under {@code config/glacierclient/reports/}. Read-only — it never alters the command.
 */
public class ReportBotAssist extends GlacierMod {

    private final BooleanSetting autoCollect = new BooleanSetting("Auto Collect", "Automatically collect evidence on /report", true);
    private final BooleanSetting autoScreenshot = new BooleanSetting("Auto Screenshot", "Take screenshot when report is issued", true);
    private final BooleanSetting packageEvidence = new BooleanSetting("Package Evidence", "Bundle all evidence into a zip file", false);
    private final NumberSetting chatHistoryLines = new NumberSetting("Chat History Lines", "Number of chat lines to include in report", 30, 10, 100);

    private static final Path REPORTS_DIR = Path.of("config/glacierclient/reports");
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final int BUFFER_CAP = 250;

    private final Deque<String> chatBuffer = new ArrayDeque<>();

    public ReportBotAssist() {
        super("Report Assist", "Auto-collect evidence when /report is used", Category.QOL);
        addSettings(autoCollect, autoScreenshot, packageEvidence, chatHistoryLines);
    }

    @Override
    public void onDisable() {
        chatBuffer.clear();
    }

    @EventListen
    public void onChatReceive(ChatReceiveEvent event) {
        if (event.getMessage() == null) return;
        chatBuffer.addLast("[" + LocalTime.now().format(TIME) + "] " + event.getMessage());
        while (chatBuffer.size() > BUFFER_CAP) chatBuffer.pollFirst();
    }

    @EventListen
    public void onChatSend(ChatSendEvent event) {
        if (!autoCollect.getValue() || event.getMessage() == null) return;
        String cmd = event.getMessage().trim();
        String lower = cmd.toLowerCase();
        if (!(lower.equals("/report") || lower.startsWith("/report "))) return;
        try {
            collectEvidence(cmd);
        } catch (Exception e) {
            notify("Report Assist", "Failed to collect evidence: " + e.getMessage(), Notification.Type.ERROR);
        }
    }

    private void collectEvidence(String reportCommand) throws IOException {
        MinecraftClient mc = MinecraftClient.getInstance();
        LocalDateTime now = LocalDateTime.now();
        Path dir = REPORTS_DIR.resolve("report_" + now.format(STAMP));
        Files.createDirectories(dir);

        int wanted = (int) (double) chatHistoryLines.getValue();
        List<String> all = new ArrayList<>(chatBuffer);
        List<String> recent = all.subList(Math.max(0, all.size() - wanted), all.size());

        StringBuilder sb = new StringBuilder();
        sb.append("Glacier Client — Report Evidence\n");
        sb.append("================================\n");
        sb.append("Timestamp : ").append(now).append('\n');
        sb.append("Command   : ").append(reportCommand).append('\n');
        sb.append("Player    : ").append(mc.getSession() != null ? mc.getSession().getUsername() : "unknown").append('\n');
        if (mc.getCurrentServerEntry() != null) {
            sb.append("Server    : ").append(mc.getCurrentServerEntry().address).append('\n');
        } else {
            sb.append("Server    : singleplayer / unknown\n");
        }
        if (mc.player != null) {
            sb.append("Position  : ")
              .append(String.format("%.1f, %.1f, %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ()))
              .append(" in ").append(mc.player.getWorld().getRegistryKey().getValue()).append('\n');
        }
        sb.append("\nRecent chat (").append(recent.size()).append(" lines):\n");
        sb.append("--------------------------------\n");
        for (String line : recent) sb.append(line).append('\n');

        Path evidenceFile = dir.resolve("report.txt");
        Files.writeString(evidenceFile, sb.toString(), StandardCharsets.UTF_8);

        if (autoScreenshot.getValue()) {
            mc.execute(() -> {
                try {
                    ScreenshotRecorder.saveScreenshot(dir.toFile(), "screenshot.png", mc.getFramebuffer(), text -> {});
                } catch (Throwable ignored) {}
            });
        }

        if (packageEvidence.getValue()) {
            zipDirectory(dir);
        }

        notify("Report Assist", "Saved evidence to " + dir.getFileName(), Notification.Type.SUCCESS);
    }

    private void zipDirectory(Path dir) {
        Path zip = dir.resolveSibling(dir.getFileName() + ".zip");
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zip));
             var stream = Files.list(dir)) {
            for (Path p : (Iterable<Path>) stream::iterator) {
                if (!Files.isRegularFile(p)) continue;
                zos.putNextEntry(new ZipEntry(p.getFileName().toString()));
                Files.copy(p, zos);
                zos.closeEntry();
            }
        } catch (IOException ignored) {}
    }

    private void notify(String title, String msg, Notification.Type type) {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc != null && gc.getNotificationSystem() != null) {
            gc.getNotificationSystem().send(title, msg, type);
        }
    }
}
