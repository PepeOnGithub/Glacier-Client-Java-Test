package net.glacierclient.modules.expanded.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class ChatWaypointMarkers extends GlacierMod {

    private final BooleanSetting autoExtract = new BooleanSetting("Auto Extract", "Automatically extract coordinates from chat", false);
    private final BooleanSetting showHUD = new BooleanSetting("Show HUD", "Show extracted waypoints on HUD", false);
    private final BooleanSetting tempWaypoint = new BooleanSetting("Temp Waypoint", "Create temporary waypoint from extracted coords", false);
    private final NumberSetting waypointDuration = new NumberSetting("Waypoint Duration", "Seconds before temporary waypoint expires", 300, 30, 3600);

    public ChatWaypointMarkers() {
        super("Chat Waypoints", "Auto-extract coordinates from chat messages", Category.QOL);
        addSettings(autoExtract, showHUD, tempWaypoint, waypointDuration);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public boolean isAutoExtract() { return autoExtract.getValue(); }
    public boolean isShowHUD() { return showHUD.getValue(); }
    public boolean isTempWaypoint() { return tempWaypoint.getValue(); }
    public int getWaypointDuration() { return (int) waypointDuration.getValue(); }
}
