package net.glacierclient.bridge.v12111;

import net.glacierclient.core.client.GlacierGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class GlacierScreen12111 extends Screen {
    public GlacierScreen12111() { super(Text.literal("Glacier")); }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        GlacierGui.render(ctx, mouseX, mouseY);
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (GlacierGui.mouseClicked(mx, my, button)) return true;
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean shouldPause() { return false; }
}
