package net.glacierclient.api.bridge;

public interface VersionBridge {

    boolean isInGame();
    Object getRawPlayer();
    Object getRawWorld();

    double getX();
    double getY();
    double getZ();
    float getYaw();
    float getPitch();
    float getHealth();
    float getMaxHealth();
    int getFood();
    int getAir();
    boolean isSprinting();
    boolean isSneaking();
    boolean isOnGround();
    boolean isFlying();
    boolean isElytraFlying();
    float getHorizontalSpeed();
    float getVerticalSpeed();

    long getWorldTime();
    int getDimensionId();
    String getBiomeName();
    int getBlockLight(int x, int y, int z);
    int getSkyLight(int x, int y, int z);
    int getCombinedLight(int x, int y, int z);

    String getServerAddress();
    int getPing();
    boolean isMultiplayer();
    String getServerBrand();
    int estimateTPS();
    void sendChatMessage(String message);
    void sendCommand(String command);

    void drawRect(Object ctx, float x, float y, float w, float h, int color);
    void drawGradientRect(Object ctx, float x, float y, float w, float h, int top, int bottom);
    void drawText(Object ctx, String text, float x, float y, int color, boolean shadow);
    void drawCenteredText(Object ctx, String text, float cx, float y, int color, boolean shadow);
    void drawTexture(Object ctx, String resourcePath, float x, float y, float w, float h);
    float getTextWidth(String text);
    int getTextHeight();
    int getScreenWidth();
    int getScreenHeight();

    boolean isKeyDown(int glfwKey);
    boolean isMouseButtonDown(int button);
    double getMouseX();
    double getMouseY();
    void setSprinting(boolean sprint);

    int getCurrentFPS();
    long getUsedMemoryBytes();
    long getMaxMemoryBytes();
    String getMinecraftVersion();
    String getVersionId();

    boolean hasCapability(BridgeCapability capability);
}
