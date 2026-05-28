package net.glacierclient.core.cosmetic;

public enum CosmeticCategory {
    WINGS("Wings"),
    CAPES("Capes"),
    HATS("Hats"),
    BODY("Body & Aura"),
    ITEMS("Items & Hands"),
    PETS("Pets"),
    WEAPONS("Weapon Effects"),
    EMOTES("Emotes");

    public final String displayName;

    CosmeticCategory(String displayName) {
        this.displayName = displayName;
    }
}
