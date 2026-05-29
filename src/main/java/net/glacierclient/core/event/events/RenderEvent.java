package net.glacierclient.core.event.events;

import net.glacierclient.core.event.GlacierEvent;
import net.minecraft.client.gui.DrawContext;

public class RenderEvent extends GlacierEvent {
    public enum Phase { PRE, POST }

    private final DrawContext context;
    private final float partialTicks;
    private final Phase phase;
    private net.minecraft.client.util.math.MatrixStack matrixStack;
    private net.minecraft.entity.Entity entity;

    public RenderEvent(DrawContext context, float partialTicks, Phase phase) {
        this.context = context;
        this.partialTicks = partialTicks;
        this.phase = phase;
    }

    public DrawContext getContext() { return context; }
    public float getPartialTicks() { return partialTicks; }
    public Phase getPhase() { return phase; }
    public net.minecraft.client.util.math.MatrixStack getMatrixStack() { return matrixStack; }
    public void setMatrixStack(net.minecraft.client.util.math.MatrixStack matrixStack) { this.matrixStack = matrixStack; }
    public net.minecraft.entity.Entity getEntity() { return entity; }
    public void setEntity(net.minecraft.entity.Entity entity) { this.entity = entity; }
}
