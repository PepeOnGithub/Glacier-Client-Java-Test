package net.glacierclient.api.bridge;

public abstract class AbstractVersionBridge implements VersionBridge {

    private long lastTickTime = System.currentTimeMillis();
    private int tickCounter = 0;
    private float estimatedTPS = 20.0f;

    public void onTick() {
        long now = System.currentTimeMillis();
        long delta = now - lastTickTime;
        lastTickTime = now;
        tickCounter++;
        if (delta > 0) {
            estimatedTPS = estimatedTPS * 0.95f + (1000.0f / delta) * 0.05f;
        }
    }

    @Override
    public int estimateTPS() {
        return Math.min(20, Math.round(estimatedTPS));
    }

    @Override
    public int getDimensionId() {
        return 0;
    }

    @Override
    public boolean isElytraFlying() {
        return false;
    }

    @Override
    public float getVerticalSpeed() {
        return 0f;
    }

    @Override
    public String getServerBrand() {
        return "vanilla";
    }

    @Override
    public long getUsedMemoryBytes() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    @Override
    public long getMaxMemoryBytes() {
        return Runtime.getRuntime().maxMemory();
    }
}
