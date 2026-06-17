package net.glacierclient.core.module;

import net.glacierclient.core.event.EventBus;
import net.glacierclient.modules.hud.*;
import net.glacierclient.modules.render.*;
import net.glacierclient.modules.pvp.*;
import net.glacierclient.modules.performance.*;
import net.glacierclient.modules.qol.*;
import net.glacierclient.modules.advanced.hud.*;
import net.glacierclient.modules.advanced.atmosphere.*;
import net.glacierclient.modules.advanced.social.*;
import net.glacierclient.modules.advanced.mechanics.*;
import net.glacierclient.modules.engine.*;
import net.glacierclient.modules.expanded.hud.*;
import net.glacierclient.modules.expanded.visual.*;
import net.glacierclient.modules.expanded.qol.*;
import net.glacierclient.modules.expanded.pvp.*;
import net.glacierclient.modules.expanded.performance.*;
import net.glacierclient.modules.expanded.social.*;

import java.util.*;
import java.util.stream.Collectors;

public class ModuleManager {

    private static final Comparator<GlacierMod> MODULE_ORDER =
            Comparator.comparing(GlacierMod::getName, String.CASE_INSENSITIVE_ORDER);

    private final List<GlacierMod> modules = new ArrayList<>();
    private final EventBus eventBus;

    public ModuleManager(EventBus eventBus) {
        this.eventBus = eventBus;
        registerModules();
    }

    private void registerModules() {
        // HUD
        register(new CPS(), new FPS(), new Ping(), new Keystrokes(), new ArmorStatusHUD(), new CoordinatesHUD(), new Speedometer(), new PotionsStatusHUD(), new DirectionHUD(), new ReachDisplay(), new ComboCounter(), new ServerIPDisplay(), new MemoryUsageHUD(), new ClockMod(), new GameTimeHUD(), new SessionTimer(), new PackDisplay(), new ItemTracker(), new TargetHUD(), new ScoreboardCustomizer());

        // Render
        register(new Zoom(), new Fullbright(), new MotionBlur(), new ChunkAnimator(), new ItemPhysics(), new ClearWater(), new NoRender(), new CustomCrosshair(), new FOVModule(), new HurtCamIntensitySlider(), new EnchantGlintColorizer(), new WeatherController(), new TimeChanger(), new SmoothScrollingMenus(), new NameProtect(), new ChatCustomizer(), new BossbarCustomizer(), new TitleOverlayCustomizer(), new BlockOverlay(), new DamageTiltFix(), new LowShieldRender());

        // PvP
        register(new ToggleSprint(), new ToggleSneak(), new PerspectiveMod(), new AutoGG(), new AutoLeave(), new AutoRejoin(), new QuickPlay(), new ChatFilter(), new CompactChat(), new HitmarkerSoundMod(), new CustomBlockBreakParticles(), new FireIntensitySlider(), new GlintScaleModifier(), new ParticlesMultiplier(), new WeaponTrails(), new EatingAnimationFix(), new SoundLocker(), new NickHider());

        // Performance
        register(new FPSBoost(), new FastRenderHook(), new SmartCulling(), new MemoryManager(), new TextureMIPMapOptimizer(), new LightEngineTweaker(), new ParticleThrottler(), new SmoothFPSStabilizer(), new LazyChunkLoading(), new FastChestOpenFix(), new ReducedF3Mod(), new FontRendererOptimizer(), new ModelRenderingCache(), new GeometryBufferOptimizer(), new CPUThreadingAllocator(), new SoundStreamingFix(), new RefreshRateSelector());

        // QoL
        register(new ProfileManagerMod(), new ConfigCloudSync(), new CustomMainMenu(), new ModSearchFilter(), new KeybindConfigurationMatrix(), new ColorPaletteSelector(), new BorderlessWindowedToggle(), new ScreenshotFolderShortcut(), new ResourcePackQuickSwitch(), new LanguageOverride(), new UpdateChecker(), new CrashReportSaver(), new DiscordRichPresence(), new StreamingMode(), new AccountSwitcher(), new MouseSensitivityFixer(), new ScrollSpeedMultiplier(), new ControllerSupportHook(), new AudioDeviceOutputSelector(), new NotificationToastSystem());

        // Advanced HUD
        register(new TPSGraphOverlay(), new StatTracker(), new FactionMapOverlay(), new EncounterHistory(), new PlayerRadar(), new CombatLogTimer(), new EntityCounter(), new BountyNotifier(), new MinimapModule(), new NetherCordsConverter(), new BiomeIndicator(), new LightLevelMapper(), new ChunkLoadViewer(), new InventoryPeekHUD(), new ActiveEffectsDurationBar(), new WeaponDurabilityWarning(), new ArrowCountNotifier(), new TotemCountHUD(), new WeightSpeedHUD(), new HeldItemLargeDisplay(), new PacketLossIndicator());

        // Advanced Atmosphere
        register(new GlacierSkybox(), new CustomStarsDensity(), new ScreenGlow(), new CustomParticles(), new CameraClipTweak(), new HandProgressModifier(), new CapePhysicsEditor(), new CinematicCameraTweak(), new ArmorColorizer(), new CustomFogDensity(), new VignetteTweak(), new ItemScaleModifier(), new SaturationChanger(), new CustomHitSounds(), new ScreenShakeMultiplier(), new DarkModeMenus(), new CustomDeathAnimations(), new RainbowGUIMode(), new MenuBackgroundVideoShader(), new SoundPan3D(), new PortalNauseaRemover(), new ExplosionFlashMuter());

        // Engine
        register(new FluidFrustumCulling(), new FastChestRender(), new ImmediateGlintPipeline(), new GlacierBrowser(), new SpotifyMediaBridge(), new LegitSchematicOutline(), new MaterialRequiredList(), new ProximityVoiceVisualization());

        // Expanded HUD
        register(new MobSpawnSphereVisualizer(), new FishingTimerLure(), new BlockBreakEfficiencyOverlay(), new ItemDurabilityHeatmap(), new NearestPlayerArrow(), new WorldSeedCrackerIndicator(), new ElytraFlightPathProjector(), new HorseStatsPanel(), new ChunkRenderQueue());

        // Expanded Visual
        register(new DepthOfFieldGUI(), new DynamicLightingOverhaul(), new WaterRefractionCaustics(), new AnimatedItemTextures(), new RainSnowParticleRenderer(), new TemporalAntiAliasing(), new ScreenSpaceReflections(), new WorldCurvatureOption(), new GlacierAuroraOverlay());

        // Expanded QoL
        register(new RecipeBookQuickCrafter(), new ShulkerBoxPreview(), new MassItemMover(), new EnchantedBookCombiner(), new AutoRefillFromBundle(), new ChatWaypointMarkers(), new InGameNotepad(), new SessionStatsDashboard(), new SharedConfigClipboard());
        // Schematic Helper is context-aware: only register it when Litematica is actually present.
        if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("litematica")) {
            register(new QuickSchematicHelper());
        }

