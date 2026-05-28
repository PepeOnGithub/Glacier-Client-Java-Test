package net.glacierclient.core.cosmetic;

import net.glacierclient.core.settings.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Cosmetic {

    private final String name;
    private final String description;
    private final CosmeticCategory category;
    private boolean enabled;
    private final List<Setting<?>> settings = new ArrayList<>();

    protected final ColorSetting color = new ColorSetting("Color", "Primary color", 0xFF7289DA);
    protected final NumberSetting scale = new NumberSetting("Scale", "Size scale", 0.5, 3.0, 1.0, 0.1);
    protected final BooleanSetting visibleToOthers = new BooleanSetting("Visible To Others", "Show to other Glacier users", true);

    protected Cosmetic(String name, String description, CosmeticCategory category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
        addSettings(color, scale, visibleToOthers);
    }

    protected void addSettings(Setting<?>... s) { settings.addAll(Arrays.asList(s)); }

    public abstract void render(net.minecraft.client.util.math.MatrixStack matrices,
                                net.minecraft.entity.player.PlayerEntity player,
                                float partialTicks);

    public String getName() { return name; }
    public String getDescription() { return description; }
    public CosmeticCategory getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void toggle() { this.enabled = !this.enabled; }
    public List<Setting<?>> getSettings() { return settings; }
}
