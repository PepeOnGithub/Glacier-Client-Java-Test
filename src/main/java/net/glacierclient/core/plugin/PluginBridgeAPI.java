package net.glacierclient.core.plugin;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.ModuleManager;
import java.util.*;

public final class PluginBridgeAPI {
    private static final PluginBridgeAPI INSTANCE = new PluginBridgeAPI();
    public static PluginBridgeAPI get() { return INSTANCE; }

    private final Map<String, GlacierPlugin> plugins = new LinkedHashMap<>();
    private ModuleManager moduleManager;

    public void init(ModuleManager mm) { this.moduleManager = mm; }

    public void registerPlugin(GlacierPlugin plugin) {
        if (plugins.containsKey(plugin.getId())) throw new IllegalStateException("Plugin already registered: " + plugin.getId());
        plugins.put(plugin.getId(), plugin);
        plugin.onLoad(moduleManager);
    }

    public void unloadPlugin(String id) {
        GlacierPlugin p = plugins.remove(id);
        if (p != null) p.onUnload();
    }

    public Collection<GlacierPlugin> getPlugins() { return Collections.unmodifiableCollection(plugins.values()); }

    public GlacierPlugin getPlugin(String id) { return plugins.get(id); }

    public interface GlacierPlugin {
        String getId();
        String getName();
        String getVersion();
        void onLoad(ModuleManager moduleManager);
        void onUnload();
        default List<GlacierMod> provideModules() { return List.of(); }
    }
}
