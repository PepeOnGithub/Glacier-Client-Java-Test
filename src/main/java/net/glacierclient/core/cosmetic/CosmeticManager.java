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

        // Hats
        register(new net.glacierclient.cosmetics.hats.Halo(),
                 new net.glacierclient.cosmetics.hats.Crown(),
                 new net.glacierclient.cosmetics.hats.CatEars(),
                 new net.glacierclient.cosmetics.hats.TopHat(),
                 new net.glacierclient.cosmetics.hats.Fedora(),
                 new net.glacierclient.cosmetics.hats.WitchHat(),
                 new net.glacierclient.cosmetics.hats.Antlers(),
                 new net.glacierclient.cosmetics.hats.SantaHat(),
                 new net.glacierclient.cosmetics.hats.PropellerHat(),
                 new net.glacierclient.cosmetics.hats.Bandana());

        // Body
        register(new net.glacierclient.cosmetics.body.ShoulderParrot(),
                 new net.glacierclient.cosmetics.body.BodyAura(),
                 new net.glacierclient.cosmetics.body.TrailParticles(),
                 new net.glacierclient.cosmetics.body.PhoenixAura(),
                 new net.glacierclient.cosmetics.body.HeartParticles());

        // Pets
        register(new net.glacierclient.cosmetics.pets.MiniMePet(),
                 new net.glacierclient.cosmetics.pets.FloatingSlime(),
                 new net.glacierclient.cosmetics.pets.FriendlyGhost(),
                 new net.glacierclient.cosmetics.pets.PhoenixCompanion());

        // Emotes
        register(new net.glacierclient.cosmetics.emotes.WaveEmote(),
                 new net.glacierclient.cosmetics.emotes.DanceEmote());
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
