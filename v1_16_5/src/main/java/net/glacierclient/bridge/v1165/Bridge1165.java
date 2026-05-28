package net.glacierclient.bridge.v1165;

import net.glacierclient.api.bridge.AbstractVersionBridge;
import net.glacierclient.api.bridge.BridgeCapability;
import net.minecraft.client.MinecraftClient;

public class Bridge1165 extends AbstractVersionBridge {

    private MinecraftClient mc() { return MinecraftClient.getInstance(); }

    @Override
    public boolean isInGame() {
        return mc().player != null && mc().world != null;
    }

    @Override public Object  getRawPlayer()    { return mc().player; }
    @Override public Object  getRawWorld()     { return mc().world; }
    @Override public double  getX()            { return isInGame() ? mc().player.getX() : 0; }
    @Override public double  getY()            { return isInGame() ? mc().player.getY() : 0; }
    @Override public double  getZ()            { return isInGame() ? mc().player.getZ() : 0; }
    @Override public float   getYaw()          { return isInGame() ? mc().player.getYaw() : 0; }
    @Override public float   getPitch()        { return isInGame() ? mc().player.getPitch() : 0; }
    @Override public float   getHealth()       { return isInGame() ? mc().player.getHealth() : 20; }
    @Override public float   getMaxHealth()    { return isInGame() ? mc().player.getMaxHealth() : 20; }
    @Override public int     getFood()         { return isInGame() ? mc().player.getHungerManager().getFoodLevel() : 20; }
    @Override public int     getAir()          { return isInGame() ? mc().player.getAir() : 300; }
    @Override public boolean isSprinting()     { return isInGame() && mc().player.isSprinting(); }
    @Override public boolean isSneaking()      { return isInGame() && mc().player.isSneaking(); }
    @Override public boolean isOnGround()      { return !isInGame() || mc().player.isOnGround(); }
    @Override public boolean isFlying()        { return isInGame() && mc().player.getAbilities().flying; }
    @Override public boolean isElytraFlying()  { return isInGame() && mc().player.isFallFlying(); }
    @Override public float   getHorizontalSpeed() {
        if (!isInGame()) return 0;
        double vx = mc().player.getVelocity().x;
        double vz = mc().player.getVelocity().z;
        return (float) Math.sqrt(vx * vx + vz * vz);
    }
    @Override public float   getVerticalSpeed()  { return isInGame() ? (float) mc().player.getVelocity().y : 0; }
    @Override public long    getWorldTime()       { return isInGame() ? mc().world.getTimeOfDay() : 0; }
    @Override public String  getBiomeName()       { return "Plains"; }
    @Override public int     getBlockLight(int x, int y, int z)   { return 15; }
    @Override public int     getSkyLight(int x, int y, int z)     { return 15; }
    @Override public int     getCombinedLight(int x, int y, int z){ return 15; }
    @Override public String  getServerAddress()   { return mc().getCurrentServerEntry() != null ? mc().getCurrentServerEntry().address : ""; }
    @Override public int     getPing()            { return isInGame() && mc().getNetworkHandler() != null ? mc().getNetworkHandler().getPlayerListEntry(mc().player.getGameProfile().getId()) != null ? mc().getNetworkHandler().getPlayerListEntry(mc().player.getGameProfile().getId()).getLatency() : 0 : 0; }
    @Override public boolean isMultiplayer()      { return mc().getCurrentServerEntry() != null; }
    @Override public void    sendChatMessage(String msg) { if (isInGame()) mc().player.sendChatMessage(msg); }
    @Override public void    sendCommand(String cmd)     { sendChatMessage("/" + cmd); }
    @Override public void  drawRect(Object ctx, float x, float y, float w, float h, int color) {}
    @Override public void  drawGradientRect(Object ctx, float x, float y, float w, float h, int top, int bot) {}
    @Override public void  drawText(Object ctx, String t, float x, float y, int c, boolean s) {}
    @Override public void  drawCenteredText(Object ctx, String t, float cx, float y, int c, boolean s) {}
    @Override public void  drawTexture(Object ctx, String path, float x, float y, float w, float h) {}
    @Override public float getTextWidth(String text)  { return mc().textRenderer != null ? mc().textRenderer.getWidth(text) : text.length() * 6f; }
    @Override public int   getTextHeight()            { return 9; }
    @Override public int   getScreenWidth()           { return mc().getWindow().getScaledWidth(); }
    @Override public int   getScreenHeight()          { return mc().getWindow().getScaledHeight(); }
    @Override public boolean isKeyDown(int key)        { return org.lwjgl.glfw.GLFW.glfwGetKey(mc().getWindow().getHandle(), key) == org.lwjgl.glfw.GLFW.GLFW_PRESS; }
    @Override public boolean isMouseButtonDown(int btn){ return org.lwjgl.glfw.GLFW.glfwGetMouseButton(mc().getWindow().getHandle(), btn) == org.lwjgl.glfw.GLFW.GLFW_PRESS; }
    @Override public double  getMouseX()              { return mc().mouse.getX(); }
    @Override public double  getMouseY()              { return mc().mouse.getY(); }
    @Override public void    setSprinting(boolean s)  { if (isInGame()) mc().player.setSprinting(s); }
    @Override public int     getCurrentFPS()           { return mc().getCurrentFps(); }
    @Override public String  getMinecraftVersion()    { return "1.16.5"; }
    @Override public String  getVersionId()           { return "v1_16_5"; }
    @Override
    public boolean hasCapability(BridgeCapability cap) {
        return switch (cap) {
            case OFFHAND, SHIELDS, ELYTRA, BOSSBAR, ATTACK_COOLDOWN, TOTEMS,
                 DUAL_WIELD, PARTICLES_V2, SHADER_PIPELINE, ENTITY_CULLING -> true;
            default -> false;
        };
    }
}
