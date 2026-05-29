package net.glacierclient.bridge.v1165;

import net.glacierclient.core.client.GlacierGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class GlacierScreen1165 extends Screen {
    public GlacierScreen1165() { super(new LiteralText("Glacier")); }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        GlacierGui.render(matrices, mouseX, mouseY);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (GlacierGui.mouseClicked(mx, my, button)) return true;
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
