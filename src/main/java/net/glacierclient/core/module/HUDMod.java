package net.glacierclient.core.module;

// Alias so modules can `import net.glacierclient.core.module.HUDMod`.
// Real impl lives in net.glacierclient.core.hud.HUDMod.
public abstract class HUDMod extends net.glacierclient.core.hud.HUDMod {
    protected HUDMod(String name, String description, int defaultWidth, int defaultHeight) {
        super(name, description, defaultWidth, defaultHeight);
    }

    public int getX() { return getX(net.minecraft.client.MinecraftClient.getInstance().getWindow().getScaledWidth()); }
    public int getY() { return getY(net.minecraft.client.MinecraftClient.getInstance().getWindow().getScaledHeight()); }
}
