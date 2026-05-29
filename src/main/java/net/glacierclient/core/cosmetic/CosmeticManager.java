package net.glacierclient.core.cosmetic;

import java.util.*;
import java.util.stream.Collectors;

public class CosmeticManager {

    private final List<Cosmetic> cosmetics = new ArrayList<>();

    public CosmeticManager() {
        registerCosmetics();
    }

    private void registerCosmetics() {
        // Wings
        register(new net.glacierclient.cosmetics.wings.DragonWings(),
                 new net.glacierclient.cosmetics.wings.IceAngelWings(),
                 new net.glacierclient.cosmetics.wings.DemonWings(),
                 new net.glacierclient.cosmetics.wings.ButterflyWings(),
                 new net.glacierclient.cosmetics.wings.FairyWings(),
                 new net.glacierclient.cosmetics.wings.PhoenixWings(),
                 new net.glacierclient.cosmetics.wings.MechanicalWings(),
                 new net.glacierclient.cosmetics.wings.CrystallineWings(),
                 new net.glacierclient.cosmetics.wings.VoidWings());

        // Capes
        register(new net.glacierclient.cosmetics.capes.WaveCape(),
                 new net.glacierclient.cosmetics.capes.GlacierAnimatedCape(),
                 new net.glacierclient.cosmetics.capes.CustomImageCape(),
                 new net.glacierclient.cosmetics.capes.PixelCape(),
                 new net.glacierclient.cosmetics.capes.BannerCape(),
                 new net.glacierclient.cosmetics.capes.LavaCape(),
                 new net.glacierclient.cosmetics.capes.StarfieldCape(),
                 new net.glacierclient.cosmetics.capes.PrismaticCape());

    }

    private void register(Cosmetic... c) { cosmetics.addAll(Arrays.asList(c)); }

    public List<Cosmetic> getCosmetics() { return Collections.unmodifiableList(cosmetics); }

    public List<Cosmetic> getByCategory(CosmeticCategory category) {
        return cosmetics.stream().filter(c -> c.getCategory() == category).collect(Collectors.toList());
    }

    public Cosmetic get(String name) {
        return cosmetics.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
