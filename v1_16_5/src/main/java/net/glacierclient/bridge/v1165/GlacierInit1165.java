package net.glacierclient.bridge.v1165;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.glacierclient.api.bridge.VersionBridgeProvider;
import net.glacierclient.core.client.GlacierCore;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class GlacierInit1165 implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        VersionBridgeProvider.register(new Bridge1165());
        GlacierCore.get().init();

        KeyBinding openGui = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.glacier.open_gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "Glacier"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            GlacierCore.get().onClientTick();
            while (openGui.wasPressed()) client.openScreen(new GlacierScreen1165());
        });
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> GlacierCore.get().onRenderHud(matrixStack));
    }
}
