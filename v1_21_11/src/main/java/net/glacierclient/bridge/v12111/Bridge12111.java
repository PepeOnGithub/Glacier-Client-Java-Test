package net.glacierclient.bridge.v12111;

import net.glacierclient.api.bridge.AbstractVersionBridge;
import net.glacierclient.api.bridge.BridgeCapability;
import net.glacierclient.bridge.v1214.Bridge1214;

public class Bridge12111 extends Bridge1214 {

    @Override public String getMinecraftVersion() { return "1.21.11"; }
    @Override public String getVersionId()         { return "v1_21_11"; }

    @Override
    public boolean hasCapability(BridgeCapability cap) {
        return switch (cap) {
            case OFFHAND, SHIELDS, ELYTRA, BOSSBAR, ATTACK_COOLDOWN, TOTEMS, DUAL_WIELD,
                 PARTICLES_V2, SHADER_PIPELINE, ENTITY_CULLING, MOJMAP_DEOBF,
                 BUNDLE_ITEMS, SMITHING_TABLE, DRAW_CONTEXT, DRAW_CONTEXT_SCISSOR,
                 BLUR_EFFECT, PROXIMITY_VOICE -> true;
        };
    }
}
