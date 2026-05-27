package net.glacierclient.core.hud;

import java.util.HashMap;
import java.util.Map;

public class HUDProfile {

    public String name;
    public Map<String, HUDElementData> elements = new HashMap<>();

    public HUDProfile() { this.name = "Default"; }
    public HUDProfile(String name) { this.name = name; }

    public static class HUDElementData {
        public float x;
        public float y;
        public float scale;
        public boolean visible;

        public HUDElementData(float x, float y, float scale, boolean visible) {
            this.x = x;
            this.y = y;
            this.scale = scale;
            this.visible = visible;
        }
    }
}
