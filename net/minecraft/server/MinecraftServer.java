/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.datafixers.DataFixer
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufOutputStream
 *  io.netty.buffer.Unpooled
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.scoreboard.ScoreboardState;
import net.minecraft.scoreboard.ScoreboardSynchronizer;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.ServerNetworkIo;
import net.minecraft.server.ServerTask;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagManager;
import net.minecraft.test.TestManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetricsData;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.TickDurationMonitor;
import net.minecraft.util.Unit;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.TickTimeTracker;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.util.snooper.SnooperListener;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ForcedChunkState;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.WanderingTraderManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.CatSpawner;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.PhantomSpawner;
import net.minecraft.world.gen.PillagerSpawner;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer
extends ReentrantThreadExecutor<ServerTask>
implements SnooperListener,
CommandOutput,
AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final File USER_CACHE_FILE = new File("usercache.json");
    public static final LevelInfo DEMO_LEVEL_INFO = new LevelInfo("Demo World", GameMode.SURVIVAL, false, Difficulty.NORMAL, false, new GameRules(), DataPackSettings.SAFE_MODE);
    protected final LevelStorage.Session session;
    protected final WorldSaveHandler field_24371;
    private final Snooper snooper = new Snooper("server", this, Util.getMeasuringTimeMs());
    private final List<Runnable> serverGuiTickables = Lists.newArrayList();
    private TickTimeTracker tickTimeTracker = new TickTimeTracker(Util.nanoTimeSupplier, this::getTicks);
    private Profiler profiler = DummyProfiler.INSTANCE;
    private final ServerNetworkIo networkIo;
    private final WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory;
    private final ServerMetadata metadata = new ServerMetadata();
    private final Random random = new Random();
    private final DataFixer dataFixer;
    private String serverIp;
    private int serverPort = -1;
    protected final RegistryTracker.Modifiable dimensionTracker;
    private final Map<RegistryKey<World>, ServerWorld> worlds = Maps.newLinkedHashMap();
    private PlayerManager playerManager;
    private volatile boolean running = true;
    private boolean stopped;
    private int ticks;
    protected final Proxy proxy;
    private boolean onlineMode;
    private boolean preventProxyConnections;
    private boolean pvpEnabled;
    private boolean flightEnabled;
    @Nullable
    private String motd;
    private int worldHeight;
    private int playerIdleTimeout;
    public final long[] lastTickLengths = new long[100];
    @Nullable
    private KeyPair keyPair;
    @Nullable
    private String userName;
    private boolean demo;
    private String resourcePackUrl = "";
    private String resourcePackHash = "";
    private volatile boolean loading;
    private long lastTimeReference;
    private boolean profilerStartQueued;
    private boolean forceGameMode;
    private final MinecraftSessionService sessionService;
    private final GameProfileRepository gameProfileRepo;
    private final UserCache userCache;
    private long lastPlayerSampleUpdate;
    private final Thread serverThread;
    private long timeReference = Util.getMeasuringTimeMs();
    private long field_19248;
    private boolean waitingForNextTick;
    @Environment(value=EnvType.CLIENT)
    private boolean iconFilePresent;
    private final ResourcePackManager dataPackManager;
    private final ServerScoreboard scoreboard = new ServerScoreboard(this);
    @Nullable
    private DataCommandStorage dataCommandStorage;
    private final BossBarManager bossBarManager = new BossBarManager();
    private final CommandFunctionManager commandFunctionManager;
    private final MetricsData metricsData = new MetricsData();
    private boolean enforceWhitelist;
    private float tickTime;
    private final Executor workerExecutor;
    @Nullable
    private String serverId;
    private ServerResourceManager serverResourceManager;
    private final StructureManager structureManager;
    protected final SaveProperties saveProperties;

    public static <S extends MinecraftServer> S startServer(Function<Thread, S> function) {
        AtomicReference<MinecraftServer> atomicReference = new AtomicReference<MinecraftServer>();
        Thread thread2 = new Thread(() -> ((MinecraftServer)atomicReference.get()).runServer(), "Server thread");
        thread2.setUncaughtExceptionHandler((thread, throwable) -> LOGGER.error((Object)throwable));
        MinecraftServer minecraftServer = (MinecraftServer)function.apply(thread2);
        atomicReference.set(minecraftServer);
        thread2.start();
        return (S)minecraftServer;
    }

    public MinecraftServer(Thread thread, RegistryTracker.Modifiable arg, LevelStorage.Session arg2, SaveProperties arg3, ResourcePackManager arg4, Proxy proxy, DataFixer dataFixer, ServerResourceManager arg5, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache arg6, WorldGenerationProgressListenerFactory arg7) {
        super("Server");
        this.dimensionTracker = arg;
        this.saveProperties = arg3;
        this.proxy = proxy;
        this.dataPackManager = arg4;
        this.serverResourceManager = arg5;
        this.sessionService = minecraftSessionService;
        this.gameProfileRepo = gameProfileRepository;
        this.userCache = arg6;
        this.networkIo = new ServerNetworkIo(this);
        this.worldGenerationProgressListenerFactory = arg7;
        this.session = arg2;
        this.field_24371 = arg2.method_27427();
        this.dataFixer = dataFixer;
        this.commandFunctionManager = new CommandFunctionManager(this, arg5.getFunctionLoader());
        this.structureManager = new StructureManager(arg5.getResourceManager(), arg2, dataFixer);
        this.serverThread = thread;
        this.workerExecutor = Util.getServerWorkerExecutor();
    }

    private void initScoreboard(PersistentStateManager arg) {
        ScoreboardState lv = arg.getOrCreate(ScoreboardState::new, "scoreboard");
        lv.setScoreboard(this.getScoreboard());
        this.getScoreboard().addUpdateListener(new ScoreboardSynchronizer(lv));
    }

    protected abstract boolean setupServer() throws IOException;

    public static void convertLevel(LevelStorage.Session arg) {
        if (arg.needsConversion()) {
            LOGGER.info("Converting map!");
            arg.convert(new ProgressListener(){
                private long lastProgressUpdate = Util.getMeasuringTimeMs();

                @Override
                public void method_15412(Text arg) {
                }

                @Override
                @Environment(value=EnvType.CLIENT)
                public void method_15413(Text arg) {
                }

                @Override
                public void progressStagePercentage(int i) {
                    if (Util.getMeasuringTimeMs() - this.lastProgressUpdate >= 1000L) {
                        this.lastProgressUpdate = Util.getMeasuringTimeMs();
                        LOGGER.info("Converting... {}%", (Object)i);
                    }
                }

                @Override
                @Environment(value=EnvType.CLIENT)
                public void setDone() {
                }

                @Override
                public void method_15414(Text arg) {
                }
            });
        }
    }

    protected void loadWorld() {
        this.loadWorldResourcePack();
        this.saveProperties.addServerBrand(this.getServerModName(), this.getModdedStatusMessage().isPresent());
        WorldGenerationProgressListener lv = this.worldGenerationProgressListenerFactory.create(11);
        this.createWorlds(lv);
        this.method_27731();
        this.prepareStartRegion(lv);
    }

    protected void method_27731() {
    }

    protected void createWorlds(WorldGenerationProgressListener arg) {
        ChunkGenerator lv8;
        DimensionType lv7;
        ServerWorldProperties lv = this.saveProperties.getMainWorldProperties();
        GeneratorOptions lv2 = this.saveProperties.getGeneratorOptions();
        boolean bl = lv2.isDebugWorld();
        long l = lv2.getSeed();
        long m = BiomeAccess.hashSeed(l);
        ImmutableList list = ImmutableList.of((Object)new PhantomSpawner(), (Object)new PillagerSpawner(), (Object)new CatSpawner(), (Object)new ZombieSiegeManager(), (Object)new WanderingTraderManager(lv));
        SimpleRegistry<DimensionOptions> lv3 = lv2.getDimensionMap();
        DimensionOptions lv4 = lv3.get(DimensionOptions.OVERWORLD);
        if (lv4 == null) {
            DimensionType lv5 = DimensionType.getOverworldDimensionType();
            SurfaceChunkGenerator lv6 = GeneratorOptions.createOverworldGenerator(new Random().nextLong());
        } else {
            lv7 = lv4.getDimensionType();
            lv8 = lv4.getChunkGenerator();
        }
        RegistryKey<DimensionType> lv9 = this.dimensionTracker.getDimensionTypeRegistry().getKey(lv7).orElseThrow(() -> new IllegalStateException("Unregistered dimension type: " + lv7));
        ServerWorld lv10 = new ServerWorld(this, this.workerExecutor, this.session, lv, World.OVERWORLD, lv9, lv7, arg, lv8, bl, m, (List<Spawner>)list, true);
        this.worlds.put(World.OVERWORLD, lv10);
        PersistentStateManager lv11 = lv10.getPersistentStateManager();
        this.initScoreboard(lv11);
        this.dataCommandStorage = new DataCommandStorage(lv11);
        WorldBorder lv12 = lv10.getWorldBorder();
        lv12.load(lv.getWorldBorder());
        if (!lv.isInitialized()) {
            try {
                MinecraftServer.setupSpawn(lv10, lv, lv2.hasBonusChest(), bl, true);
                lv.setInitialized(true);
                if (bl) {
                    this.setToDebugWorldProperties(this.saveProperties);
                }
            }
            catch (Throwable throwable) {
                CrashReport lv13 = CrashReport.create(throwable, "Exception initializing level");
                try {
                    lv10.addDetailsToCrashReport(lv13);
                }
                catch (Throwable throwable2) {
                    // empty catch block
                }
                throw new CrashException(lv13);
            }
            lv.setInitialized(true);
        }
        this.getPlayerManager().setMainWorld(lv10);
        if (this.saveProperties.getCustomBossEvents() != null) {
            this.getBossBarManager().fromTag(this.saveProperties.getCustomBossEvents());
        }
        for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : lv3.getEntries()) {
            RegistryKey<DimensionOptions> lv14 = entry.getKey();
            if (lv14 == DimensionOptions.OVERWORLD) continue;
            RegistryKey<World> lv15 = RegistryKey.of(Registry.DIMENSION, lv14.getValue());
            DimensionType lv16 = entry.getValue().getDimensionType();
            RegistryKey<DimensionType> lv17 = this.dimensionTracker.getDimensionTypeRegistry().getKey(lv16).orElseThrow(() -> new IllegalStateException("Unregistered dimension type: " + lv16));
            ChunkGenerator lv18 = entry.getValue().getChunkGenerator();
            UnmodifiableLevelProperties lv19 = new UnmodifiableLevelProperties(this.saveProperties, lv);
            ServerWorld lv20 = new ServerWorld(this, this.workerExecutor, this.session, lv19, lv15, lv17, lv16, arg, lv18, bl, m, (List<Spawner>)ImmutableList.of(), false);
            lv12.addListener(new WorldBorderListener.WorldBorderSyncer(lv20.getWorldBorder()));
            this.worlds.put(lv15, lv20);
        }
    }

    private static void setupSpawn(ServerWorld arg, ServerWorldProperties arg2, boolean bl, boolean bl2, boolean bl3) {
        ChunkPos lv4;
        ChunkGenerator lv = arg.getChunkManager().getChunkGenerator();
        if (!bl3) {
            arg2.setSpawnPos(BlockPos.ORIGIN.up(lv.getSpawnHeight()));
            return;
        }
        if (bl2) {
            arg2.setSpawnPos(BlockPos.ORIGIN.up());
            return;
        }
        BiomeSource lv2 = lv.getBiomeSource();
        List<Biome> list = lv2.getSpawnBiomes();
        Random random = new Random(arg.getSeed());
        BlockPos lv3 = lv2.locateBiome(0, arg.getSeaLevel(), 0, 256, list, random);
        ChunkPos chunkPos = lv4 = lv3 == null ? new ChunkPos(0, 0) : new ChunkPos(lv3);
        if (lv3 == null) {
            LOGGER.warn("Unable to find spawn biome");
        }
        boolean bl4 = false;
        for (Block lv5 : BlockTags.VALID_SPAWN.values()) {
            if (!lv2.getTopMaterials().contains(lv5.getDefaultState())) continue;
            bl4 = true;
            break;
        }
        arg2.setSpawnPos(lv4.getCenterBlockPos().add(8, lv.getSpawnHeight(), 8));
        int i = 0;
        int j = 0;
        int k = 0;
        int l = -1;
        int m = 32;
        for (int n = 0; n < 1024; ++n) {
            BlockPos lv6;
            if (i > -16 && i <= 16 && j > -16 && j <= 16 && (lv6 = SpawnLocating.findServerSpawnPoint(arg, new ChunkPos(lv4.x + i, lv4.z + j), bl4)) != null) {
                arg2.setSpawnPos(lv6);
                break;
            }
            if (i == j || i < 0 && i == -j || i > 0 && i == 1 - j) {
                int o = k;
                k = -l;
                l = o;
            }
            i += k;
            j += l;
        }
        if (bl) {
            ConfiguredFeature<DefaultFeatureConfig, ?> lv7 = Feature.BONUS_CHEST.configure(FeatureConfig.DEFAULT);
            lv7.generate(arg, lv, arg.random, new BlockPos(arg2.getSpawnX(), arg2.getSpawnY(), arg2.getSpawnZ()));
        }
    }

    private void setToDebugWorldProperties(SaveProperties arg) {
        arg.setDifficulty(Difficulty.PEACEFUL);
        arg.setDifficultyLocked(true);
        ServerWorldProperties lv = arg.getMainWorldProperties();
        lv.setRaining(false);
        lv.setThundering(false);
        lv.setClearWeatherTime(1000000000);
        lv.method_29035(6000L);
        lv.setGameMode(GameMode.SPECTATOR);
    }

    private void prepareStartRegion(WorldGenerationProgressListener arg) {
        ServerWorld lv = this.getOverworld();
        LOGGER.info("Preparing start region for dimension {}", (Object)lv.getRegistryKey().getValue());
        BlockPos lv2 = lv.getSpawnPos();
        arg.start(new ChunkPos(lv2));
        ServerChunkManager lv3 = lv.getChunkManager();
        lv3.getLightingProvider().setTaskBatchSize(500);
        this.timeReference = Util.getMeasuringTimeMs();
        lv3.addTicket(ChunkTicketType.START, new ChunkPos(lv2), 11, Unit.INSTANCE);
        while (lv3.getTotalChunksLoadedCount() != 441) {
            this.timeReference = Util.getMeasuringTimeMs() + 10L;
            this.method_16208();
        }
        this.timeReference = Util.getMeasuringTimeMs() + 10L;
        this.method_16208();
        for (ServerWorld lv4 : this.worlds.values()) {
            ForcedChunkState lv5 = lv4.getPersistentStateManager().get(ForcedChunkState::new, "chunks");
            if (lv5 == null) continue;
            LongIterator longIterator = lv5.getChunks().iterator();
            while (longIterator.hasNext()) {
                long l = longIterator.nextLong();
                ChunkPos lv6 = new ChunkPos(l);
                lv4.getChunkManager().setChunkForced(lv6, true);
            }
        }
        this.timeReference = Util.getMeasuringTimeMs() + 10L;
        this.method_16208();
        arg.stop();
        lv3.getLightingProvider().setTaskBatchSize(5);
        this.updateMobSpawnOptions();
    }

    protected void loadWorldResourcePack() {
        File file = this.session.getDirectory(WorldSavePath.RESOURCES_ZIP).toFile();
        if (file.isFile()) {
            String string = this.session.getDirectoryName();
            try {
                this.setResourcePack("level://" + URLEncoder.encode(string, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                LOGGER.warn("Something went wrong url encoding {}", (Object)string);
            }
        }
    }

    public GameMode getDefaultGameMode() {
        return this.saveProperties.getGameMode();
    }

    public boolean isHardcore() {
        return this.saveProperties.isHardcore();
    }

    public abstract int getOpPermissionLevel();

    public abstract int getFunctionPermissionLevel();

    public abstract boolean shouldBroadcastRconToOps();

    public boolean save(boolean bl, boolean bl2, boolean bl3) {
        boolean bl4 = false;
        for (ServerWorld lv : this.getWorlds()) {
            if (!bl) {
                LOGGER.info("Saving chunks for level '{}'/{}", (Object)lv, (Object)lv.getRegistryKey().getValue());
            }
            lv.save(null, bl2, lv.savingDisabled && !bl3);
            bl4 = true;
        }
        ServerWorld lv2 = this.getOverworld();
        ServerWorldProperties lv3 = this.saveProperties.getMainWorldProperties();
        lv3.setWorldBorder(lv2.getWorldBorder().write());
        this.saveProperties.setCustomBossEvents(this.getBossBarManager().toTag());
        this.session.method_27426(this.dimensionTracker, this.saveProperties, this.getPlayerManager().getUserData());
        return bl4;
    }

    @Override
    public void close() {
        this.shutdown();
    }

    protected void shutdown() {
        LOGGER.info("Stopping server");
        if (this.getNetworkIo() != null) {
            this.getNetworkIo().stop();
        }
        if (this.playerManager != null) {
            LOGGER.info("Saving players");
            this.playerManager.saveAllPlayerData();
            this.playerManager.disconnectAllPlayers();
        }
        LOGGER.info("Saving worlds");
        for (ServerWorld lv : this.getWorlds()) {
            if (lv == null) continue;
            lv.savingDisabled = false;
        }
        this.save(false, true, false);
        for (ServerWorld lv2 : this.getWorlds()) {
            if (lv2 == null) continue;
            try {
                lv2.close();
            }
            catch (IOException iOException) {
                LOGGER.error("Exception closing the level", (Throwable)iOException);
            }
        }
        if (this.snooper.isActive()) {
            this.snooper.cancel();
        }
        this.serverResourceManager.close();
        try {
            this.session.close();
        }
        catch (IOException iOException2) {
            LOGGER.error("Failed to unlock level {}", (Object)this.session.getDirectoryName(), (Object)iOException2);
        }
    }

    public String getServerIp() {
        return this.serverIp;
    }

    public void setServerIp(String string) {
        this.serverIp = string;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void stop(boolean bl) {
        this.running = false;
        if (bl) {
            try {
                this.serverThread.join();
            }
            catch (InterruptedException interruptedException) {
                LOGGER.error("Error while shutting down", (Throwable)interruptedException);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void runServer() {
        try {
            if (this.setupServer()) {
                this.timeReference = Util.getMeasuringTimeMs();
                this.metadata.setDescription(new LiteralText(this.motd));
                this.metadata.setVersion(new ServerMetadata.Version(SharedConstants.getGameVersion().getName(), SharedConstants.getGameVersion().getProtocolVersion()));
                this.setFavicon(this.metadata);
                while (this.running) {
                    long l = Util.getMeasuringTimeMs() - this.timeReference;
                    if (l > 2000L && this.timeReference - this.lastTimeReference >= 15000L) {
                        long m = l / 50L;
                        LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", (Object)l, (Object)m);
                        this.timeReference += m * 50L;
                        this.lastTimeReference = this.timeReference;
                    }
                    this.timeReference += 50L;
                    TickDurationMonitor lv = TickDurationMonitor.create("Server");
                    this.startMonitor(lv);
                    this.profiler.startTick();
                    this.profiler.push("tick");
                    this.tick(this::shouldKeepTicking);
                    this.profiler.swap("nextTickWait");
                    this.waitingForNextTick = true;
                    this.field_19248 = Math.max(Util.getMeasuringTimeMs() + 50L, this.timeReference);
                    this.method_16208();
                    this.profiler.pop();
                    this.profiler.endTick();
                    this.endMonitor(lv);
                    this.loading = true;
                }
            } else {
                this.setCrashReport(null);
            }
        }
        catch (Throwable throwable2) {
            CrashReport lv3;
            LOGGER.error("Encountered an unexpected exception", throwable2);
            if (throwable2 instanceof CrashException) {
                CrashReport lv2 = this.populateCrashReport(((CrashException)throwable2).getReport());
            } else {
                lv3 = this.populateCrashReport(new CrashReport("Exception in server tick loop", throwable2));
            }
            File file = new File(new File(this.getRunDirectory(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt");
            if (lv3.writeToFile(file)) {
                LOGGER.error("This crash report has been saved to: {}", (Object)file.getAbsolutePath());
            } else {
                LOGGER.error("We were unable to save this crash report to disk.");
            }
            this.setCrashReport(lv3);
        }
        finally {
            try {
                this.stopped = true;
                this.shutdown();
            }
            catch (Throwable throwable) {
                LOGGER.error("Exception stopping the server", throwable);
            }
            finally {
                this.exit();
            }
        }
    }

    private boolean shouldKeepTicking() {
        return this.hasRunningTasks() || Util.getMeasuringTimeMs() < (this.waitingForNextTick ? this.field_19248 : this.timeReference);
    }

    protected void method_16208() {
        this.runTasks();
        this.runTasks(() -> !this.shouldKeepTicking());
    }

    @Override
    protected ServerTask createTask(Runnable runnable) {
        return new ServerTask(this.ticks, runnable);
    }

    @Override
    protected boolean canExecute(ServerTask arg) {
        return arg.getCreationTicks() + 3 < this.ticks || this.shouldKeepTicking();
    }

    @Override
    public boolean runTask() {
        boolean bl;
        this.waitingForNextTick = bl = this.method_20415();
        return bl;
    }

    private boolean method_20415() {
        if (super.runTask()) {
            return true;
        }
        if (this.shouldKeepTicking()) {
            for (ServerWorld lv : this.getWorlds()) {
                if (!lv.getChunkManager().executeQueuedTasks()) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    protected void executeTask(ServerTask arg) {
        this.getProfiler().visit("runTask");
        super.executeTask(arg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setFavicon(ServerMetadata arg) {
        File file = this.getFile("server-icon.png");
        if (!file.exists()) {
            file = this.session.getIconFile();
        }
        if (file.isFile()) {
            ByteBuf byteBuf = Unpooled.buffer();
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Validate.validState((bufferedImage.getWidth() == 64 ? 1 : 0) != 0, (String)"Must be 64 pixels wide", (Object[])new Object[0]);
                Validate.validState((bufferedImage.getHeight() == 64 ? 1 : 0) != 0, (String)"Must be 64 pixels high", (Object[])new Object[0]);
                ImageIO.write((RenderedImage)bufferedImage, "PNG", (OutputStream)new ByteBufOutputStream(byteBuf));
                ByteBuffer byteBuffer = Base64.getEncoder().encode(byteBuf.nioBuffer());
                arg.setFavicon("data:image/png;base64," + StandardCharsets.UTF_8.decode(byteBuffer));
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't load server icon", (Throwable)exception);
            }
            finally {
                byteBuf.release();
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasIconFile() {
        this.iconFilePresent = this.iconFilePresent || this.getIconFile().isFile();
        return this.iconFilePresent;
    }

    @Environment(value=EnvType.CLIENT)
    public File getIconFile() {
        return this.session.getIconFile();
    }

    public File getRunDirectory() {
        return new File(".");
    }

    protected void setCrashReport(CrashReport arg) {
    }

    protected void exit() {
    }

    protected void tick(BooleanSupplier booleanSupplier) {
        long l = Util.getMeasuringTimeNano();
        ++this.ticks;
        this.tickWorlds(booleanSupplier);
        if (l - this.lastPlayerSampleUpdate >= 5000000000L) {
            this.lastPlayerSampleUpdate = l;
            this.metadata.setPlayers(new ServerMetadata.Players(this.getMaxPlayerCount(), this.getCurrentPlayerCount()));
            GameProfile[] gameProfiles = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
            int i = MathHelper.nextInt(this.random, 0, this.getCurrentPlayerCount() - gameProfiles.length);
            for (int j = 0; j < gameProfiles.length; ++j) {
                gameProfiles[j] = this.playerManager.getPlayerList().get(i + j).getGameProfile();
            }
            Collections.shuffle(Arrays.asList(gameProfiles));
            this.metadata.getPlayers().setSample(gameProfiles);
        }
        if (this.ticks % 6000 == 0) {
            LOGGER.debug("Autosave started");
            this.profiler.push("save");
            this.playerManager.saveAllPlayerData();
            this.save(true, false, false);
            this.profiler.pop();
            LOGGER.debug("Autosave finished");
        }
        this.profiler.push("snooper");
        if (!this.snooper.isActive() && this.ticks > 100) {
            this.snooper.method_5482();
        }
        if (this.ticks % 6000 == 0) {
            this.snooper.update();
        }
        this.profiler.pop();
        this.profiler.push("tallying");
        long l2 = Util.getMeasuringTimeNano() - l;
        this.lastTickLengths[this.ticks % 100] = l2;
        long m = l2;
        this.tickTime = this.tickTime * 0.8f + (float)m / 1000000.0f * 0.19999999f;
        long n = Util.getMeasuringTimeNano();
        this.metricsData.pushSample(n - l);
        this.profiler.pop();
    }

    protected void tickWorlds(BooleanSupplier booleanSupplier) {
        this.profiler.push("commandFunctions");
        this.getCommandFunctionManager().tick();
        this.profiler.swap("levels");
        for (ServerWorld lv : this.getWorlds()) {
            this.profiler.push(() -> lv + " " + lv.getRegistryKey().getValue());
            if (this.ticks % 20 == 0) {
                this.profiler.push("timeSync");
                this.playerManager.sendToDimension(new WorldTimeUpdateS2CPacket(lv.getTime(), lv.getTimeOfDay(), lv.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)), lv.getRegistryKey());
                this.profiler.pop();
            }
            this.profiler.push("tick");
            try {
                lv.tick(booleanSupplier);
            }
            catch (Throwable throwable) {
                CrashReport lv2 = CrashReport.create(throwable, "Exception ticking world");
                lv.addDetailsToCrashReport(lv2);
                throw new CrashException(lv2);
            }
            this.profiler.pop();
            this.profiler.pop();
        }
        this.profiler.swap("connection");
        this.getNetworkIo().tick();
        this.profiler.swap("players");
        this.playerManager.updatePlayerLatency();
        if (SharedConstants.isDevelopment) {
            TestManager.INSTANCE.tick();
        }
        this.profiler.swap("server gui refresh");
        for (int i = 0; i < this.serverGuiTickables.size(); ++i) {
            this.serverGuiTickables.get(i).run();
        }
        this.profiler.pop();
    }

    public boolean isNetherAllowed() {
        return true;
    }

    public void addServerGuiTickable(Runnable runnable) {
        this.serverGuiTickables.add(runnable);
    }

    protected void setServerId(String string) {
        this.serverId = string;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isStopping() {
        return !this.serverThread.isAlive();
    }

    public File getFile(String string) {
        return new File(this.getRunDirectory(), string);
    }

    public final ServerWorld getOverworld() {
        return this.worlds.get(World.OVERWORLD);
    }

    @Nullable
    public ServerWorld getWorld(RegistryKey<World> arg) {
        return this.worlds.get(arg);
    }

    public Set<RegistryKey<World>> getWorldRegistryKeys() {
        return this.worlds.keySet();
    }

    public Iterable<ServerWorld> getWorlds() {
        return this.worlds.values();
    }

    public String getVersion() {
        return SharedConstants.getGameVersion().getName();
    }

    public int getCurrentPlayerCount() {
        return this.playerManager.getCurrentPlayerCount();
    }

    public int getMaxPlayerCount() {
        return this.playerManager.getMaxPlayerCount();
    }

    public String[] getPlayerNames() {
        return this.playerManager.getPlayerNames();
    }

    public String getServerModName() {
        return "vanilla";
    }

    public CrashReport populateCrashReport(CrashReport arg) {
        if (this.playerManager != null) {
            arg.getSystemDetailsSection().add("Player Count", () -> this.playerManager.getCurrentPlayerCount() + " / " + this.playerManager.getMaxPlayerCount() + "; " + this.playerManager.getPlayerList());
        }
        arg.getSystemDetailsSection().add("Data Packs", () -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (ResourcePackProfile lv : this.dataPackManager.getEnabledProfiles()) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(lv.getName());
                if (lv.getCompatibility().isCompatible()) continue;
                stringBuilder.append(" (incompatible)");
            }
            return stringBuilder.toString();
        });
        if (this.serverId != null) {
            arg.getSystemDetailsSection().add("Server Id", () -> this.serverId);
        }
        return arg;
    }

    public abstract Optional<String> getModdedStatusMessage();

    @Override
    public void sendSystemMessage(Text arg, UUID uUID) {
        LOGGER.info(arg.getString());
    }

    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(int i) {
        this.serverPort = i;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setServerName(String string) {
        this.userName = string;
    }

    public boolean isSinglePlayer() {
        return this.userName != null;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public void setDifficulty(Difficulty arg, boolean bl) {
        if (!bl && this.saveProperties.isDifficultyLocked()) {
            return;
        }
        this.saveProperties.setDifficulty(this.saveProperties.isHardcore() ? Difficulty.HARD : arg);
        this.updateMobSpawnOptions();
        this.getPlayerManager().getPlayerList().forEach(this::sendDifficulty);
    }

    public int adjustTrackingDistance(int i) {
        return i;
    }

    private void updateMobSpawnOptions() {
        for (ServerWorld lv : this.getWorlds()) {
            lv.setMobSpawnOptions(this.isMonsterSpawningEnabled(), this.shouldSpawnAnimals());
        }
    }

    public void setDifficultyLocked(boolean bl) {
        this.saveProperties.setDifficultyLocked(bl);
        this.getPlayerManager().getPlayerList().forEach(this::sendDifficulty);
    }

    private void sendDifficulty(ServerPlayerEntity arg) {
        WorldProperties lv = arg.getServerWorld().getLevelProperties();
        arg.networkHandler.sendPacket(new DifficultyS2CPacket(lv.getDifficulty(), lv.isDifficultyLocked()));
    }

    protected boolean isMonsterSpawningEnabled() {
        return this.saveProperties.getDifficulty() != Difficulty.PEACEFUL;
    }

    public boolean isDemo() {
        return this.demo;
    }

    public void setDemo(boolean bl) {
        this.demo = bl;
    }

    public String getResourcePackUrl() {
        return this.resourcePackUrl;
    }

    public String getResourcePackHash() {
        return this.resourcePackHash;
    }

    public void setResourcePack(String string, String string2) {
        this.resourcePackUrl = string;
        this.resourcePackHash = string2;
    }

    @Override
    public void addSnooperInfo(Snooper arg) {
        arg.addInfo("whitelist_enabled", false);
        arg.addInfo("whitelist_count", 0);
        if (this.playerManager != null) {
            arg.addInfo("players_current", this.getCurrentPlayerCount());
            arg.addInfo("players_max", this.getMaxPlayerCount());
            arg.addInfo("players_seen", this.field_24371.getSavedPlayerIds().length);
        }
        arg.addInfo("uses_auth", this.onlineMode);
        arg.addInfo("gui_state", this.hasGui() ? "enabled" : "disabled");
        arg.addInfo("run_time", (Util.getMeasuringTimeMs() - arg.getStartTime()) / 60L * 1000L);
        arg.addInfo("avg_tick_ms", (int)(MathHelper.average(this.lastTickLengths) * 1.0E-6));
        int i = 0;
        for (ServerWorld lv : this.getWorlds()) {
            if (lv == null) continue;
            arg.addInfo("world[" + i + "][dimension]", lv.getRegistryKey().getValue());
            arg.addInfo("world[" + i + "][mode]", (Object)this.saveProperties.getGameMode());
            arg.addInfo("world[" + i + "][difficulty]", (Object)lv.getDifficulty());
            arg.addInfo("world[" + i + "][hardcore]", this.saveProperties.isHardcore());
            arg.addInfo("world[" + i + "][height]", this.worldHeight);
            arg.addInfo("world[" + i + "][chunks_loaded]", lv.getChunkManager().getLoadedChunkCount());
            ++i;
        }
        arg.addInfo("worlds", i);
    }

    public abstract boolean isDedicated();

    public boolean isOnlineMode() {
        return this.onlineMode;
    }

    public void setOnlineMode(boolean bl) {
        this.onlineMode = bl;
    }

    public boolean shouldPreventProxyConnections() {
        return this.preventProxyConnections;
    }

    public void setPreventProxyConnections(boolean bl) {
        this.preventProxyConnections = bl;
    }

    public boolean shouldSpawnAnimals() {
        return true;
    }

    public boolean shouldSpawnNpcs() {
        return true;
    }

    public abstract boolean isUsingNativeTransport();

    public boolean isPvpEnabled() {
        return this.pvpEnabled;
    }

    public void setPvpEnabled(boolean bl) {
        this.pvpEnabled = bl;
    }

    public boolean isFlightEnabled() {
        return this.flightEnabled;
    }

    public void setFlightEnabled(boolean bl) {
        this.flightEnabled = bl;
    }

    public abstract boolean areCommandBlocksEnabled();

    public String getServerMotd() {
        return this.motd;
    }

    public void setMotd(String string) {
        this.motd = string;
    }

    public int getWorldHeight() {
        return this.worldHeight;
    }

    public void setWorldHeight(int i) {
        this.worldHeight = i;
    }

    public boolean isStopped() {
        return this.stopped;
    }

    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public void setPlayerManager(PlayerManager arg) {
        this.playerManager = arg;
    }

    public abstract boolean isRemote();

    public void setDefaultGameMode(GameMode arg) {
        this.saveProperties.setGameMode(arg);
    }

    @Nullable
    public ServerNetworkIo getNetworkIo() {
        return this.networkIo;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isLoading() {
        return this.loading;
    }

    public boolean hasGui() {
        return false;
    }

    public abstract boolean openToLan(GameMode var1, boolean var2, int var3);

    public int getTicks() {
        return this.ticks;
    }

    @Environment(value=EnvType.CLIENT)
    public Snooper getSnooper() {
        return this.snooper;
    }

    public int getSpawnProtectionRadius() {
        return 16;
    }

    public boolean isSpawnProtected(ServerWorld arg, BlockPos arg2, PlayerEntity arg3) {
        return false;
    }

    public void setForceGameMode(boolean bl) {
        this.forceGameMode = bl;
    }

    public boolean shouldForceGameMode() {
        return this.forceGameMode;
    }

    public boolean acceptsStatusQuery() {
        return true;
    }

    public int getPlayerIdleTimeout() {
        return this.playerIdleTimeout;
    }

    public void setPlayerIdleTimeout(int i) {
        this.playerIdleTimeout = i;
    }

    public MinecraftSessionService getSessionService() {
        return this.sessionService;
    }

    public GameProfileRepository getGameProfileRepo() {
        return this.gameProfileRepo;
    }

    public UserCache getUserCache() {
        return this.userCache;
    }

    public ServerMetadata getServerMetadata() {
        return this.metadata;
    }

    public void forcePlayerSampleUpdate() {
        this.lastPlayerSampleUpdate = 0L;
    }

    public int getMaxWorldBorderRadius() {
        return 29999984;
    }

    @Override
    public boolean shouldExecuteAsync() {
        return super.shouldExecuteAsync() && !this.isStopped();
    }

    @Override
    public Thread getThread() {
        return this.serverThread;
    }

    public int getNetworkCompressionThreshold() {
        return 256;
    }

    public long getServerStartTime() {
        return this.timeReference;
    }

    public DataFixer getDataFixer() {
        return this.dataFixer;
    }

    public int getSpawnRadius(@Nullable ServerWorld arg) {
        if (arg != null) {
            return arg.getGameRules().getInt(GameRules.SPAWN_RADIUS);
        }
        return 10;
    }

    public ServerAdvancementLoader getAdvancementLoader() {
        return this.serverResourceManager.getServerAdvancementLoader();
    }

    public CommandFunctionManager getCommandFunctionManager() {
        return this.commandFunctionManager;
    }

    public CompletableFuture<Void> reloadResources(Collection<String> collection) {
        CompletionStage completableFuture = ((CompletableFuture)CompletableFuture.supplyAsync(() -> (ImmutableList)collection.stream().map(this.dataPackManager::getProfile).filter(Objects::nonNull).map(ResourcePackProfile::createResourcePack).collect(ImmutableList.toImmutableList()), this).thenCompose(immutableList -> ServerResourceManager.reload((List<ResourcePack>)immutableList, this.isDedicated() ? CommandManager.RegistrationEnvironment.DEDICATED : CommandManager.RegistrationEnvironment.INTEGRATED, this.getFunctionPermissionLevel(), this.workerExecutor, this))).thenAcceptAsync(arg -> {
            this.serverResourceManager.close();
            this.serverResourceManager = arg;
            this.dataPackManager.setEnabledProfiles(collection);
            this.saveProperties.method_29590(MinecraftServer.method_29735(this.dataPackManager));
            arg.loadRegistryTags();
            this.getPlayerManager().saveAllPlayerData();
            this.getPlayerManager().onDataPacksReloaded();
            this.commandFunctionManager.method_29461(this.serverResourceManager.getFunctionLoader());
            this.structureManager.method_29300(this.serverResourceManager.getResourceManager());
        }, (Executor)this);
        if (this.isOnThread()) {
            this.runTasks(((CompletableFuture)completableFuture)::isDone);
        }
        return completableFuture;
    }

    public static DataPackSettings loadDataPacks(ResourcePackManager arg, DataPackSettings arg2, boolean bl) {
        arg.scanPacks();
        if (bl) {
            arg.setEnabledProfiles(Collections.singleton("vanilla"));
            return new DataPackSettings((List<String>)ImmutableList.of((Object)"vanilla"), (List<String>)ImmutableList.of());
        }
        LinkedHashSet set = Sets.newLinkedHashSet();
        for (String string : arg2.getEnabled()) {
            if (arg.hasProfile(string)) {
                set.add(string);
                continue;
            }
            LOGGER.warn("Missing data pack {}", (Object)string);
        }
        for (ResourcePackProfile lv : arg.getProfiles()) {
            String string2 = lv.getName();
            if (arg2.getDisabled().contains(string2) || set.contains(string2)) continue;
            LOGGER.info("Found new data pack {}, loading it automatically", (Object)string2);
            set.add(string2);
        }
        if (set.isEmpty()) {
            LOGGER.info("No datapacks selected, forcing vanilla");
            set.add("vanilla");
        }
        arg.setEnabledProfiles(set);
        return MinecraftServer.method_29735(arg);
    }

    private static DataPackSettings method_29735(ResourcePackManager arg) {
        Collection<String> collection = arg.getEnabledNames();
        ImmutableList list = ImmutableList.copyOf(collection);
        List list2 = (List)arg.getNames().stream().filter(string -> !collection.contains(string)).collect(ImmutableList.toImmutableList());
        return new DataPackSettings((List<String>)list, list2);
    }

    public void kickNonWhitelistedPlayers(ServerCommandSource arg) {
        if (!this.isEnforceWhitelist()) {
            return;
        }
        PlayerManager lv = arg.getMinecraftServer().getPlayerManager();
        Whitelist lv2 = lv.getWhitelist();
        ArrayList list = Lists.newArrayList(lv.getPlayerList());
        for (ServerPlayerEntity lv3 : list) {
            if (lv2.isAllowed(lv3.getGameProfile())) continue;
            lv3.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.not_whitelisted"));
        }
    }

    public ResourcePackManager getDataPackManager() {
        return this.dataPackManager;
    }

    public CommandManager getCommandManager() {
        return this.serverResourceManager.getCommandManager();
    }

    public ServerCommandSource getCommandSource() {
        ServerWorld lv = this.getOverworld();
        return new ServerCommandSource(this, lv == null ? Vec3d.ZERO : Vec3d.of(lv.getSpawnPos()), Vec2f.ZERO, lv, 4, "Server", new LiteralText("Server"), this, null);
    }

    @Override
    public boolean shouldReceiveFeedback() {
        return true;
    }

    @Override
    public boolean shouldTrackOutput() {
        return true;
    }

    public RecipeManager getRecipeManager() {
        return this.serverResourceManager.getRecipeManager();
    }

    public TagManager getTagManager() {
        return this.serverResourceManager.getRegistryTagManager();
    }

    public ServerScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public DataCommandStorage getDataCommandStorage() {
        if (this.dataCommandStorage == null) {
            throw new NullPointerException("Called before server init");
        }
        return this.dataCommandStorage;
    }

    public LootManager getLootManager() {
        return this.serverResourceManager.getLootManager();
    }

    public LootConditionManager getPredicateManager() {
        return this.serverResourceManager.getLootConditionManager();
    }

    public GameRules getGameRules() {
        return this.getOverworld().getGameRules();
    }

    public BossBarManager getBossBarManager() {
        return this.bossBarManager;
    }

    public boolean isEnforceWhitelist() {
        return this.enforceWhitelist;
    }

    public void setEnforceWhitelist(boolean bl) {
        this.enforceWhitelist = bl;
    }

    public float getTickTime() {
        return this.tickTime;
    }

    public int getPermissionLevel(GameProfile gameProfile) {
        if (this.getPlayerManager().isOperator(gameProfile)) {
            OperatorEntry lv = (OperatorEntry)this.getPlayerManager().getOpList().get(gameProfile);
            if (lv != null) {
                return lv.getPermissionLevel();
            }
            if (this.isHost(gameProfile)) {
                return 4;
            }
            if (this.isSinglePlayer()) {
                return this.getPlayerManager().areCheatsAllowed() ? 4 : 0;
            }
            return this.getOpPermissionLevel();
        }
        return 0;
    }

    @Environment(value=EnvType.CLIENT)
    public MetricsData getMetricsData() {
        return this.metricsData;
    }

    public Profiler getProfiler() {
        return this.profiler;
    }

    public abstract boolean isHost(GameProfile var1);

    public void dump(Path path) throws IOException {
        Path path2 = path.resolve("levels");
        for (Map.Entry<RegistryKey<World>, ServerWorld> entry : this.worlds.entrySet()) {
            Identifier lv = entry.getKey().getValue();
            Path path3 = path2.resolve(lv.getNamespace()).resolve(lv.getPath());
            Files.createDirectories(path3, new FileAttribute[0]);
            entry.getValue().dump(path3);
        }
        this.dumpGamerules(path.resolve("gamerules.txt"));
        this.dumpClasspath(path.resolve("classpath.txt"));
        this.dumpExampleCrash(path.resolve("example_crash.txt"));
        this.dumpStats(path.resolve("stats.txt"));
        this.dumpThreads(path.resolve("threads.txt"));
    }

    private void dumpStats(Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
            writer.write(String.format("pending_tasks: %d\n", this.getTaskCount()));
            writer.write(String.format("average_tick_time: %f\n", Float.valueOf(this.getTickTime())));
            writer.write(String.format("tick_times: %s\n", Arrays.toString(this.lastTickLengths)));
            writer.write(String.format("queue: %s\n", Util.getServerWorkerExecutor()));
        }
    }

    private void dumpExampleCrash(Path path) throws IOException {
        CrashReport lv = new CrashReport("Server dump", new Exception("dummy"));
        this.populateCrashReport(lv);
        try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
            writer.write(lv.asString());
        }
    }

    private void dumpGamerules(Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
            final ArrayList list = Lists.newArrayList();
            final GameRules lv = this.getGameRules();
            GameRules.forEachType(new GameRules.TypeConsumer(){

                @Override
                public <T extends GameRules.Rule<T>> void accept(GameRules.Key<T> arg, GameRules.Type<T> arg2) {
                    list.add(String.format("%s=%s\n", arg.getName(), ((GameRules.Rule)lv.get(arg)).toString()));
                }
            });
            for (String string : list) {
                writer.write(string);
            }
        }
    }

    private void dumpClasspath(Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
            String string = System.getProperty("java.class.path");
            String string2 = System.getProperty("path.separator");
            for (String string3 : Splitter.on((String)string2).split((CharSequence)string)) {
                writer.write(string3);
                writer.write("\n");
            }
        }
    }

    private void dumpThreads(Path path) throws IOException {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
        Arrays.sort(threadInfos, Comparator.comparing(ThreadInfo::getThreadName));
        try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
            for (ThreadInfo threadInfo : threadInfos) {
                writer.write(threadInfo.toString());
                ((Writer)writer).write(10);
            }
        }
    }

    private void startMonitor(@Nullable TickDurationMonitor arg) {
        if (this.profilerStartQueued) {
            this.profilerStartQueued = false;
            this.tickTimeTracker.enable();
        }
        this.profiler = TickDurationMonitor.tickProfiler(this.tickTimeTracker.getProfiler(), arg);
    }

    private void endMonitor(@Nullable TickDurationMonitor arg) {
        if (arg != null) {
            arg.endTick();
        }
        this.profiler = this.tickTimeTracker.getProfiler();
    }

    public boolean isDebugRunning() {
        return this.tickTimeTracker.isActive();
    }

    public void enableProfiler() {
        this.profilerStartQueued = true;
    }

    public ProfileResult stopDebug() {
        ProfileResult lv = this.tickTimeTracker.getResult();
        this.tickTimeTracker.disable();
        return lv;
    }

    public Path getSavePath(WorldSavePath arg) {
        return this.session.getDirectory(arg);
    }

    public boolean syncChunkWrites() {
        return true;
    }

    public StructureManager getStructureManager() {
        return this.structureManager;
    }

    public SaveProperties getSaveProperties() {
        return this.saveProperties;
    }

    @Override
    public /* synthetic */ void executeTask(Runnable runnable) {
        this.executeTask((ServerTask)runnable);
    }

    @Override
    public /* synthetic */ boolean canExecute(Runnable runnable) {
        return this.canExecute((ServerTask)runnable);
    }

    @Override
    public /* synthetic */ Runnable createTask(Runnable runnable) {
        return this.createTask(runnable);
    }
}

