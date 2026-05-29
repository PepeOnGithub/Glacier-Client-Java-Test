package net.glacierclient.core.module;

/** Version-agnostic module categories for the shared (bridge-driven) client. */
public enum CoreCategory {
    HUD("HUD"),
    RENDER("Render"),
    PERFORMANCE("Performance"),
    PVP("PvP"),
    QOL("Quality of Life");

    public final String displayName;

    CoreCategory(String displayName) {
        this.displayName = displayName;
    }
}