        // Expanded PvP
        register(new HitSelectorVisualisation(), new KnockbackCalculator(), new ComboRetainerOverlay(), new TotemPopTracker(), new PearlTrailPrediction(), new BowDrawProgressBar(), new ArmorBreakWarning(), new SwordBlockingVisual(), new HitCooldownRing(), new PotionEffectTicker());

        // Expanded Performance
        register(new LODRenderer(), new EntityModelLOD(), new SodiumFRAPICompatLayer(), new CustomChunkPregenerator(), new VRAMUsageTargeter());

        // Expanded Social
        register(new StreamerSafeInventory(), new PartyHealthBarOverlay(), new InGameVoiceActivityIndicator(), new ReportBotAssist(), new AntiStreamSnipingBlocker());

        // Advanced Mechanics (movement / PvP mechanics — event-driven via PlayerMoveEvent / TickEvent)
        register(new SpeedMod(), new HighJumpMod(), new LongJumpMod(), new AirStrafeExtendedMod(), new SafeWalkMod(), new StepMod(), new NoSlowMod(), new AntiKnockbackMod(), new ElytraFlightMod(), new AntiVoidMod(), new AntiAfkMod(), new InventoryWalkMod(), new FastPlaceMod(), new PhaseWalkMod(), new TimerMod());

        // Advanced Social (chat) — BetterChat is the combined timestamp/filter/highlight/anti-spam/sounds module
        register(new BetterChatMod(), new AutoReplyMod(), new ChatAnimationsMod(), new ChatLoggerMod(), new ChatMacrosMod(), new CopyMod(), new EmotesMod(), new FriendTrackerMod(), new IgnoreListMod(), new PlayerChatHeadMod());

        modules.sort(MODULE_ORDER);
    }

    private void register(GlacierMod... mods) {
        modules.addAll(Arrays.asList(mods));
        modules.sort(MODULE_ORDER);
    }

    public List<GlacierMod> getModules() { return Collections.unmodifiableList(modules); }

    public void moveModule(GlacierMod from, GlacierMod target) {
        modules.sort(MODULE_ORDER);
    }

    public List<GlacierMod> getModulesByCategory(Category category) {
        return modules.stream().filter(m -> m.getCategory() == category).sorted(MODULE_ORDER).collect(Collectors.toList());
    }

    public GlacierMod getModule(String name) {
        return modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @SuppressWarnings("unchecked")
    public <T extends GlacierMod> T getModule(Class<T> clazz) {
        return (T) modules.stream().filter(m -> m.getClass() == clazz).findFirst().orElse(null);
    }

    public void onKey(int key) {
        modules.forEach(m -> {
            if (m.getKeybind() == key) m.toggle();
        });
    }

    public void onTick() {
        modules.stream().filter(GlacierMod::isEnabled).forEach(GlacierMod::onTick);
    }
}
