package net.glacierclient.launch;

import net.glacierclient.api.bridge.VersionBridgeProvider;
import net.glacierclient.bridge.v189.Bridge189;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import java.io.File;
import java.util.List;

public class GlacierTweaker implements ITweaker {

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {}

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        VersionBridgeProvider.register(new Bridge189());
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
