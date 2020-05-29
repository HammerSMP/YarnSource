/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.datafixers.DataFixer
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.dedicated;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.class_5350;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.DedicatedPlayerManager;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerWatchdog;
import net.minecraft.server.dedicated.PendingServerCommand;
import net.minecraft.server.dedicated.ServerCommandOutput;
import net.minecraft.server.dedicated.ServerMBean;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.server.dedicated.gui.DedicatedServerGui;
import net.minecraft.server.rcon.QueryResponseHandler;
import net.minecraft.server.rcon.RconListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.logging.UncaughtExceptionHandler;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftDedicatedServer
extends MinecraftServer
implements DedicatedServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern SHA1_PATTERN = Pattern.compile("^[a-fA-F0-9]{40}$");
    private final List<PendingServerCommand> commandQueue = Collections.synchronizedList(Lists.newArrayList());
    private QueryResponseHandler queryResponseHandler;
    private final ServerCommandOutput rconCommandOutput;
    private RconListener rconServer;
    private final ServerPropertiesLoader propertiesLoader;
    @Nullable
    private DedicatedServerGui gui;

    public MinecraftDedicatedServer(LevelStorage.Session arg, ResourcePackManager<ResourcePackProfile> arg2, class_5350 arg3, SaveProperties arg4, ServerPropertiesLoader arg5, DataFixer dataFixer, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache arg6, WorldGenerationProgressListenerFactory arg7) {
        super(arg, arg4, arg2, Proxy.NO_PROXY, dataFixer, arg3, minecraftSessionService, gameProfileRepository, arg6, arg7);
        this.propertiesLoader = arg5;
        this.rconCommandOutput = new ServerCommandOutput(this);
    }

    @Override
    public boolean setupServer() throws IOException {
        Thread thread = new Thread("Server console handler"){

            @Override
            public void run() {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                try {
                    String string;
                    while (!MinecraftDedicatedServer.this.isStopped() && MinecraftDedicatedServer.this.isRunning() && (string = bufferedReader.readLine()) != null) {
                        MinecraftDedicatedServer.this.enqueueCommand(string, MinecraftDedicatedServer.this.getCommandSource());
                    }
                }
                catch (IOException iOException) {
                    LOGGER.error("Exception handling console input", (Throwable)iOException);
                }
            }
        };
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
        thread.start();
        LOGGER.info("Starting minecraft server version " + SharedConstants.getGameVersion().getName());
        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
            LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }
        LOGGER.info("Loading properties");
        ServerPropertiesHandler lv = this.propertiesLoader.getPropertiesHandler();
        if (this.isSinglePlayer()) {
            this.setServerIp("127.0.0.1");
        } else {
            this.setOnlineMode(lv.onlineMode);
            this.setPreventProxyConnections(lv.preventProxyConnections);
            this.setServerIp(lv.serverIp);
        }
        this.setPvpEnabled(lv.pvp);
        this.setFlightEnabled(lv.allowFlight);
        this.setResourcePack(lv.resourcePack, this.createResourcePackHash());
        this.setMotd(lv.motd);
        this.setForceGameMode(lv.forceGameMode);
        super.setPlayerIdleTimeout(lv.playerIdleTimeout.get());
        this.setEnforceWhitelist(lv.enforceWhitelist);
        this.field_24372.setGameMode(lv.gameMode);
        LOGGER.info("Default game type: {}", (Object)lv.gameMode);
        InetAddress inetAddress = null;
        if (!this.getServerIp().isEmpty()) {
            inetAddress = InetAddress.getByName(this.getServerIp());
        }
        if (this.getServerPort() < 0) {
            this.setServerPort(lv.serverPort);
        }
        LOGGER.info("Generating keypair");
        this.setKeyPair(NetworkEncryptionUtils.generateServerKeyPair());
        LOGGER.info("Starting Minecraft server on {}:{}", (Object)(this.getServerIp().isEmpty() ? "*" : this.getServerIp()), (Object)this.getServerPort());
        try {
            this.getNetworkIo().bind(inetAddress, this.getServerPort());
        }
        catch (IOException iOException) {
            LOGGER.warn("**** FAILED TO BIND TO PORT!");
            LOGGER.warn("The exception was: {}", (Object)iOException.toString());
            LOGGER.warn("Perhaps a server is already running on that port?");
            return false;
        }
        if (!this.isOnlineMode()) {
            LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
            LOGGER.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
        }
        if (this.convertData()) {
            this.getUserCache().save();
        }
        if (!ServerConfigHandler.checkSuccess(this)) {
            return false;
        }
        this.setPlayerManager(new DedicatedPlayerManager(this, this.field_25132, this.field_24371));
        long l = Util.getMeasuringTimeNano();
        this.setWorldHeight(lv.maxBuildHeight);
        SkullBlockEntity.setUserCache(this.getUserCache());
        SkullBlockEntity.setSessionService(this.getSessionService());
        UserCache.setUseRemote(this.isOnlineMode());
        LOGGER.info("Preparing level \"{}\"", (Object)this.getLevelName());
        this.loadWorld();
        long m = Util.getMeasuringTimeNano() - l;
        String string = String.format(Locale.ROOT, "%.3fs", (double)m / 1.0E9);
        LOGGER.info("Done ({})! For help, type \"help\"", (Object)string);
        if (lv.announcePlayerAchievements != null) {
            this.getGameRules().get(GameRules.ANNOUNCE_ADVANCEMENTS).set(lv.announcePlayerAchievements, this);
        }
        if (lv.enableQuery) {
            LOGGER.info("Starting GS4 status listener");
            this.queryResponseHandler = new QueryResponseHandler(this);
            this.queryResponseHandler.start();
        }
        if (lv.enableRcon) {
            LOGGER.info("Starting remote control listener");
            this.rconServer = new RconListener(this);
            this.rconServer.start();
        }
        if (this.getMaxTickTime() > 0L) {
            Thread thread2 = new Thread(new DedicatedServerWatchdog(this));
            thread2.setUncaughtExceptionHandler(new UncaughtExceptionHandler(LOGGER));
            thread2.setName("Server Watchdog");
            thread2.setDaemon(true);
            thread2.start();
        }
        Items.AIR.appendStacks(ItemGroup.SEARCH, DefaultedList.of());
        if (lv.enableJmxMonitoring) {
            ServerMBean.register(this);
        }
        return true;
    }

    @Override
    public boolean shouldSpawnAnimals() {
        return this.getProperties().spawnAnimals && super.shouldSpawnAnimals();
    }

    @Override
    public boolean isMonsterSpawningEnabled() {
        return this.propertiesLoader.getPropertiesHandler().spawnMonsters && super.isMonsterSpawningEnabled();
    }

    @Override
    public boolean shouldSpawnNpcs() {
        return this.propertiesLoader.getPropertiesHandler().spawnNpcs && super.shouldSpawnNpcs();
    }

    public String createResourcePackHash() {
        String string3;
        ServerPropertiesHandler lv = this.propertiesLoader.getPropertiesHandler();
        if (!lv.resourcePackSha1.isEmpty()) {
            String string = lv.resourcePackSha1;
            if (!Strings.isNullOrEmpty((String)lv.resourcePackHash)) {
                LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
            }
        } else if (!Strings.isNullOrEmpty((String)lv.resourcePackHash)) {
            LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
            String string2 = lv.resourcePackHash;
        } else {
            string3 = "";
        }
        if (!string3.isEmpty() && !SHA1_PATTERN.matcher(string3).matches()) {
            LOGGER.warn("Invalid sha1 for ressource-pack-sha1");
        }
        if (!lv.resourcePack.isEmpty() && string3.isEmpty()) {
            LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
        }
        return string3;
    }

    @Override
    public ServerPropertiesHandler getProperties() {
        return this.propertiesLoader.getPropertiesHandler();
    }

    @Override
    public void method_27731() {
        this.setDifficulty(this.getProperties().difficulty, true);
    }

    @Override
    public boolean isHardcore() {
        return this.getProperties().hardcore;
    }

    @Override
    public CrashReport populateCrashReport(CrashReport arg) {
        arg = super.populateCrashReport(arg);
        arg.getSystemDetailsSection().add("Is Modded", () -> this.getModdedStatusMessage().orElse("Unknown (can't tell)"));
        arg.getSystemDetailsSection().add("Type", () -> "Dedicated Server (map_server.txt)");
        return arg;
    }

    @Override
    public Optional<String> getModdedStatusMessage() {
        String string = this.getServerModName();
        if (!"vanilla".equals(string)) {
            return Optional.of("Definitely; Server brand changed to '" + string + "'");
        }
        return Optional.empty();
    }

    @Override
    public void exit() {
        if (this.gui != null) {
            this.gui.stop();
        }
        if (this.rconServer != null) {
            this.rconServer.stop();
        }
        if (this.queryResponseHandler != null) {
            this.queryResponseHandler.stop();
        }
    }

    @Override
    public void tickWorlds(BooleanSupplier booleanSupplier) {
        super.tickWorlds(booleanSupplier);
        this.executeQueuedCommands();
    }

    @Override
    public boolean isNetherAllowed() {
        return this.getProperties().allowNether;
    }

    @Override
    public void addSnooperInfo(Snooper arg) {
        arg.addInfo("whitelist_enabled", this.getPlayerManager().isWhitelistEnabled());
        arg.addInfo("whitelist_count", this.getPlayerManager().getWhitelistedNames().length);
        super.addSnooperInfo(arg);
    }

    public void enqueueCommand(String string, ServerCommandSource arg) {
        this.commandQueue.add(new PendingServerCommand(string, arg));
    }

    public void executeQueuedCommands() {
        while (!this.commandQueue.isEmpty()) {
            PendingServerCommand lv = this.commandQueue.remove(0);
            this.getCommandManager().execute(lv.source, lv.command);
        }
    }

    @Override
    public boolean isDedicated() {
        return true;
    }

    @Override
    public boolean isUsingNativeTransport() {
        return this.getProperties().useNativeTransport;
    }

    @Override
    public DedicatedPlayerManager getPlayerManager() {
        return (DedicatedPlayerManager)super.getPlayerManager();
    }

    @Override
    public boolean isRemote() {
        return true;
    }

    @Override
    public String getHostname() {
        return this.getServerIp();
    }

    @Override
    public int getPort() {
        return this.getServerPort();
    }

    @Override
    public String getMotd() {
        return this.getServerMotd();
    }

    public void createGui() {
        if (this.gui == null) {
            this.gui = DedicatedServerGui.create(this);
        }
    }

    @Override
    public boolean hasGui() {
        return this.gui != null;
    }

    @Override
    public boolean openToLan(GameMode arg, boolean bl, int i) {
        return false;
    }

    @Override
    public boolean areCommandBlocksEnabled() {
        return this.getProperties().enableCommandBlock;
    }

    @Override
    public int getSpawnProtectionRadius() {
        return this.getProperties().spawnProtection;
    }

    @Override
    public boolean isSpawnProtected(ServerWorld arg, BlockPos arg2, PlayerEntity arg3) {
        int j;
        if (arg.method_27983() != World.field_25179) {
            return false;
        }
        if (this.getPlayerManager().getOpList().isEmpty()) {
            return false;
        }
        if (this.getPlayerManager().isOperator(arg3.getGameProfile())) {
            return false;
        }
        if (this.getSpawnProtectionRadius() <= 0) {
            return false;
        }
        BlockPos lv = arg.getSpawnPos();
        int i = MathHelper.abs(arg2.getX() - lv.getX());
        int k = Math.max(i, j = MathHelper.abs(arg2.getZ() - lv.getZ()));
        return k <= this.getSpawnProtectionRadius();
    }

    @Override
    public boolean acceptsStatusQuery() {
        return this.getProperties().enableStatus;
    }

    @Override
    public int getOpPermissionLevel() {
        return this.getProperties().opPermissionLevel;
    }

    @Override
    public int getFunctionPermissionLevel() {
        return this.getProperties().functionPermissionLevel;
    }

    @Override
    public void setPlayerIdleTimeout(int i) {
        super.setPlayerIdleTimeout(i);
        this.propertiesLoader.apply(arg -> (ServerPropertiesHandler)arg.playerIdleTimeout.set(i));
    }

    @Override
    public boolean shouldBroadcastRconToOps() {
        return this.getProperties().broadcastRconToOps;
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return this.getProperties().broadcastConsoleToOps;
    }

    @Override
    public int getMaxWorldBorderRadius() {
        return this.getProperties().maxWorldSize;
    }

    @Override
    public int getNetworkCompressionThreshold() {
        return this.getProperties().networkCompressionThreshold;
    }

    protected boolean convertData() {
        int i;
        boolean bl = false;
        for (i = 0; !bl && i <= 2; ++i) {
            if (i > 0) {
                LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
                this.sleepFiveSeconds();
            }
            bl = ServerConfigHandler.convertBannedPlayers(this);
        }
        boolean bl2 = false;
        for (i = 0; !bl2 && i <= 2; ++i) {
            if (i > 0) {
                LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
                this.sleepFiveSeconds();
            }
            bl2 = ServerConfigHandler.convertBannedIps(this);
        }
        boolean bl3 = false;
        for (i = 0; !bl3 && i <= 2; ++i) {
            if (i > 0) {
                LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
                this.sleepFiveSeconds();
            }
            bl3 = ServerConfigHandler.convertOperators(this);
        }
        boolean bl4 = false;
        for (i = 0; !bl4 && i <= 2; ++i) {
            if (i > 0) {
                LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
                this.sleepFiveSeconds();
            }
            bl4 = ServerConfigHandler.convertWhitelist(this);
        }
        boolean bl5 = false;
        for (i = 0; !bl5 && i <= 2; ++i) {
            if (i > 0) {
                LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
                this.sleepFiveSeconds();
            }
            bl5 = ServerConfigHandler.convertPlayerFiles(this);
        }
        return bl || bl2 || bl3 || bl4 || bl5;
    }

    private void sleepFiveSeconds() {
        try {
            Thread.sleep(5000L);
        }
        catch (InterruptedException interruptedException) {
            return;
        }
    }

    public long getMaxTickTime() {
        return this.getProperties().maxTickTime;
    }

    @Override
    public String getPlugins() {
        return "";
    }

    @Override
    public String executeRconCommand(String string) {
        this.rconCommandOutput.clear();
        this.submitAndJoin(() -> this.getCommandManager().execute(this.rconCommandOutput.createReconCommandSource(), string));
        return this.rconCommandOutput.asString();
    }

    public void setUseWhitelist(boolean bl) {
        this.propertiesLoader.apply(arg -> (ServerPropertiesHandler)arg.whiteList.set(bl));
    }

    @Override
    public void shutdown() {
        super.shutdown();
        Util.shutdownServerWorkerExecutor();
    }

    @Override
    public boolean isHost(GameProfile gameProfile) {
        return false;
    }

    @Override
    public int adjustTrackingDistance(int i) {
        return this.getProperties().entityBroadcastRangePercentage * i / 100;
    }

    @Override
    public String getLevelName() {
        return this.session.getDirectoryName();
    }

    @Override
    public boolean syncChunkWrites() {
        return this.propertiesLoader.getPropertiesHandler().syncChunkWrites;
    }

    @Override
    public /* synthetic */ PlayerManager getPlayerManager() {
        return this.getPlayerManager();
    }
}

