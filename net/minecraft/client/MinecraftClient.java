/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Queues
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.properties.PropertyMap
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.datafixers.DataFixer
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.platform.GlDebugInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.class_5219;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClientGame;
import net.minecraft.client.Mouse;
import net.minecraft.client.RunArgs;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.OutOfMemoryScreen;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.options.AoOption;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.HotbarStorage;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.Option;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.client.resource.FoliageColormapResourceSupplier;
import net.minecraft.client.resource.Format3ResourcePack;
import net.minecraft.client.resource.Format4ResourcePack;
import net.minecraft.client.resource.GrassColormapResourceSupplier;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.search.IdentifierSearchableContainer;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.search.TextSearchableContainer;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.MusicType;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.WindowProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.datafixer.Schemas;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SkullItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.QueueingWorldGenerationProgressListener;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.sound.MusicSound;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.KeybindText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetricsData;
import net.minecraft.util.TickDurationMonitor;
import net.minecraft.util.Unit;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerTiming;
import net.minecraft.util.profiler.TickTimeTracker;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.util.snooper.SnooperListener;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MinecraftClient
extends ReentrantThreadExecutor<Runnable>
implements SnooperListener,
WindowEventHandler {
    private static MinecraftClient instance;
    private static final Logger LOGGER;
    public static final boolean IS_SYSTEM_MAC;
    public static final Identifier DEFAULT_FONT_ID;
    public static final Identifier UNICODE_FONT_ID;
    public static final Identifier ALT_TEXT_RENDERER_ID;
    private static final CompletableFuture<Unit> COMPLETED_UNIT_FUTURE;
    private final File resourcePackDir;
    private final PropertyMap sessionPropertyMap;
    private final TextureManager textureManager;
    private final DataFixer dataFixer;
    private final WindowProvider windowProvider;
    private final Window window;
    private final RenderTickCounter renderTickCounter = new RenderTickCounter(20.0f, 0L);
    private final Snooper snooper = new Snooper("client", this, Util.getMeasuringTimeMs());
    private final BufferBuilderStorage bufferBuilders;
    public final WorldRenderer worldRenderer;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final ItemRenderer itemRenderer;
    private final HeldItemRenderer heldItemRenderer;
    public final ParticleManager particleManager;
    private final SearchManager searchManager = new SearchManager();
    private final Session session;
    public final TextRenderer textRenderer;
    public final GameRenderer gameRenderer;
    public final DebugRenderer debugRenderer;
    private final AtomicReference<WorldGenerationProgressTracker> worldGenProgressTracker = new AtomicReference();
    public final InGameHud inGameHud;
    public final GameOptions options;
    private final HotbarStorage creativeHotbarStorage;
    public final Mouse mouse;
    public final Keyboard keyboard;
    public final File runDirectory;
    private final String gameVersion;
    private final String versionType;
    private final Proxy netProxy;
    private final LevelStorage levelStorage;
    public final MetricsData metricsData = new MetricsData();
    private final boolean is64Bit;
    private final boolean isDemo;
    private final ReloadableResourceManager resourceManager;
    private final ClientBuiltinResourcePackProvider builtinPackProvider;
    private final ResourcePackManager<ClientResourcePackProfile> resourcePackManager;
    private final LanguageManager languageManager;
    private final BlockColors blockColorMap;
    private final ItemColors itemColorMap;
    private final Framebuffer framebuffer;
    private final SoundManager soundManager;
    private final MusicTracker musicTracker;
    private final FontManager fontManager;
    private final SplashTextResourceSupplier splashTextLoader;
    private final MinecraftSessionService sessionService;
    private final PlayerSkinProvider skinProvider;
    private final BakedModelManager bakedModelManager;
    private final BlockRenderManager blockRenderManager;
    private final PaintingManager paintingManager;
    private final StatusEffectSpriteManager statusEffectSpriteManager;
    private final ToastManager toastManager;
    private final MinecraftClientGame game = new MinecraftClientGame(this);
    private final TutorialManager tutorialManager;
    public static byte[] memoryReservedForCrash;
    @Nullable
    public ClientPlayerInteractionManager interactionManager;
    @Nullable
    public ClientWorld world;
    @Nullable
    public ClientPlayerEntity player;
    @Nullable
    private IntegratedServer server;
    @Nullable
    private ServerInfo currentServerEntry;
    @Nullable
    private ClientConnection connection;
    private boolean isIntegratedServerRunning;
    @Nullable
    public Entity cameraEntity;
    @Nullable
    public Entity targetedEntity;
    @Nullable
    public HitResult crosshairTarget;
    private int itemUseCooldown;
    protected int attackCooldown;
    private boolean paused;
    private float pausedTickDelta;
    private long lastMetricsSampleTime = Util.getMeasuringTimeNano();
    private long nextDebugInfoUpdateTime;
    private int fpsCounter;
    public boolean skipGameRender;
    @Nullable
    public Screen currentScreen;
    @Nullable
    public Overlay overlay;
    private boolean connectedToRealms;
    private Thread thread;
    private volatile boolean running = true;
    @Nullable
    private CrashReport crashReport;
    private static int currentFps;
    public String fpsDebugString = "";
    public boolean debugChunkInfo;
    public boolean debugChunkOcculsion;
    public boolean chunkCullingEnabled = true;
    private boolean windowFocused;
    private final Queue<Runnable> renderTaskQueue = Queues.newConcurrentLinkedQueue();
    @Nullable
    private CompletableFuture<Void> resourceReloadFuture;
    private Profiler profiler = DummyProfiler.INSTANCE;
    private int trackingTick;
    private final TickTimeTracker tickTimeTracker = new TickTimeTracker(Util.nanoTimeSupplier, () -> this.trackingTick);
    @Nullable
    private ProfileResult tickProfilerResult;
    private String openProfilerSection = "root";

    public MinecraftClient(RunArgs arg) {
        super("Client");
        WindowSettings lv2;
        int j;
        String string2;
        instance = this;
        this.runDirectory = arg.directories.runDir;
        File file = arg.directories.assetDir;
        this.resourcePackDir = arg.directories.resourcePackDir;
        this.gameVersion = arg.game.version;
        this.versionType = arg.game.versionType;
        this.sessionPropertyMap = arg.network.profileProperties;
        this.builtinPackProvider = new ClientBuiltinResourcePackProvider(new File(this.runDirectory, "server-resource-packs"), arg.directories.getResourceIndex());
        this.resourcePackManager = new ResourcePackManager<ClientResourcePackProfile>(MinecraftClient::createResourcePackProfile);
        this.resourcePackManager.registerProvider(this.builtinPackProvider);
        this.resourcePackManager.registerProvider(new FileResourcePackProvider(this.resourcePackDir));
        this.netProxy = arg.network.netProxy;
        this.sessionService = new YggdrasilAuthenticationService(this.netProxy, UUID.randomUUID().toString()).createMinecraftSessionService();
        this.session = arg.network.session;
        LOGGER.info("Setting user: {}", (Object)this.session.getUsername());
        LOGGER.debug("(Session ID is {})", (Object)this.session.getSessionId());
        this.isDemo = arg.game.demo;
        this.is64Bit = MinecraftClient.checkIs64Bit();
        this.server = null;
        if (arg.autoConnect.serverAddress != null) {
            String string = arg.autoConnect.serverAddress;
            int i = arg.autoConnect.serverPort;
        } else {
            string2 = null;
            j = 0;
        }
        Bootstrap.initialize();
        Bootstrap.logMissing();
        KeybindText.setTranslator(KeyBinding::getLocalizedName);
        this.dataFixer = Schemas.getFixer();
        this.toastManager = new ToastManager(this);
        this.tutorialManager = new TutorialManager(this);
        this.thread = Thread.currentThread();
        this.options = new GameOptions(this, this.runDirectory);
        this.creativeHotbarStorage = new HotbarStorage(this.runDirectory, this.dataFixer);
        this.startTimerHackThread();
        LOGGER.info("Backend library: {}", (Object)RenderSystem.getBackendDescription());
        if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
            WindowSettings lv = new WindowSettings(this.options.overrideWidth, this.options.overrideHeight, arg.windowSettings.fullscreenWidth, arg.windowSettings.fullscreenHeight, arg.windowSettings.fullscreen);
        } else {
            lv2 = arg.windowSettings;
        }
        Util.nanoTimeSupplier = RenderSystem.initBackendSystem();
        this.windowProvider = new WindowProvider(this);
        this.window = this.windowProvider.createWindow(lv2, this.options.fullscreenResolution, this.getWindowTitle());
        this.onWindowFocusChanged(true);
        try {
            InputStream inputStream = this.getResourcePackDownloader().getPack().open(ResourceType.CLIENT_RESOURCES, new Identifier("icons/icon_16x16.png"));
            InputStream inputStream2 = this.getResourcePackDownloader().getPack().open(ResourceType.CLIENT_RESOURCES, new Identifier("icons/icon_32x32.png"));
            this.window.setIcon(inputStream, inputStream2);
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't set icon", (Throwable)iOException);
        }
        this.window.setFramerateLimit(this.options.maxFps);
        this.mouse = new Mouse(this);
        this.mouse.setup(this.window.getHandle());
        this.keyboard = new Keyboard(this);
        this.keyboard.setup(this.window.getHandle());
        RenderSystem.initRenderer(this.options.glDebugVerbosity, false);
        this.framebuffer = new Framebuffer(this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), true, IS_SYSTEM_MAC);
        this.framebuffer.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.resourceManager = new ReloadableResourceManagerImpl(ResourceType.CLIENT_RESOURCES, this.thread);
        this.options.addResourcePackProfilesToManager(this.resourcePackManager);
        this.resourcePackManager.scanPacks();
        this.languageManager = new LanguageManager(this.options.language);
        this.resourceManager.registerListener(this.languageManager);
        this.textureManager = new TextureManager(this.resourceManager);
        this.resourceManager.registerListener(this.textureManager);
        this.skinProvider = new PlayerSkinProvider(this.textureManager, new File(file, "skins"), this.sessionService);
        this.levelStorage = new LevelStorage(this.runDirectory.toPath().resolve("saves"), this.runDirectory.toPath().resolve("backups"), this.dataFixer);
        this.soundManager = new SoundManager(this.resourceManager, this.options);
        this.resourceManager.registerListener(this.soundManager);
        this.splashTextLoader = new SplashTextResourceSupplier(this.session);
        this.resourceManager.registerListener(this.splashTextLoader);
        this.musicTracker = new MusicTracker(this);
        this.fontManager = new FontManager(this.textureManager);
        this.textRenderer = this.fontManager.createTextRenderer();
        this.resourceManager.registerListener(this.fontManager.getResourceReloadListener());
        this.initFont(this.forcesUnicodeFont());
        this.resourceManager.registerListener(new GrassColormapResourceSupplier());
        this.resourceManager.registerListener(new FoliageColormapResourceSupplier());
        this.window.setPhase("Startup");
        RenderSystem.setupDefaultState(0, 0, this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        this.window.setPhase("Post startup");
        this.blockColorMap = BlockColors.create();
        this.itemColorMap = ItemColors.create(this.blockColorMap);
        this.bakedModelManager = new BakedModelManager(this.textureManager, this.blockColorMap, this.options.mipmapLevels);
        this.resourceManager.registerListener(this.bakedModelManager);
        this.itemRenderer = new ItemRenderer(this.textureManager, this.bakedModelManager, this.itemColorMap);
        this.entityRenderDispatcher = new EntityRenderDispatcher(this.textureManager, this.itemRenderer, this.resourceManager, this.textRenderer, this.options);
        this.heldItemRenderer = new HeldItemRenderer(this);
        this.resourceManager.registerListener(this.itemRenderer);
        this.bufferBuilders = new BufferBuilderStorage();
        this.gameRenderer = new GameRenderer(this, this.resourceManager, this.bufferBuilders);
        this.resourceManager.registerListener(this.gameRenderer);
        this.blockRenderManager = new BlockRenderManager(this.bakedModelManager.getBlockModels(), this.blockColorMap);
        this.resourceManager.registerListener(this.blockRenderManager);
        this.worldRenderer = new WorldRenderer(this, this.bufferBuilders);
        this.resourceManager.registerListener(this.worldRenderer);
        this.initializeSearchableContainers();
        this.resourceManager.registerListener(this.searchManager);
        this.particleManager = new ParticleManager(this.world, this.textureManager);
        this.resourceManager.registerListener(this.particleManager);
        this.paintingManager = new PaintingManager(this.textureManager);
        this.resourceManager.registerListener(this.paintingManager);
        this.statusEffectSpriteManager = new StatusEffectSpriteManager(this.textureManager);
        this.resourceManager.registerListener(this.statusEffectSpriteManager);
        this.inGameHud = new InGameHud(this);
        this.debugRenderer = new DebugRenderer(this);
        RenderSystem.setErrorCallback((arg_0, arg_1) -> this.handleGlErrorByDisableVsync(arg_0, arg_1));
        if (this.options.fullscreen && !this.window.isFullscreen()) {
            this.window.toggleFullscreen();
            this.options.fullscreen = this.window.isFullscreen();
        }
        this.window.setVsync(this.options.enableVsync);
        this.window.setRawMouseMotion(this.options.rawMouseInput);
        this.window.logOnGlError();
        this.onResolutionChanged();
        if (string2 != null) {
            this.openScreen(new ConnectScreen(new TitleScreen(), this, string2, j));
        } else {
            this.openScreen(new TitleScreen(true));
        }
        SplashScreen.init(this);
        List<ResourcePack> list = this.resourcePackManager.getEnabledProfiles().stream().map(ResourcePackProfile::createResourcePack).collect(Collectors.toList());
        this.setOverlay(new SplashScreen(this, this.resourceManager.beginMonitoredReload(Util.getServerWorkerExecutor(), this, COMPLETED_UNIT_FUTURE, list), optional -> Util.ifPresentOrElse(optional, this::handleResourceReloadExecption, () -> {
            this.languageManager.reloadResources(list);
            if (SharedConstants.isDevelopment) {
                this.checkGameData();
            }
        }), false));
    }

    public void updateWindowTitle() {
        this.window.setTitle(this.getWindowTitle());
    }

    private String getWindowTitle() {
        StringBuilder stringBuilder = new StringBuilder("Minecraft");
        if (this.isModded()) {
            stringBuilder.append("*");
        }
        stringBuilder.append(" ");
        stringBuilder.append(SharedConstants.getGameVersion().getName());
        ClientPlayNetworkHandler lv = this.getNetworkHandler();
        if (lv != null && lv.getConnection().isOpen()) {
            stringBuilder.append(" - ");
            if (this.server != null && !this.server.isRemote()) {
                stringBuilder.append(I18n.translate("title.singleplayer", new Object[0]));
            } else if (this.isConnectedToRealms()) {
                stringBuilder.append(I18n.translate("title.multiplayer.realms", new Object[0]));
            } else if (this.server != null || this.currentServerEntry != null && this.currentServerEntry.isLocal()) {
                stringBuilder.append(I18n.translate("title.multiplayer.lan", new Object[0]));
            } else {
                stringBuilder.append(I18n.translate("title.multiplayer.other", new Object[0]));
            }
        }
        return stringBuilder.toString();
    }

    public boolean isModded() {
        return !"vanilla".equals(ClientBrandRetriever.getClientModName()) || MinecraftClient.class.getSigners() == null;
    }

    private void handleResourceReloadExecption(Throwable throwable) {
        if (this.resourcePackManager.getEnabledProfiles().size() > 1) {
            Text lv2;
            if (throwable instanceof ReloadableResourceManagerImpl.PackAdditionFailedException) {
                LiteralText lv = new LiteralText(((ReloadableResourceManagerImpl.PackAdditionFailedException)throwable).getPack().getName());
            } else {
                lv2 = null;
            }
            LOGGER.info("Caught error loading resourcepacks, removing all selected resourcepacks", throwable);
            this.resourcePackManager.setEnabledProfiles(Collections.emptyList());
            this.options.resourcePacks.clear();
            this.options.incompatibleResourcePacks.clear();
            this.options.write();
            this.reloadResources().thenRun(() -> {
                ToastManager lv = this.getToastManager();
                SystemToast.show(lv, SystemToast.Type.PACK_LOAD_FAILURE, new TranslatableText("resourcePack.load_fail"), lv2);
            });
        } else {
            Util.throwUnchecked(throwable);
        }
    }

    public void run() {
        this.thread = Thread.currentThread();
        try {
            boolean bl = false;
            while (this.running) {
                if (this.crashReport != null) {
                    MinecraftClient.printCrashReport(this.crashReport);
                    return;
                }
                try {
                    TickDurationMonitor lv = TickDurationMonitor.create("Renderer");
                    boolean bl2 = this.shouldMonitorTickDuration();
                    this.startMonitor(bl2, lv);
                    this.profiler.startTick();
                    this.render(!bl);
                    this.profiler.endTick();
                    this.endMonitor(bl2, lv);
                }
                catch (OutOfMemoryError outOfMemoryError) {
                    if (bl) {
                        throw outOfMemoryError;
                    }
                    this.cleanUpAfterCrash();
                    this.openScreen(new OutOfMemoryScreen());
                    System.gc();
                    LOGGER.fatal("Out of memory", (Throwable)outOfMemoryError);
                    bl = true;
                }
            }
        }
        catch (CrashException lv2) {
            this.addDetailsToCrashReport(lv2.getReport());
            this.cleanUpAfterCrash();
            LOGGER.fatal("Reported exception thrown!", (Throwable)lv2);
            MinecraftClient.printCrashReport(lv2.getReport());
        }
        catch (Throwable throwable) {
            CrashReport lv3 = this.addDetailsToCrashReport(new CrashReport("Unexpected error", throwable));
            LOGGER.fatal("Unreported exception thrown!", throwable);
            this.cleanUpAfterCrash();
            MinecraftClient.printCrashReport(lv3);
        }
    }

    void initFont(boolean bl) {
        this.fontManager.setIdOverrides((Map<Identifier, Identifier>)(bl ? ImmutableMap.of((Object)DEFAULT_FONT_ID, (Object)UNICODE_FONT_ID) : ImmutableMap.of()));
        this.textRenderer.setRightToLeft(this.languageManager.isRightToLeft());
    }

    private void initializeSearchableContainers() {
        TextSearchableContainer<ItemStack> lv = new TextSearchableContainer<ItemStack>(arg2 -> arg2.getTooltip(null, TooltipContext.Default.NORMAL).stream().map(arg -> Formatting.strip(arg.getString()).trim()).filter(string -> !string.isEmpty()), arg -> Stream.of(Registry.ITEM.getId(arg.getItem())));
        IdentifierSearchableContainer<ItemStack> lv2 = new IdentifierSearchableContainer<ItemStack>(arg -> ItemTags.getContainer().getTagsFor(arg.getItem()).stream());
        DefaultedList<ItemStack> lv3 = DefaultedList.of();
        for (Item lv4 : Registry.ITEM) {
            lv4.appendStacks(ItemGroup.SEARCH, lv3);
        }
        lv3.forEach(arg3 -> {
            lv.add((ItemStack)arg3);
            lv2.add((ItemStack)arg3);
        });
        TextSearchableContainer<RecipeResultCollection> lv5 = new TextSearchableContainer<RecipeResultCollection>(arg2 -> arg2.getAllRecipes().stream().flatMap(arg -> arg.getOutput().getTooltip(null, TooltipContext.Default.NORMAL).stream()).map(arg -> Formatting.strip(arg.getString()).trim()).filter(string -> !string.isEmpty()), arg2 -> arg2.getAllRecipes().stream().map(arg -> Registry.ITEM.getId(arg.getOutput().getItem())));
        this.searchManager.put(SearchManager.ITEM_TOOLTIP, lv);
        this.searchManager.put(SearchManager.ITEM_TAG, lv2);
        this.searchManager.put(SearchManager.RECIPE_OUTPUT, lv5);
    }

    private void handleGlErrorByDisableVsync(int i, long l) {
        this.options.enableVsync = false;
        this.options.write();
    }

    private static boolean checkIs64Bit() {
        String[] strings;
        for (String string : strings = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"}) {
            String string2 = System.getProperty(string);
            if (string2 == null || !string2.contains("64")) continue;
            return true;
        }
        return false;
    }

    public Framebuffer getFramebuffer() {
        return this.framebuffer;
    }

    public String getGameVersion() {
        return this.gameVersion;
    }

    public String getVersionType() {
        return this.versionType;
    }

    private void startTimerHackThread() {
        Thread thread = new Thread("Timer hack thread"){

            @Override
            public void run() {
                while (MinecraftClient.this.running) {
                    try {
                        Thread.sleep(Integer.MAX_VALUE);
                    }
                    catch (InterruptedException interruptedException) {}
                }
            }
        };
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
        thread.start();
    }

    public void setCrashReport(CrashReport arg) {
        this.crashReport = arg;
    }

    public static void printCrashReport(CrashReport arg) {
        File file = new File(MinecraftClient.getInstance().runDirectory, "crash-reports");
        File file2 = new File(file, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-client.txt");
        Bootstrap.println(arg.asString());
        if (arg.getFile() != null) {
            Bootstrap.println("#@!@# Game crashed! Crash report saved to: #@!@# " + arg.getFile());
            System.exit(-1);
        } else if (arg.writeToFile(file2)) {
            Bootstrap.println("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
            System.exit(-1);
        } else {
            Bootstrap.println("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            System.exit(-2);
        }
    }

    public boolean forcesUnicodeFont() {
        return this.options.forceUnicodeFont;
    }

    public CompletableFuture<Void> reloadResources() {
        if (this.resourceReloadFuture != null) {
            return this.resourceReloadFuture;
        }
        CompletableFuture<Void> completableFuture = new CompletableFuture<Void>();
        if (this.overlay instanceof SplashScreen) {
            this.resourceReloadFuture = completableFuture;
            return completableFuture;
        }
        this.resourcePackManager.scanPacks();
        List<ResourcePack> list = this.resourcePackManager.getEnabledProfiles().stream().map(ResourcePackProfile::createResourcePack).collect(Collectors.toList());
        this.setOverlay(new SplashScreen(this, this.resourceManager.beginMonitoredReload(Util.getServerWorkerExecutor(), this, COMPLETED_UNIT_FUTURE, list), optional -> Util.ifPresentOrElse(optional, this::handleResourceReloadExecption, () -> {
            this.languageManager.reloadResources(list);
            this.worldRenderer.reload();
            completableFuture.complete(null);
        }), true));
        return completableFuture;
    }

    private void checkGameData() {
        boolean bl = false;
        BlockModels lv = this.getBlockRenderManager().getModels();
        BakedModel lv2 = lv.getModelManager().getMissingModel();
        for (Block block : Registry.BLOCK) {
            for (BlockState lv4 : block.getStateManager().getStates()) {
                BakedModel lv5;
                if (lv4.getRenderType() != BlockRenderType.MODEL || (lv5 = lv.getModel(lv4)) != lv2) continue;
                LOGGER.debug("Missing model for: {}", (Object)lv4);
                bl = true;
            }
        }
        Sprite lv6 = lv2.getSprite();
        for (Block lv7 : Registry.BLOCK) {
            for (BlockState lv8 : lv7.getStateManager().getStates()) {
                Sprite lv9 = lv.getSprite(lv8);
                if (lv8.isAir() || lv9 != lv6) continue;
                LOGGER.debug("Missing particle icon for: {}", (Object)lv8);
                bl = true;
            }
        }
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        for (Item lv11 : Registry.ITEM) {
            defaultedList.clear();
            lv11.appendStacks(ItemGroup.SEARCH, defaultedList);
            for (ItemStack lv12 : defaultedList) {
                String string = lv12.getTranslationKey();
                String string2 = new TranslatableText(string).getString();
                if (!string2.toLowerCase(Locale.ROOT).equals(lv11.getTranslationKey())) continue;
                LOGGER.debug("Missing translation for: {} {} {}", (Object)lv12, (Object)string, (Object)lv12.getItem());
            }
        }
        if (bl |= HandledScreens.validateScreens()) {
            throw new IllegalStateException("Your game data is foobar, fix the errors above!");
        }
    }

    public LevelStorage getLevelStorage() {
        return this.levelStorage;
    }

    public void openScreen(@Nullable Screen arg) {
        if (this.currentScreen != null) {
            this.currentScreen.removed();
        }
        if (arg == null && this.world == null) {
            arg = new TitleScreen();
        } else if (arg == null && this.player.getHealth() <= 0.0f) {
            if (this.player.showsDeathScreen()) {
                arg = new DeathScreen(null, this.world.getLevelProperties().isHardcore());
            } else {
                this.player.requestRespawn();
            }
        }
        if (arg instanceof TitleScreen || arg instanceof MultiplayerScreen) {
            this.options.debugEnabled = false;
            this.inGameHud.getChatHud().clear(true);
        }
        this.currentScreen = arg;
        if (arg != null) {
            this.mouse.unlockCursor();
            KeyBinding.unpressAll();
            arg.init(this, this.window.getScaledWidth(), this.window.getScaledHeight());
            this.skipGameRender = false;
            NarratorManager.INSTANCE.narrate(arg.getNarrationMessage());
        } else {
            this.soundManager.resumeAll();
            this.mouse.lockCursor();
        }
        this.updateWindowTitle();
    }

    public void setOverlay(@Nullable Overlay arg) {
        this.overlay = arg;
    }

    public void stop() {
        try {
            LOGGER.info("Stopping!");
            try {
                NarratorManager.INSTANCE.destroy();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                if (this.world != null) {
                    this.world.disconnect();
                }
                this.disconnect();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (this.currentScreen != null) {
                this.currentScreen.removed();
            }
            this.close();
        }
        finally {
            Util.nanoTimeSupplier = System::nanoTime;
            if (this.crashReport == null) {
                System.exit(0);
            }
        }
    }

    @Override
    public void close() {
        try {
            this.bakedModelManager.close();
            this.fontManager.close();
            this.gameRenderer.close();
            this.worldRenderer.close();
            this.soundManager.close();
            this.resourcePackManager.close();
            this.particleManager.clearAtlas();
            this.statusEffectSpriteManager.close();
            this.paintingManager.close();
            this.textureManager.close();
            Util.shutdownServerWorkerExecutor();
        }
        catch (Throwable throwable) {
            LOGGER.error("Shutdown failure!", throwable);
            throw throwable;
        }
        finally {
            this.windowProvider.close();
            this.window.close();
        }
    }

    private void render(boolean bl) {
        boolean bl2;
        Runnable runnable;
        this.window.setPhase("Pre render");
        long l = Util.getMeasuringTimeNano();
        if (this.window.shouldClose()) {
            this.scheduleStop();
        }
        if (this.resourceReloadFuture != null && !(this.overlay instanceof SplashScreen)) {
            CompletableFuture<Void> completableFuture = this.resourceReloadFuture;
            this.resourceReloadFuture = null;
            this.reloadResources().thenRun(() -> completableFuture.complete(null));
        }
        while ((runnable = this.renderTaskQueue.poll()) != null) {
            runnable.run();
        }
        if (bl) {
            int i = this.renderTickCounter.beginRenderTick(Util.getMeasuringTimeMs());
            this.profiler.push("scheduledExecutables");
            this.runTasks();
            this.profiler.pop();
            this.profiler.push("tick");
            for (int j = 0; j < Math.min(10, i); ++j) {
                this.profiler.visit("clientTick");
                this.tick();
            }
            this.profiler.pop();
        }
        this.mouse.updateMouse();
        this.window.setPhase("Render");
        this.profiler.push("sound");
        this.soundManager.updateListenerPosition(this.gameRenderer.getCamera());
        this.profiler.pop();
        this.profiler.push("render");
        RenderSystem.pushMatrix();
        RenderSystem.clear(16640, IS_SYSTEM_MAC);
        this.framebuffer.beginWrite(true);
        BackgroundRenderer.method_23792();
        this.profiler.push("display");
        RenderSystem.enableTexture();
        RenderSystem.enableCull();
        this.profiler.pop();
        if (!this.skipGameRender) {
            this.profiler.swap("gameRenderer");
            this.gameRenderer.render(this.paused ? this.pausedTickDelta : this.renderTickCounter.tickDelta, l, bl);
            this.profiler.swap("toasts");
            this.toastManager.draw(new MatrixStack());
            this.profiler.pop();
        }
        if (this.tickProfilerResult != null) {
            this.profiler.push("fpsPie");
            this.drawProfilerResults(new MatrixStack(), this.tickProfilerResult);
            this.profiler.pop();
        }
        this.profiler.push("blit");
        this.framebuffer.endWrite();
        RenderSystem.popMatrix();
        RenderSystem.pushMatrix();
        this.framebuffer.draw(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        RenderSystem.popMatrix();
        this.profiler.swap("updateDisplay");
        this.window.swapBuffers();
        int k = this.getFramerateLimit();
        if ((double)k < Option.FRAMERATE_LIMIT.getMax()) {
            RenderSystem.limitDisplayFPS(k);
        }
        this.profiler.swap("yield");
        Thread.yield();
        this.profiler.pop();
        this.window.setPhase("Post render");
        ++this.fpsCounter;
        boolean bl3 = bl2 = this.isIntegratedServerRunning() && (this.currentScreen != null && this.currentScreen.isPauseScreen() || this.overlay != null && this.overlay.pausesGame()) && !this.server.isRemote();
        if (this.paused != bl2) {
            if (this.paused) {
                this.pausedTickDelta = this.renderTickCounter.tickDelta;
            } else {
                this.renderTickCounter.tickDelta = this.pausedTickDelta;
            }
            this.paused = bl2;
        }
        long m = Util.getMeasuringTimeNano();
        this.metricsData.pushSample(m - this.lastMetricsSampleTime);
        this.lastMetricsSampleTime = m;
        this.profiler.push("fpsUpdate");
        while (Util.getMeasuringTimeMs() >= this.nextDebugInfoUpdateTime + 1000L) {
            currentFps = this.fpsCounter;
            Object[] arrobject = new Object[6];
            arrobject[0] = currentFps;
            arrobject[1] = (double)this.options.maxFps == Option.FRAMERATE_LIMIT.getMax() ? "inf" : Integer.valueOf(this.options.maxFps);
            arrobject[2] = this.options.enableVsync ? " vsync" : "";
            Object object = arrobject[3] = this.options.fancyGraphics ? "" : " fast";
            arrobject[4] = this.options.cloudRenderMode == CloudRenderMode.OFF ? "" : (this.options.cloudRenderMode == CloudRenderMode.FAST ? " fast-clouds" : " fancy-clouds");
            arrobject[5] = this.options.biomeBlendRadius;
            this.fpsDebugString = String.format("%d fps T: %s%s%s%s B: %d", arrobject);
            this.nextDebugInfoUpdateTime += 1000L;
            this.fpsCounter = 0;
            this.snooper.update();
            if (this.snooper.isActive()) continue;
            this.snooper.method_5482();
        }
        this.profiler.pop();
    }

    private boolean shouldMonitorTickDuration() {
        return this.options.debugEnabled && this.options.debugProfilerEnabled && !this.options.hudHidden;
    }

    private void startMonitor(boolean bl, @Nullable TickDurationMonitor arg) {
        if (bl) {
            if (!this.tickTimeTracker.isActive()) {
                this.trackingTick = 0;
                this.tickTimeTracker.enable();
            }
            ++this.trackingTick;
        } else {
            this.tickTimeTracker.disable();
        }
        this.profiler = TickDurationMonitor.tickProfiler(this.tickTimeTracker.getProfiler(), arg);
    }

    private void endMonitor(boolean bl, @Nullable TickDurationMonitor arg) {
        if (arg != null) {
            arg.endTick();
        }
        this.tickProfilerResult = bl ? this.tickTimeTracker.getResult() : null;
        this.profiler = this.tickTimeTracker.getProfiler();
    }

    @Override
    public void onResolutionChanged() {
        int i = this.window.calculateScaleFactor(this.options.guiScale, this.forcesUnicodeFont());
        this.window.setScaleFactor(i);
        if (this.currentScreen != null) {
            this.currentScreen.resize(this, this.window.getScaledWidth(), this.window.getScaledHeight());
        }
        Framebuffer lv = this.getFramebuffer();
        lv.resize(this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), IS_SYSTEM_MAC);
        this.gameRenderer.onResized(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        this.mouse.onResolutionChanged();
    }

    private int getFramerateLimit() {
        if (this.world == null && (this.currentScreen != null || this.overlay != null)) {
            return 60;
        }
        return this.window.getFramerateLimit();
    }

    public void cleanUpAfterCrash() {
        try {
            memoryReservedForCrash = new byte[0];
            this.worldRenderer.method_3267();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            System.gc();
            if (this.isIntegratedServerRunning && this.server != null) {
                this.server.stop(true);
            }
            this.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        System.gc();
    }

    void handleProfilerKeyPress(int i) {
        if (this.tickProfilerResult == null) {
            return;
        }
        List<ProfilerTiming> list = this.tickProfilerResult.getTimings(this.openProfilerSection);
        if (list.isEmpty()) {
            return;
        }
        ProfilerTiming lv = list.remove(0);
        if (i == 0) {
            int j;
            if (!lv.name.isEmpty() && (j = this.openProfilerSection.lastIndexOf(30)) >= 0) {
                this.openProfilerSection = this.openProfilerSection.substring(0, j);
            }
        } else if (--i < list.size() && !"unspecified".equals(list.get((int)i).name)) {
            if (!this.openProfilerSection.isEmpty()) {
                this.openProfilerSection = this.openProfilerSection + '\u001e';
            }
            this.openProfilerSection = this.openProfilerSection + list.get((int)i).name;
        }
    }

    private void drawProfilerResults(MatrixStack arg, ProfileResult arg2) {
        List<ProfilerTiming> list = arg2.getTimings(this.openProfilerSection);
        ProfilerTiming lv = list.remove(0);
        RenderSystem.clear(256, IS_SYSTEM_MAC);
        RenderSystem.matrixMode(5889);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0.0, this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), 0.0, 1000.0, 3000.0);
        RenderSystem.matrixMode(5888);
        RenderSystem.loadIdentity();
        RenderSystem.translatef(0.0f, 0.0f, -2000.0f);
        RenderSystem.lineWidth(1.0f);
        RenderSystem.disableTexture();
        Tessellator lv2 = Tessellator.getInstance();
        BufferBuilder lv3 = lv2.getBuffer();
        int i = 160;
        int j = this.window.getFramebufferWidth() - 160 - 10;
        int k = this.window.getFramebufferHeight() - 320;
        RenderSystem.enableBlend();
        lv3.begin(7, VertexFormats.POSITION_COLOR);
        lv3.vertex((float)j - 176.0f, (float)k - 96.0f - 16.0f, 0.0).color(200, 0, 0, 0).next();
        lv3.vertex((float)j - 176.0f, k + 320, 0.0).color(200, 0, 0, 0).next();
        lv3.vertex((float)j + 176.0f, k + 320, 0.0).color(200, 0, 0, 0).next();
        lv3.vertex((float)j + 176.0f, (float)k - 96.0f - 16.0f, 0.0).color(200, 0, 0, 0).next();
        lv2.draw();
        RenderSystem.disableBlend();
        double d = 0.0;
        for (ProfilerTiming lv4 : list) {
            int l = MathHelper.floor(lv4.parentSectionUsagePercentage / 4.0) + 1;
            lv3.begin(6, VertexFormats.POSITION_COLOR);
            int m = lv4.getColor();
            int n = m >> 16 & 0xFF;
            int o = m >> 8 & 0xFF;
            int p = m & 0xFF;
            lv3.vertex(j, k, 0.0).color(n, o, p, 255).next();
            for (int q = l; q >= 0; --q) {
                float f = (float)((d + lv4.parentSectionUsagePercentage * (double)q / (double)l) * 6.2831854820251465 / 100.0);
                float g = MathHelper.sin(f) * 160.0f;
                float h = MathHelper.cos(f) * 160.0f * 0.5f;
                lv3.vertex((float)j + g, (float)k - h, 0.0).color(n, o, p, 255).next();
            }
            lv2.draw();
            lv3.begin(5, VertexFormats.POSITION_COLOR);
            for (int r = l; r >= 0; --r) {
                float s = (float)((d + lv4.parentSectionUsagePercentage * (double)r / (double)l) * 6.2831854820251465 / 100.0);
                float t = MathHelper.sin(s) * 160.0f;
                float u = MathHelper.cos(s) * 160.0f * 0.5f;
                if (u > 0.0f) continue;
                lv3.vertex((float)j + t, (float)k - u, 0.0).color(n >> 1, o >> 1, p >> 1, 255).next();
                lv3.vertex((float)j + t, (float)k - u + 10.0f, 0.0).color(n >> 1, o >> 1, p >> 1, 255).next();
            }
            lv2.draw();
            d += lv4.parentSectionUsagePercentage;
        }
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
        RenderSystem.enableTexture();
        String string = ProfileResult.getHumanReadableName(lv.name);
        String string2 = "";
        if (!"unspecified".equals(string)) {
            string2 = string2 + "[0] ";
        }
        string2 = string.isEmpty() ? string2 + "ROOT " : string2 + string + ' ';
        int v = 0xFFFFFF;
        this.textRenderer.drawWithShadow(arg, string2, (float)(j - 160), (float)(k - 80 - 16), 0xFFFFFF);
        string2 = decimalFormat.format(lv.totalUsagePercentage) + "%";
        this.textRenderer.drawWithShadow(arg, string2, (float)(j + 160 - this.textRenderer.getWidth(string2)), (float)(k - 80 - 16), 0xFFFFFF);
        for (int w = 0; w < list.size(); ++w) {
            ProfilerTiming lv5 = list.get(w);
            StringBuilder stringBuilder = new StringBuilder();
            if ("unspecified".equals(lv5.name)) {
                stringBuilder.append("[?] ");
            } else {
                stringBuilder.append("[").append(w + 1).append("] ");
            }
            String string3 = stringBuilder.append(lv5.name).toString();
            this.textRenderer.drawWithShadow(arg, string3, (float)(j - 160), (float)(k + 80 + w * 8 + 20), lv5.getColor());
            string3 = decimalFormat.format(lv5.parentSectionUsagePercentage) + "%";
            this.textRenderer.drawWithShadow(arg, string3, (float)(j + 160 - 50 - this.textRenderer.getWidth(string3)), (float)(k + 80 + w * 8 + 20), lv5.getColor());
            string3 = decimalFormat.format(lv5.totalUsagePercentage) + "%";
            this.textRenderer.drawWithShadow(arg, string3, (float)(j + 160 - this.textRenderer.getWidth(string3)), (float)(k + 80 + w * 8 + 20), lv5.getColor());
        }
    }

    public void scheduleStop() {
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void openPauseMenu(boolean bl) {
        boolean bl2;
        if (this.currentScreen != null) {
            return;
        }
        boolean bl3 = bl2 = this.isIntegratedServerRunning() && !this.server.isRemote();
        if (bl2) {
            this.openScreen(new GameMenuScreen(!bl));
            this.soundManager.pauseAll();
        } else {
            this.openScreen(new GameMenuScreen(true));
        }
    }

    private void handleBlockBreaking(boolean bl) {
        if (!bl) {
            this.attackCooldown = 0;
        }
        if (this.attackCooldown > 0 || this.player.isUsingItem()) {
            return;
        }
        if (bl && this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            Direction lv3;
            BlockHitResult lv = (BlockHitResult)this.crosshairTarget;
            BlockPos lv2 = lv.getBlockPos();
            if (!this.world.getBlockState(lv2).isAir() && this.interactionManager.updateBlockBreakingProgress(lv2, lv3 = lv.getSide())) {
                this.particleManager.addBlockBreakingParticles(lv2, lv3);
                this.player.swingHand(Hand.MAIN_HAND);
            }
            return;
        }
        this.interactionManager.cancelBlockBreaking();
    }

    private void doAttack() {
        if (this.attackCooldown > 0) {
            return;
        }
        if (this.crosshairTarget == null) {
            LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.interactionManager.hasLimitedAttackSpeed()) {
                this.attackCooldown = 10;
            }
            return;
        }
        if (this.player.isRiding()) {
            return;
        }
        switch (this.crosshairTarget.getType()) {
            case ENTITY: {
                this.interactionManager.attackEntity(this.player, ((EntityHitResult)this.crosshairTarget).getEntity());
                break;
            }
            case BLOCK: {
                BlockHitResult lv = (BlockHitResult)this.crosshairTarget;
                BlockPos lv2 = lv.getBlockPos();
                if (!this.world.getBlockState(lv2).isAir()) {
                    this.interactionManager.attackBlock(lv2, lv.getSide());
                    break;
                }
            }
            case MISS: {
                if (this.interactionManager.hasLimitedAttackSpeed()) {
                    this.attackCooldown = 10;
                }
                this.player.resetLastAttackedTicks();
            }
        }
        this.player.swingHand(Hand.MAIN_HAND);
    }

    private void doItemUse() {
        if (this.interactionManager.isBreakingBlock()) {
            return;
        }
        this.itemUseCooldown = 4;
        if (this.player.isRiding()) {
            return;
        }
        if (this.crosshairTarget == null) {
            LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
        }
        for (Hand lv : Hand.values()) {
            ActionResult lv8;
            ItemStack lv2 = this.player.getStackInHand(lv);
            if (this.crosshairTarget != null) {
                switch (this.crosshairTarget.getType()) {
                    case ENTITY: {
                        EntityHitResult lv3 = (EntityHitResult)this.crosshairTarget;
                        Entity lv4 = lv3.getEntity();
                        ActionResult lv5 = this.interactionManager.interactEntityAtLocation(this.player, lv4, lv3, lv);
                        if (!lv5.isAccepted()) {
                            lv5 = this.interactionManager.interactEntity(this.player, lv4, lv);
                        }
                        if (!lv5.isAccepted()) break;
                        if (lv5.shouldSwingHand()) {
                            this.player.swingHand(lv);
                        }
                        return;
                    }
                    case BLOCK: {
                        BlockHitResult lv6 = (BlockHitResult)this.crosshairTarget;
                        int i = lv2.getCount();
                        ActionResult lv7 = this.interactionManager.interactBlock(this.player, this.world, lv, lv6);
                        if (lv7.isAccepted()) {
                            if (lv7.shouldSwingHand()) {
                                this.player.swingHand(lv);
                                if (!lv2.isEmpty() && (lv2.getCount() != i || this.interactionManager.hasCreativeInventory())) {
                                    this.gameRenderer.firstPersonRenderer.resetEquipProgress(lv);
                                }
                            }
                            return;
                        }
                        if (lv7 != ActionResult.FAIL) break;
                        return;
                    }
                }
            }
            if (lv2.isEmpty() || !(lv8 = this.interactionManager.interactItem(this.player, this.world, lv)).isAccepted()) continue;
            if (lv8.shouldSwingHand()) {
                this.player.swingHand(lv);
            }
            this.gameRenderer.firstPersonRenderer.resetEquipProgress(lv);
            return;
        }
    }

    public MusicTracker getMusicTracker() {
        return this.musicTracker;
    }

    public void tick() {
        if (this.itemUseCooldown > 0) {
            --this.itemUseCooldown;
        }
        this.profiler.push("gui");
        if (!this.paused) {
            this.inGameHud.tick();
        }
        this.profiler.pop();
        this.gameRenderer.updateTargetedEntity(1.0f);
        this.tutorialManager.tick(this.world, this.crosshairTarget);
        this.profiler.push("gameMode");
        if (!this.paused && this.world != null) {
            this.interactionManager.tick();
        }
        this.profiler.swap("textures");
        if (this.world != null) {
            this.textureManager.tick();
        }
        if (this.currentScreen == null && this.player != null) {
            if (this.player.getHealth() <= 0.0f && !(this.currentScreen instanceof DeathScreen)) {
                this.openScreen(null);
            } else if (this.player.isSleeping() && this.world != null) {
                this.openScreen(new SleepingChatScreen());
            }
        } else if (this.currentScreen != null && this.currentScreen instanceof SleepingChatScreen && !this.player.isSleeping()) {
            this.openScreen(null);
        }
        if (this.currentScreen != null) {
            this.attackCooldown = 10000;
        }
        if (this.currentScreen != null) {
            Screen.wrapScreenError(() -> this.currentScreen.tick(), "Ticking screen", this.currentScreen.getClass().getCanonicalName());
        }
        if (!this.options.debugEnabled) {
            this.inGameHud.resetDebugHudChunk();
        }
        if (this.overlay == null && (this.currentScreen == null || this.currentScreen.passEvents)) {
            this.profiler.swap("Keybindings");
            this.handleInputEvents();
            if (this.attackCooldown > 0) {
                --this.attackCooldown;
            }
        }
        if (this.world != null) {
            this.profiler.swap("gameRenderer");
            if (!this.paused) {
                this.gameRenderer.tick();
            }
            this.profiler.swap("levelRenderer");
            if (!this.paused) {
                this.worldRenderer.tick();
            }
            this.profiler.swap("level");
            if (!this.paused) {
                if (this.world.getLightningTicksLeft() > 0) {
                    this.world.setLightningTicksLeft(this.world.getLightningTicksLeft() - 1);
                }
                this.world.tickEntities();
            }
        } else if (this.gameRenderer.getShader() != null) {
            this.gameRenderer.disableShader();
        }
        if (!this.paused) {
            this.musicTracker.tick();
        }
        this.soundManager.tick(this.paused);
        if (this.world != null) {
            if (!this.paused) {
                this.tutorialManager.tick();
                try {
                    this.world.tick(() -> true);
                }
                catch (Throwable throwable) {
                    CrashReport lv = CrashReport.create(throwable, "Exception in world tick");
                    if (this.world == null) {
                        CrashReportSection lv2 = lv.addElement("Affected level");
                        lv2.add("Problem", "Level is null!");
                    } else {
                        this.world.addDetailsToCrashReport(lv);
                    }
                    throw new CrashException(lv);
                }
            }
            this.profiler.swap("animateTick");
            if (!this.paused && this.world != null) {
                this.world.doRandomBlockDisplayTicks(MathHelper.floor(this.player.getX()), MathHelper.floor(this.player.getY()), MathHelper.floor(this.player.getZ()));
            }
            this.profiler.swap("particles");
            if (!this.paused) {
                this.particleManager.tick();
            }
        } else if (this.connection != null) {
            this.profiler.swap("pendingConnection");
            this.connection.tick();
        }
        this.profiler.swap("keyboard");
        this.keyboard.pollDebugCrash();
        this.profiler.pop();
    }

    private void handleInputEvents() {
        boolean bl3;
        while (this.options.keyTogglePerspective.wasPressed()) {
            ++this.options.perspective;
            if (this.options.perspective > 2) {
                this.options.perspective = 0;
            }
            if (this.options.perspective == 0) {
                this.gameRenderer.onCameraEntitySet(this.getCameraEntity());
            } else if (this.options.perspective == 1) {
                this.gameRenderer.onCameraEntitySet(null);
            }
            this.worldRenderer.scheduleTerrainUpdate();
        }
        while (this.options.keySmoothCamera.wasPressed()) {
            this.options.smoothCameraEnabled = !this.options.smoothCameraEnabled;
        }
        for (int i = 0; i < 9; ++i) {
            boolean bl = this.options.keySaveToolbarActivator.isPressed();
            boolean bl2 = this.options.keyLoadToolbarActivator.isPressed();
            if (!this.options.keysHotbar[i].wasPressed()) continue;
            if (this.player.isSpectator()) {
                this.inGameHud.getSpectatorHud().selectSlot(i);
                continue;
            }
            if (this.player.isCreative() && this.currentScreen == null && (bl2 || bl)) {
                CreativeInventoryScreen.onHotbarKeyPress(this, i, bl2, bl);
                continue;
            }
            this.player.inventory.selectedSlot = i;
        }
        while (this.options.keyInventory.wasPressed()) {
            if (this.interactionManager.hasRidingInventory()) {
                this.player.openRidingInventory();
                continue;
            }
            this.tutorialManager.onInventoryOpened();
            this.openScreen(new InventoryScreen(this.player));
        }
        while (this.options.keyAdvancements.wasPressed()) {
            this.openScreen(new AdvancementsScreen(this.player.networkHandler.getAdvancementHandler()));
        }
        while (this.options.keySwapHands.wasPressed()) {
            if (this.player.isSpectator()) continue;
            this.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_HELD_ITEMS, BlockPos.ORIGIN, Direction.DOWN));
        }
        while (this.options.keyDrop.wasPressed()) {
            if (this.player.isSpectator() || !this.player.dropSelectedItem(Screen.hasControlDown())) continue;
            this.player.swingHand(Hand.MAIN_HAND);
        }
        boolean bl = bl3 = this.options.chatVisibility != ChatVisibility.HIDDEN;
        if (bl3) {
            while (this.options.keyChat.wasPressed()) {
                this.openScreen(new ChatScreen(""));
            }
            if (this.currentScreen == null && this.overlay == null && this.options.keyCommand.wasPressed()) {
                this.openScreen(new ChatScreen("/"));
            }
        }
        if (this.player.isUsingItem()) {
            if (!this.options.keyUse.isPressed()) {
                this.interactionManager.stopUsingItem(this.player);
            }
            while (this.options.keyAttack.wasPressed()) {
            }
            while (this.options.keyUse.wasPressed()) {
            }
            while (this.options.keyPickItem.wasPressed()) {
            }
        } else {
            while (this.options.keyAttack.wasPressed()) {
                this.doAttack();
            }
            while (this.options.keyUse.wasPressed()) {
                this.doItemUse();
            }
            while (this.options.keyPickItem.wasPressed()) {
                this.doItemPick();
            }
        }
        if (this.options.keyUse.isPressed() && this.itemUseCooldown == 0 && !this.player.isUsingItem()) {
            this.doItemUse();
        }
        this.handleBlockBreaking(this.currentScreen == null && this.options.keyAttack.isPressed() && this.mouse.isCursorLocked());
    }

    /*
     * WARNING - void declaration
     */
    public void startIntegratedServer(String string, @Nullable LevelInfo arg2) {
        String string3;
        void lv2;
        this.disconnect();
        try {
            LevelStorage.Session lv = this.levelStorage.createSession(string);
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to read level {} data", (Object)string, (Object)iOException);
            SystemToast.addWorldAccessFailureToast(this, string);
            this.openScreen(null);
            return;
        }
        class_5219 lv3 = lv2.readLevelProperties();
        if (lv3 == null) {
            if (arg2 == null) {
                throw new IllegalStateException("Requested world creation without any settings");
            }
            lv3 = new LevelProperties(arg2);
            String string2 = arg2.getLevelName();
            lv2.method_27425(lv3);
        } else {
            string3 = lv3.getLevelName();
        }
        this.worldGenProgressTracker.set(null);
        try {
            YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(this.netProxy, UUID.randomUUID().toString());
            MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
            GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
            UserCache lv4 = new UserCache(gameProfileRepository, new File(this.runDirectory, MinecraftServer.USER_CACHE_FILE.getName()));
            SkullBlockEntity.setUserCache(lv4);
            SkullBlockEntity.setSessionService(minecraftSessionService);
            UserCache.setUseRemote(false);
            this.server = new IntegratedServer(this, (LevelStorage.Session)lv2, lv3, minecraftSessionService, gameProfileRepository, lv4, i -> {
                WorldGenerationProgressTracker lv = new WorldGenerationProgressTracker(i + 0);
                lv.start();
                this.worldGenProgressTracker.set(lv);
                return new QueueingWorldGenerationProgressListener(lv, this.renderTaskQueue::add);
            });
            this.server.start();
            this.isIntegratedServerRunning = true;
        }
        catch (Throwable throwable) {
            CrashReport lv5 = CrashReport.create(throwable, "Starting integrated server");
            CrashReportSection lv6 = lv5.addElement("Starting integrated server");
            lv6.add("Level ID", string);
            lv6.add("Level Name", string3);
            throw new CrashException(lv5);
        }
        while (this.worldGenProgressTracker.get() == null) {
            Thread.yield();
        }
        LevelLoadingScreen lv7 = new LevelLoadingScreen(this.worldGenProgressTracker.get());
        this.openScreen(lv7);
        this.profiler.push("waitForServer");
        while (!this.server.isLoading()) {
            lv7.tick();
            this.render(false);
            try {
                Thread.sleep(16L);
            }
            catch (InterruptedException lv5) {
                // empty catch block
            }
            if (this.crashReport == null) continue;
            MinecraftClient.printCrashReport(this.crashReport);
            return;
        }
        this.profiler.pop();
        SocketAddress socketAddress = this.server.getNetworkIo().bindLocal();
        ClientConnection lv8 = ClientConnection.connectLocal(socketAddress);
        lv8.setPacketListener(new ClientLoginNetworkHandler(lv8, this, null, arg -> {}));
        lv8.send(new HandshakeC2SPacket(socketAddress.toString(), 0, NetworkState.LOGIN));
        lv8.send(new LoginHelloC2SPacket(this.getSession().getProfile()));
        this.connection = lv8;
    }

    public void joinWorld(ClientWorld arg) {
        ProgressScreen lv = new ProgressScreen();
        lv.method_15412(new TranslatableText("connect.joining"));
        this.reset(lv);
        this.world = arg;
        this.setWorld(arg);
        if (!this.isIntegratedServerRunning) {
            YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(this.netProxy, UUID.randomUUID().toString());
            MinecraftSessionService minecraftSessionService = authenticationService.createMinecraftSessionService();
            GameProfileRepository gameProfileRepository = authenticationService.createProfileRepository();
            UserCache lv2 = new UserCache(gameProfileRepository, new File(this.runDirectory, MinecraftServer.USER_CACHE_FILE.getName()));
            SkullBlockEntity.setUserCache(lv2);
            SkullBlockEntity.setSessionService(minecraftSessionService);
            UserCache.setUseRemote(false);
        }
    }

    public void disconnect() {
        this.disconnect(new ProgressScreen());
    }

    public void disconnect(Screen arg) {
        ClientPlayNetworkHandler lv = this.getNetworkHandler();
        if (lv != null) {
            this.cancelTasks();
            lv.clearWorld();
        }
        IntegratedServer lv2 = this.server;
        this.server = null;
        this.gameRenderer.reset();
        this.interactionManager = null;
        NarratorManager.INSTANCE.clear();
        this.reset(arg);
        if (this.world != null) {
            if (lv2 != null) {
                this.profiler.push("waitForServer");
                while (!lv2.isStopping()) {
                    this.render(false);
                }
                this.profiler.pop();
            }
            this.builtinPackProvider.clear();
            this.inGameHud.clear();
            this.currentServerEntry = null;
            this.isIntegratedServerRunning = false;
            this.game.onLeaveGameSession();
        }
        this.world = null;
        this.setWorld(null);
        this.player = null;
    }

    private void reset(Screen arg) {
        this.profiler.push("forcedTick");
        this.musicTracker.stop();
        this.soundManager.stopAll();
        this.cameraEntity = null;
        this.connection = null;
        this.openScreen(arg);
        this.render(false);
        this.profiler.pop();
    }

    private void setWorld(@Nullable ClientWorld arg) {
        this.worldRenderer.setWorld(arg);
        this.particleManager.setWorld(arg);
        BlockEntityRenderDispatcher.INSTANCE.setWorld(arg);
        this.updateWindowTitle();
    }

    public final boolean isDemo() {
        return this.isDemo;
    }

    @Nullable
    public ClientPlayNetworkHandler getNetworkHandler() {
        return this.player == null ? null : this.player.networkHandler;
    }

    public static boolean isHudEnabled() {
        return !MinecraftClient.instance.options.hudHidden;
    }

    public static boolean isFancyGraphicsEnabled() {
        return MinecraftClient.instance.options.fancyGraphics;
    }

    public static boolean isAmbientOcclusionEnabled() {
        return MinecraftClient.instance.options.ao != AoOption.OFF;
    }

    /*
     * WARNING - void declaration
     */
    private void doItemPick() {
        void lv27;
        if (this.crosshairTarget == null || this.crosshairTarget.getType() == HitResult.Type.MISS) {
            return;
        }
        boolean bl = this.player.abilities.creativeMode;
        BlockEntity lv = null;
        HitResult.Type lv2 = this.crosshairTarget.getType();
        if (lv2 == HitResult.Type.BLOCK) {
            BlockPos lv3 = ((BlockHitResult)this.crosshairTarget).getBlockPos();
            BlockState lv4 = this.world.getBlockState(lv3);
            Block lv5 = lv4.getBlock();
            if (lv4.isAir()) {
                return;
            }
            ItemStack lv6 = lv5.getPickStack(this.world, lv3, lv4);
            if (lv6.isEmpty()) {
                return;
            }
            if (bl && Screen.hasControlDown() && lv5.hasBlockEntity()) {
                lv = this.world.getBlockEntity(lv3);
            }
        } else if (lv2 == HitResult.Type.ENTITY && bl) {
            Entity lv7 = ((EntityHitResult)this.crosshairTarget).getEntity();
            if (lv7 instanceof PaintingEntity) {
                ItemStack lv8 = new ItemStack(Items.PAINTING);
            } else if (lv7 instanceof LeashKnotEntity) {
                ItemStack lv9 = new ItemStack(Items.LEAD);
            } else if (lv7 instanceof ItemFrameEntity) {
                ItemFrameEntity lv10 = (ItemFrameEntity)lv7;
                ItemStack lv11 = lv10.getHeldItemStack();
                if (lv11.isEmpty()) {
                    ItemStack lv12 = new ItemStack(Items.ITEM_FRAME);
                } else {
                    ItemStack lv13 = lv11.copy();
                }
            } else if (lv7 instanceof AbstractMinecartEntity) {
                Item lv20;
                AbstractMinecartEntity lv14 = (AbstractMinecartEntity)lv7;
                switch (lv14.getMinecartType()) {
                    case FURNACE: {
                        Item lv15 = Items.FURNACE_MINECART;
                        break;
                    }
                    case CHEST: {
                        Item lv16 = Items.CHEST_MINECART;
                        break;
                    }
                    case TNT: {
                        Item lv17 = Items.TNT_MINECART;
                        break;
                    }
                    case HOPPER: {
                        Item lv18 = Items.HOPPER_MINECART;
                        break;
                    }
                    case COMMAND_BLOCK: {
                        Item lv19 = Items.COMMAND_BLOCK_MINECART;
                        break;
                    }
                    default: {
                        lv20 = Items.MINECART;
                    }
                }
                ItemStack lv21 = new ItemStack(lv20);
            } else if (lv7 instanceof BoatEntity) {
                ItemStack lv22 = new ItemStack(((BoatEntity)lv7).asItem());
            } else if (lv7 instanceof ArmorStandEntity) {
                ItemStack lv23 = new ItemStack(Items.ARMOR_STAND);
            } else if (lv7 instanceof EndCrystalEntity) {
                ItemStack lv24 = new ItemStack(Items.END_CRYSTAL);
            } else {
                SpawnEggItem lv25 = SpawnEggItem.forEntity(lv7.getType());
                if (lv25 == null) {
                    return;
                }
                ItemStack lv26 = new ItemStack(lv25);
            }
        } else {
            return;
        }
        if (lv27.isEmpty()) {
            String string = "";
            if (lv2 == HitResult.Type.BLOCK) {
                string = Registry.BLOCK.getId(this.world.getBlockState(((BlockHitResult)this.crosshairTarget).getBlockPos()).getBlock()).toString();
            } else if (lv2 == HitResult.Type.ENTITY) {
                string = Registry.ENTITY_TYPE.getId(((EntityHitResult)this.crosshairTarget).getEntity().getType()).toString();
            }
            LOGGER.warn("Picking on: [{}] {} gave null item", (Object)lv2, (Object)string);
            return;
        }
        PlayerInventory lv28 = this.player.inventory;
        if (lv != null) {
            this.addBlockEntityNbt((ItemStack)lv27, lv);
        }
        int i = lv28.getSlotWithStack((ItemStack)lv27);
        if (bl) {
            lv28.addPickBlock((ItemStack)lv27);
            this.interactionManager.clickCreativeStack(this.player.getStackInHand(Hand.MAIN_HAND), 36 + lv28.selectedSlot);
        } else if (i != -1) {
            if (PlayerInventory.isValidHotbarIndex(i)) {
                lv28.selectedSlot = i;
            } else {
                this.interactionManager.pickFromInventory(i);
            }
        }
    }

    private ItemStack addBlockEntityNbt(ItemStack arg, BlockEntity arg2) {
        CompoundTag lv = arg2.toTag(new CompoundTag());
        if (arg.getItem() instanceof SkullItem && lv.contains("Owner")) {
            CompoundTag lv2 = lv.getCompound("Owner");
            arg.getOrCreateTag().put("SkullOwner", lv2);
            return arg;
        }
        arg.putSubTag("BlockEntityTag", lv);
        CompoundTag lv3 = new CompoundTag();
        ListTag lv4 = new ListTag();
        lv4.add(StringTag.of("\"(+NBT)\""));
        lv3.put("Lore", lv4);
        arg.putSubTag("display", lv3);
        return arg;
    }

    public CrashReport addDetailsToCrashReport(CrashReport arg) {
        MinecraftClient.addSystemDetailsToCrashReport(this.languageManager, this.gameVersion, this.options, arg);
        if (this.world != null) {
            this.world.addDetailsToCrashReport(arg);
        }
        return arg;
    }

    public static void addSystemDetailsToCrashReport(@Nullable LanguageManager arg, String string, @Nullable GameOptions arg2, CrashReport arg3) {
        CrashReportSection lv = arg3.getSystemDetailsSection();
        lv.add("Launched Version", () -> string);
        lv.add("Backend library", RenderSystem::getBackendDescription);
        lv.add("Backend API", RenderSystem::getApiDescription);
        lv.add("GL Caps", RenderSystem::getCapsString);
        lv.add("Using VBOs", () -> "Yes");
        lv.add("Is Modded", () -> {
            String string = ClientBrandRetriever.getClientModName();
            if (!"vanilla".equals(string)) {
                return "Definitely; Client brand changed to '" + string + "'";
            }
            if (MinecraftClient.class.getSigners() == null) {
                return "Very likely; Jar signature invalidated";
            }
            return "Probably not. Jar signature remains and client brand is untouched.";
        });
        lv.add("Type", "Client (map_client.txt)");
        if (arg2 != null) {
            lv.add("Resource Packs", () -> {
                StringBuilder stringBuilder = new StringBuilder();
                for (String string : arg.resourcePacks) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(string);
                    if (!arg.incompatibleResourcePacks.contains(string)) continue;
                    stringBuilder.append(" (incompatible)");
                }
                return stringBuilder.toString();
            });
        }
        if (arg != null) {
            lv.add("Current Language", () -> arg.getLanguage().toString());
        }
        lv.add("CPU", GlDebugInfo::getCpuInfo);
    }

    public static MinecraftClient getInstance() {
        return instance;
    }

    public CompletableFuture<Void> reloadResourcesConcurrently() {
        return this.submit(this::reloadResources).thenCompose(completableFuture -> completableFuture);
    }

    @Override
    public void addSnooperInfo(Snooper arg) {
        arg.addInfo("fps", currentFps);
        arg.addInfo("vsync_enabled", this.options.enableVsync);
        arg.addInfo("display_frequency", this.window.getRefreshRate());
        arg.addInfo("display_type", this.window.isFullscreen() ? "fullscreen" : "windowed");
        arg.addInfo("run_time", (Util.getMeasuringTimeMs() - arg.getStartTime()) / 60L * 1000L);
        arg.addInfo("current_action", this.getCurrentAction());
        arg.addInfo("language", this.options.language == null ? "en_us" : this.options.language);
        String string = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
        arg.addInfo("endianness", string);
        arg.addInfo("subtitles", this.options.showSubtitles);
        arg.addInfo("touch", this.options.touchscreen ? "touch" : "mouse");
        int i = 0;
        for (ClientResourcePackProfile lv : this.resourcePackManager.getEnabledProfiles()) {
            if (lv.isAlwaysEnabled() || lv.isPinned()) continue;
            arg.addInfo("resource_pack[" + i++ + "]", lv.getName());
        }
        arg.addInfo("resource_packs", i);
        if (this.server != null) {
            arg.addInfo("snooper_partner", this.server.getSnooper().getToken());
        }
    }

    private String getCurrentAction() {
        if (this.server != null) {
            if (this.server.isRemote()) {
                return "hosting_lan";
            }
            return "singleplayer";
        }
        if (this.currentServerEntry != null) {
            if (this.currentServerEntry.isLocal()) {
                return "playing_lan";
            }
            return "multiplayer";
        }
        return "out_of_game";
    }

    public void setCurrentServerEntry(@Nullable ServerInfo arg) {
        this.currentServerEntry = arg;
    }

    @Nullable
    public ServerInfo getCurrentServerEntry() {
        return this.currentServerEntry;
    }

    public boolean isInSingleplayer() {
        return this.isIntegratedServerRunning;
    }

    public boolean isIntegratedServerRunning() {
        return this.isIntegratedServerRunning && this.server != null;
    }

    @Nullable
    public IntegratedServer getServer() {
        return this.server;
    }

    public Snooper getSnooper() {
        return this.snooper;
    }

    public Session getSession() {
        return this.session;
    }

    public PropertyMap getSessionProperties() {
        if (this.sessionPropertyMap.isEmpty()) {
            GameProfile gameProfile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
            this.sessionPropertyMap.putAll((Multimap)gameProfile.getProperties());
        }
        return this.sessionPropertyMap;
    }

    public Proxy getNetworkProxy() {
        return this.netProxy;
    }

    public TextureManager getTextureManager() {
        return this.textureManager;
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public ResourcePackManager<ClientResourcePackProfile> getResourcePackManager() {
        return this.resourcePackManager;
    }

    public ClientBuiltinResourcePackProvider getResourcePackDownloader() {
        return this.builtinPackProvider;
    }

    public File getResourcePackDir() {
        return this.resourcePackDir;
    }

    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }

    public Function<Identifier, Sprite> getSpriteAtlas(Identifier arg) {
        return this.bakedModelManager.method_24153(arg)::getSprite;
    }

    public boolean is64Bit() {
        return this.is64Bit;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public SoundManager getSoundManager() {
        return this.soundManager;
    }

    public MusicSound getMusicType() {
        if (this.currentScreen instanceof CreditsScreen) {
            return MusicType.CREDITS;
        }
        if (this.player != null) {
            if (this.player.world.getDimension() instanceof TheEndDimension) {
                if (this.inGameHud.getBossBarHud().shouldPlayDragonMusic()) {
                    return MusicType.DRAGON;
                }
                return MusicType.END;
            }
            Biome.Category lv = this.player.world.getBiome(this.player.getBlockPos()).getCategory();
            if (this.musicTracker.isPlayingType(MusicType.UNDERWATER) || this.player.isSubmergedInWater() && (lv == Biome.Category.OCEAN || lv == Biome.Category.RIVER)) {
                return MusicType.UNDERWATER;
            }
            if (this.player.abilities.creativeMode && this.player.abilities.allowFlying) {
                return MusicType.CREATIVE;
            }
            return this.world.getBiomeAccess().method_27344(this.player.getBlockPos()).method_27343().orElse(MusicType.GAME);
        }
        return MusicType.MENU;
    }

    public MinecraftSessionService getSessionService() {
        return this.sessionService;
    }

    public PlayerSkinProvider getSkinProvider() {
        return this.skinProvider;
    }

    @Nullable
    public Entity getCameraEntity() {
        return this.cameraEntity;
    }

    public void setCameraEntity(Entity arg) {
        this.cameraEntity = arg;
        this.gameRenderer.onCameraEntitySet(arg);
    }

    public boolean method_27022(Entity arg) {
        return arg.isGlowing() || this.player != null && this.player.isSpectator() && this.options.keySpectatorOutlines.isPressed() && arg.getType() == EntityType.PLAYER;
    }

    @Override
    protected Thread getThread() {
        return this.thread;
    }

    @Override
    protected Runnable createTask(Runnable runnable) {
        return runnable;
    }

    @Override
    protected boolean canExecute(Runnable runnable) {
        return true;
    }

    public BlockRenderManager getBlockRenderManager() {
        return this.blockRenderManager;
    }

    public EntityRenderDispatcher getEntityRenderManager() {
        return this.entityRenderDispatcher;
    }

    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }

    public HeldItemRenderer getHeldItemRenderer() {
        return this.heldItemRenderer;
    }

    public <T> SearchableContainer<T> getSearchableContainer(SearchManager.Key<T> arg) {
        return this.searchManager.get(arg);
    }

    public MetricsData getMetricsData() {
        return this.metricsData;
    }

    public boolean isConnectedToRealms() {
        return this.connectedToRealms;
    }

    public void setConnectedToRealms(boolean bl) {
        this.connectedToRealms = bl;
    }

    public DataFixer getDataFixer() {
        return this.dataFixer;
    }

    public float getTickDelta() {
        return this.renderTickCounter.tickDelta;
    }

    public float getLastFrameDuration() {
        return this.renderTickCounter.lastFrameDuration;
    }

    public BlockColors getBlockColorMap() {
        return this.blockColorMap;
    }

    public boolean hasReducedDebugInfo() {
        return this.player != null && this.player.getReducedDebugInfo() || this.options.reducedDebugInfo;
    }

    public ToastManager getToastManager() {
        return this.toastManager;
    }

    public TutorialManager getTutorialManager() {
        return this.tutorialManager;
    }

    public boolean isWindowFocused() {
        return this.windowFocused;
    }

    public HotbarStorage getCreativeHotbarStorage() {
        return this.creativeHotbarStorage;
    }

    public BakedModelManager getBakedModelManager() {
        return this.bakedModelManager;
    }

    public PaintingManager getPaintingManager() {
        return this.paintingManager;
    }

    public StatusEffectSpriteManager getStatusEffectSpriteManager() {
        return this.statusEffectSpriteManager;
    }

    @Override
    public void onWindowFocusChanged(boolean bl) {
        this.windowFocused = bl;
    }

    public Profiler getProfiler() {
        return this.profiler;
    }

    public MinecraftClientGame getGame() {
        return this.game;
    }

    public SplashTextResourceSupplier getSplashTextLoader() {
        return this.splashTextLoader;
    }

    @Nullable
    public Overlay getOverlay() {
        return this.overlay;
    }

    public boolean shouldRenderAsync() {
        return false;
    }

    public Window getWindow() {
        return this.window;
    }

    public BufferBuilderStorage getBufferBuilders() {
        return this.bufferBuilders;
    }

    private static ClientResourcePackProfile createResourcePackProfile(String string, boolean bl, Supplier<ResourcePack> supplier, ResourcePack arg, PackResourceMetadata arg2, ResourcePackProfile.InsertionPosition arg3) {
        int i = arg2.getPackFormat();
        Supplier<ResourcePack> supplier2 = supplier;
        if (i <= 3) {
            supplier2 = MinecraftClient.createV3ResoucePackFactory(supplier2);
        }
        if (i <= 4) {
            supplier2 = MinecraftClient.createV4ResourcePackFactory(supplier2);
        }
        return new ClientResourcePackProfile(string, bl, supplier2, arg, arg2, arg3);
    }

    private static Supplier<ResourcePack> createV3ResoucePackFactory(Supplier<ResourcePack> supplier) {
        return () -> new Format3ResourcePack((ResourcePack)supplier.get(), Format3ResourcePack.NEW_TO_OLD_MAP);
    }

    private static Supplier<ResourcePack> createV4ResourcePackFactory(Supplier<ResourcePack> supplier) {
        return () -> new Format4ResourcePack((ResourcePack)supplier.get());
    }

    public void resetMipmapLevels(int i) {
        this.bakedModelManager.resetMipmapLevels(i);
    }

    static {
        LOGGER = LogManager.getLogger();
        IS_SYSTEM_MAC = Util.getOperatingSystem() == Util.OperatingSystem.OSX;
        DEFAULT_FONT_ID = new Identifier("default");
        UNICODE_FONT_ID = new Identifier("uniform");
        ALT_TEXT_RENDERER_ID = new Identifier("alt");
        COMPLETED_UNIT_FUTURE = CompletableFuture.completedFuture(Unit.INSTANCE);
        memoryReservedForCrash = new byte[0xA00000];
    }
}

