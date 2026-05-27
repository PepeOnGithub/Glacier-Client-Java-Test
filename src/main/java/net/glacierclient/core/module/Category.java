package net.glacierclient.core.module;

public enum Category {
    HUD("HUD"),
    RENDER("Render"),
    PVP("PvP"),
    PERFORMANCE("Performance"),
    QOL("Quality of Life"),
    ADVANCED("Advanced"),
    ENGINE("Engine"),
    COSMETICS("Cosmetics");

    public final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }
}
