package net.glacierclient.bridge.v189;

import net.glacierclient.api.bridge.AbstractVersionBridge;
import net.glacierclient.api.bridge.BridgeCapability;

public class Bridge189 extends AbstractVersionBridge {

    private static final String VERSION = "1.8.9";

    @Override
    public boolean isInGame() {
        try {
            Object mc = Class.forName("net.minecraft.client.Minecraft")
                            .getMethod("getMinecraft").invoke(null);
            Object world = mc.getClass().getMethod("theWorld").invoke(mc);
            return world != null;
        } catch (Exception e) { return false; }
    }

    @Override public Object getRawPlayer() { return null; }
    @Override public Object getRawWorld()  { return null; }

    @Override public double getX()         { return 0; }
    @Override public double getY()         { return 0; }
    @Override public double getZ()         { return 0; }
    @Override public float  getYaw()       { return 0; }
    @Override public float  getPitch()     { return 0; }
    @Override public float  getHealth()    { return 20; }
    @Override public float  getMaxHealth() { return 20; }
    @Override public int    getFood()      { return 20; }
    @Override public int    getAir()       { return 300; }
    @Override public boolean isSprinting() { return false; }
    @Override public boolean isSneaking()  { return false; }
    @Override public boolean isOnGround()  { return true; }
    @Override public boolean isFlying()    { return false; }
    @Override public float getHorizontalSpeed() { return 0; }

    @Override public long   getWorldTime()        { return 0; }
    @Override public String getBiomeName()         { return "Plains"; }
    @Override public int    getBlockLight(int x, int y, int z)  { return 15; }
    @Override public int    getSkyLight(int x, int y, int z)    { return 15; }
    @Override public int    getCombinedLight(int x, int y, int z){ return 15; }

    @Override public String  getServerAddress()  { return ""; }
    @Override public int     getPing()           { return 0; }
    @Override public boolean isMultiplayer()     { return false; }
    @Override public void    sendChatMessage(String msg) {}
    @Override public void    sendCommand(String cmd)     {}

    @Override public void  drawRect(Object ctx, float x, float y, float w, float h, int color) {}
    @Override public void  drawGradientRect(Object ctx, float x, float y, float w, float h, int top, int bot) {}
    @Override public void  drawText(Object ctx, String t, float x, float y, int c, boolean s) {}
    @Override public void  drawCenteredText(Object ctx, String t, float cx, float y, int c, boolean s) {}
    @Override public void  drawTexture(Object ctx, String path, float x, float y, float w, float h) {}
    @Override public float getTextWidth(String text) { return text.length() * 6f; }
    @Override public int   getTextHeight()      { return 9; }
    @Override public int   getScreenWidth()     { return 854; }
    @Override public int   getScreenHeight()    { return 480; }

    @Override public boolean isKeyDown(int key)           { return false; }
    @Override public boolean isMouseButtonDown(int button){ return false; }
    @Override public double  getMouseX()  { return 0; }
    @Override public double  getMouseY()  { return 0; }
    @Override public void    setSprinting(boolean s) {}

    @Override public int    getCurrentFPS()    { return 60; }
    @Override public String getMinecraftVersion() { return VERSION; }
    @Override public String getVersionId()        { return "v1_8_9"; }

    @Override
    public boolean hasCapability(BridgeCapability cap) {
        return switch (cap) {
            default -> false;
        };
    }
}
