package net.glacierclient.common;

import java.util.logging.*;

public final class GlacierLogger {

    private static final Logger LOGGER = Logger.getLogger("GlacierClient");

    static {
        LOGGER.setLevel(Level.ALL);
    }

    private GlacierLogger() {}

    public static void info(String msg) { LOGGER.info("[Glacier] " + msg); }
    public static void warn(String msg) { LOGGER.warning("[Glacier] " + msg); }
    public static void error(String msg) { LOGGER.severe("[Glacier] " + msg); }
    public static void debug(String msg) { LOGGER.fine("[Glacier] " + msg); }
    public static void error(String msg, Throwable t) { LOGGER.log(Level.SEVERE, "[Glacier] " + msg, t); }
}
