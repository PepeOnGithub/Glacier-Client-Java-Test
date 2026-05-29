package net.glacierclient.core.module;

import net.glacierclient.api.bridge.VersionBridge;

/**
 * A version-agnostic client module. Subclasses use only the {@link VersionBridge} for game state and
 * rendering, so the exact same module runs on every supported Minecraft version.
 */
public abstract class CoreModule {

    private final String name;
    private final String description;
    private final CoreCategory category;
    private boolean enabled;

    /** Default HUD anchor for rendering modules (screen-space, top-left origin). */
    protected int x;
    protected int y;

    protected CoreModule(String name, String description, CoreCategory category, boolean defaultEnabled, int x, int y) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = defaultEnabled;
        this.x = x;
        this.y = y;
    }

    /** Called once per client tick while enabled. */
    public void onTick(VersionBridge bridge) {}

    /** Called every frame while enabled. {@code ctx} is the version-specific draw context (passed through the bridge). */
    public abstract void render(VersionBridge bridge, Object ctx);

    // convenience draw helpers ------------------------------------------------
    protected void text(VersionBridge b, Object ctx, String s, int color) {
        b.drawText(ctx, s, x, y, color, true);
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public CoreCategory getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void toggle() { this.enabled = !this.enabled; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }
}
