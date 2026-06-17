package net.glacierclient.web;

import net.glacierclient.GlacierClient;
import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.settings.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.Text;

import java.util.Locale;

/**
 * Routes web-UI actions (the {@code data-act} strings sent by bridge.js / window.glacier.send) to
 * native Minecraft behaviour. Module <em>logic</em> stays here in Java — the HTML only sends intents.
 *
 * <p>Every handler is wrapped so a bad action can never crash the client. This class has no Ultralight
 * dependency, so it is fully usable/testable on its own.</p>
 */
public final class GlacierBridge {

    private GlacierBridge() {}

    /** Entry point invoked from JS (window.glacierBridge.send) or any host. */
    public static void onAction(String action) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || action == null || action.isEmpty()) return;
        mc.execute(() -> { try { dispatch(mc, action.trim()); } catch (Throwable t) {
            System.err.println("[Glacier] bridge action failed: " + action + " -> " + t);
        }});
    }

    private static void dispatch(MinecraftClient mc, String action) {
        // parameterised actions: "verb:args"
        int colon = action.indexOf(':');
        String verb = colon < 0 ? action : action.substring(0, colon);
        String args = colon < 0 ? "" : action.substring(colon + 1);
        Screen parent = mc.currentScreen;

        switch (verb) {
            // ---- title ----
            case "play" -> mc.setScreen(new SelectWorldScreen(parent != null ? parent : new TitleScreen()));
            case "options" -> mc.setScreen(new OptionsScreen(parent, mc.options));
            case "marketplace", "dressing", "changelog", "event", "achievements", "inbox", "info", "profile" ->
                    toast(mc, capitalize(verb) + " — coming soon");
            case "discord" -> openUrl("https://discord.glacierclient.xyz");
            case "refresh" -> mc.reloadResources();
            case "quit" -> {
                if (mc.world != null) { // pause "Save & Quit"
                    mc.world.disconnect();
                    mc.disconnect();
                    mc.setScreen(new TitleScreen());
                } else {
                    mc.scheduleStop();
                }
            }
            case "resume" -> mc.setScreen(null);
            case "sidebar" -> {} // handled by the web UI itself
            case "screenshot" -> ScreenshotRecorder.saveScreenshot(mc.runDirectory, mc.getFramebuffer(),
                    text -> mc.execute(() -> { if (mc.inGameHud != null) mc.inGameHud.getChatHud().addMessage(text); }));

            // ---- modules ----
            case "toggle" -> { GlacierMod m = findBySlug(args); if (m != null) m.toggle(); }
            case "set" -> applySetting(args); // slug:Name=value
            case "hudpos" -> {} // x/y persisted via HUDMod elsewhere; accepted no-op for now
            case "hudreset" -> {}

            // ---- cosmetics (stub until the cosmetic backend is wired) ----
            case "buy", "equip" -> toast(mc, capitalize(verb) + ": " + args);

            default -> { /* unknown action ignored */ }
        }
    }

    /** "slug:SettingName=value" */
    private static void applySetting(String args) {
        int c = args.indexOf(':');
        if (c < 0) return;
        String slug = args.substring(0, c);
        String rest = args.substring(c + 1);
        int eq = rest.indexOf('=');
        if (eq < 0) return;
        String name = rest.substring(0, eq);
        String value = rest.substring(eq + 1);
        GlacierMod mod = findBySlug(slug);
        if (mod == null) return;
        if (name.equalsIgnoreCase("Enabled")) { mod.setEnabled(Boolean.parseBoolean(value)); return; }
        Setting<?> s = mod.getSetting(name);
        if (s == null) return;
        try {
            if (s instanceof BooleanSetting b) b.setValue(Boolean.parseBoolean(value));
            else if (s instanceof NumberSetting n) n.setValue(Double.parseDouble(value));
            else if (s instanceof ModeSetting md) md.setValue(value);
            else if (s instanceof StringSetting st) st.setValue(value);
            else if (s instanceof ColorSetting col) col.setValue(parseColor(value));
        } catch (Exception ignored) {}
    }

    private static int parseColor(String v) {
        v = v.trim();
        if (v.startsWith("#")) return 0xFF000000 | Integer.parseInt(v.substring(1), 16);
        return Integer.decode(v);
    }

    private static GlacierMod findBySlug(String slug) {
        if (GlacierClient.getInstance() == null) return null;
        for (GlacierMod m : GlacierClient.getInstance().getModuleManager().getModules()) {
            if (slug(m.getName()).equals(slug)) return m;
        }
        return null;
    }

    private static String slug(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private static String capitalize(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static void toast(MinecraftClient mc, String msg) {
        if (mc.inGameHud != null && mc.world != null) mc.inGameHud.getChatHud().addMessage(Text.literal("[Glacier] " + msg));
        else System.out.println("[Glacier] " + msg);
    }

    private static void openUrl(String url) {
        try { net.minecraft.util.Util.getOperatingSystem().open(new java.net.URI(url)); } catch (Exception ignored) {}
    }
}
