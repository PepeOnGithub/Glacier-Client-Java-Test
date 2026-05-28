package net.glacierclient.modules.advanced.social;
import net.glacierclient.core.module.*;
import net.glacierclient.core.setting.*;
import net.glacierclient.core.event.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
public final class ChatLoggerMod extends GlacierMod {
    private static final Path LOG_FILE = Path.of("logs/glacier_chat.log");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    public ChatLoggerMod() { super("ChatLogger", "Logs all chat messages to a file", Category.QOL, -1); }
    @EventListen
    public void onChat(EventChat event) {
        try {
            Files.createDirectories(LOG_FILE.getParent());
            String line = "[" + LocalTime.now().format(FMT) + "] " + event.getMessage() + "\n";
            Files.writeString(LOG_FILE, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {}
    }
}
