package net.glacierclient.mixin;

import net.glacierclient.GlacierClient;
import net.glacierclient.modules.performance.ReducedF3Mod;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/** Trims the F3 debug screen according to the Reduced F3 module's toggles. */
@Mixin(DebugHud.class)
public class MixinDebugHud {

    private ReducedF3Mod glacier$mod() {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return null;
        ReducedF3Mod m = gc.getModuleManager().getModule(ReducedF3Mod.class);
        return (m != null && m.isEnabled()) ? m : null;
    }

    @Inject(method = "getLeftText", at = @At("RETURN"), require = 0)
    private void glacier$left(CallbackInfoReturnable<List<String>> cir) {
        ReducedF3Mod m = glacier$mod();
        if (m == null) return;
        List<String> lines = cir.getReturnValue();
        lines.removeIf(s -> s == null
                || (m.isHideCoords() && (s.startsWith("XYZ") || s.startsWith("Block:") || s.startsWith("Chunk:") || s.startsWith("Facing")))
                || (m.isHideEntityCount() && s.startsWith("E:"))
                || (m.isHideChunkInfo() && (s.startsWith("C:") || s.startsWith("Client Chunk") || s.startsWith("Server Chunk"))));
        if (m.isCompactMode() && lines.size() > 8) lines.subList(8, lines.size()).clear();
    }

    @Inject(method = "getRightText", at = @At("RETURN"), require = 0)
    private void glacier$right(CallbackInfoReturnable<List<String>> cir) {
        ReducedF3Mod m = glacier$mod();
        if (m == null) return;
        List<String> lines = cir.getReturnValue();
        if (m.isHideGPUInfo()) lines.removeIf(s -> s != null && (s.contains("GPU") || s.contains("Display") || s.startsWith("Backend")));
        if (m.isCompactMode() && lines.size() > 8) lines.subList(8, lines.size()).clear();
    }
}
