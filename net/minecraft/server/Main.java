/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.serialization.Lifecycle
 *  joptsimple.AbstractOptionSpec
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.NonOptionArgumentSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  joptsimple.OptionSpecBuilder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Lifecycle;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.OutputStream;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.minecraft.Bootstrap;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.Tag;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.dedicated.EulaReader;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.text.Text;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.updater.WorldUpdater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();

    /*
     * WARNING - void declaration
     */
    public static void main(String[] strings) {
        OptionParser optionParser = new OptionParser();
        OptionSpecBuilder optionSpec = optionParser.accepts("nogui");
        OptionSpecBuilder optionSpec2 = optionParser.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
        OptionSpecBuilder optionSpec3 = optionParser.accepts("demo");
        OptionSpecBuilder optionSpec4 = optionParser.accepts("bonusChest");
        OptionSpecBuilder optionSpec5 = optionParser.accepts("forceUpgrade");
        OptionSpecBuilder optionSpec6 = optionParser.accepts("eraseCache");
        OptionSpecBuilder optionSpec7 = optionParser.accepts("safeMode", "Loads level with vanilla datapack only");
        AbstractOptionSpec optionSpec8 = optionParser.accepts("help").forHelp();
        ArgumentAcceptingOptionSpec optionSpec9 = optionParser.accepts("singleplayer").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec10 = optionParser.accepts("universe").withRequiredArg().defaultsTo((Object)".", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec optionSpec11 = optionParser.accepts("world").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec12 = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo((Object)-1, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec optionSpec13 = optionParser.accepts("serverId").withRequiredArg();
        NonOptionArgumentSpec optionSpec14 = optionParser.nonOptions();
        try {
            void lv10;
            OptionSet optionSet = optionParser.parse(strings);
            if (optionSet.has((OptionSpec)optionSpec8)) {
                optionParser.printHelpOn((OutputStream)System.err);
                return;
            }
            CrashReport.initCrashReport();
            Bootstrap.initialize();
            Bootstrap.logMissing();
            Util.method_29476();
            Path path = Paths.get("server.properties", new String[0]);
            ServerPropertiesLoader lv = new ServerPropertiesLoader(path);
            lv.store();
            Path path2 = Paths.get("eula.txt", new String[0]);
            EulaReader lv2 = new EulaReader(path2);
            if (optionSet.has((OptionSpec)optionSpec2)) {
                LOGGER.info("Initialized '{}' and '{}'", (Object)path.toAbsolutePath(), (Object)path2.toAbsolutePath());
                return;
            }
            if (!lv2.isEulaAgreedTo()) {
                LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
                return;
            }
            File file = new File((String)optionSet.valueOf((OptionSpec)optionSpec10));
            YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
            MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
            GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
            UserCache lv3 = new UserCache(gameProfileRepository, new File(file, MinecraftServer.USER_CACHE_FILE.getName()));
            String string = (String)Optional.ofNullable(optionSet.valueOf((OptionSpec)optionSpec11)).orElse(lv.getPropertiesHandler().levelName);
            LevelStorage lv4 = LevelStorage.create(file.toPath());
            LevelStorage.Session lv5 = lv4.createSession(string);
            MinecraftServer.convertLevel(lv5);
            DataPackSettings lv6 = lv5.method_29585();
            boolean bl = optionSet.has((OptionSpec)optionSpec7);
            if (bl) {
                LOGGER.warn("Safe mode active, only vanilla datapack will be loaded");
            }
            ResourcePackManager lv7 = new ResourcePackManager(new VanillaDataPackProvider(), new FileResourcePackProvider(lv5.getDirectory(WorldSavePath.DATAPACKS).toFile(), ResourcePackSource.PACK_SOURCE_WORLD));
            DataPackSettings lv8 = MinecraftServer.loadDataPacks(lv7, lv6 == null ? DataPackSettings.SAFE_MODE : lv6, bl);
            CompletableFuture<ServerResourceManager> completableFuture = ServerResourceManager.reload(lv7.createResourcePacks(), CommandManager.RegistrationEnvironment.DEDICATED, lv.getPropertiesHandler().functionPermissionLevel, Util.getServerWorkerExecutor(), Runnable::run);
            try {
                ServerResourceManager lv9 = completableFuture.get();
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", (Throwable)exception);
                lv7.close();
                return;
            }
            lv10.loadRegistryTags();
            RegistryTracker.Modifiable lv11 = RegistryTracker.create();
            RegistryOps<Tag> lv12 = RegistryOps.of(NbtOps.INSTANCE, lv10.getResourceManager(), lv11);
            SaveProperties lv13 = lv5.readLevelProperties(lv12, lv8);
            if (lv13 == null) {
                GeneratorOptions lv18;
                LevelInfo lv17;
                if (optionSet.has((OptionSpec)optionSpec3)) {
                    LevelInfo lv14 = MinecraftServer.DEMO_LEVEL_INFO;
                    GeneratorOptions lv15 = GeneratorOptions.DEMO_CONFIG;
                } else {
                    ServerPropertiesHandler lv16 = lv.getPropertiesHandler();
                    lv17 = new LevelInfo(lv16.levelName, lv16.gameMode, lv16.hardcore, lv16.difficulty, false, new GameRules(), lv8);
                    lv18 = optionSet.has((OptionSpec)optionSpec4) ? lv16.field_24623.withBonusChest() : lv16.field_24623;
                }
                lv13 = new LevelProperties(lv17, lv18, Lifecycle.stable());
            }
            if (optionSet.has((OptionSpec)optionSpec5)) {
                Main.forceUpgradeWorld(lv5, Schemas.getFixer(), optionSet.has((OptionSpec)optionSpec6), () -> true, lv13.getGeneratorOptions().getWorlds());
            }
            lv5.method_27425(lv11, lv13);
            SaveProperties lv19 = lv13;
            final MinecraftDedicatedServer lv20 = MinecraftServer.startServer(arg_0 -> Main.method_29734(lv11, lv5, lv7, (ServerResourceManager)lv10, lv19, lv, minecraftSessionService, gameProfileRepository, lv3, optionSet, (OptionSpec)optionSpec9, (OptionSpec)optionSpec12, (OptionSpec)optionSpec3, (OptionSpec)optionSpec13, (OptionSpec)optionSpec, (OptionSpec)optionSpec14, arg_0));
            Thread thread = new Thread("Server Shutdown Thread"){

                @Override
                public void run() {
                    lv20.stop(true);
                }
            };
            thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
            Runtime.getRuntime().addShutdownHook(thread);
        }
        catch (Exception exception2) {
            LOGGER.fatal("Failed to start the minecraft server", (Throwable)exception2);
        }
    }

    private static void forceUpgradeWorld(LevelStorage.Session arg, DataFixer dataFixer, boolean bl, BooleanSupplier booleanSupplier, ImmutableSet<RegistryKey<World>> immutableSet) {
        LOGGER.info("Forcing world upgrade!");
        WorldUpdater lv = new WorldUpdater(arg, dataFixer, immutableSet, bl);
        Text lv2 = null;
        while (!lv.isDone()) {
            int i;
            Text lv3 = lv.getStatus();
            if (lv2 != lv3) {
                lv2 = lv3;
                LOGGER.info(lv.getStatus().getString());
            }
            if ((i = lv.getTotalChunkCount()) > 0) {
                int j = lv.getUpgradedChunkCount() + lv.getSkippedChunkCount();
                LOGGER.info("{}% completed ({} / {} chunks)...", (Object)MathHelper.floor((float)j / (float)i * 100.0f), (Object)j, (Object)i);
            }
            if (!booleanSupplier.getAsBoolean()) {
                lv.cancel();
                continue;
            }
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    private static /* synthetic */ MinecraftDedicatedServer method_29734(RegistryTracker.Modifiable arg, LevelStorage.Session arg2, ResourcePackManager arg3, ServerResourceManager arg4, SaveProperties arg5, ServerPropertiesLoader arg6, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache arg7, OptionSet optionSet, OptionSpec optionSpec, OptionSpec optionSpec2, OptionSpec optionSpec3, OptionSpec optionSpec4, OptionSpec optionSpec5, OptionSpec optionSpec6, Thread thread) {
        boolean bl;
        MinecraftDedicatedServer lv = new MinecraftDedicatedServer(thread, arg, arg2, arg3, arg4, arg5, arg6, Schemas.getFixer(), minecraftSessionService, gameProfileRepository, arg7, WorldGenerationProgressLogger::new);
        lv.setServerName((String)optionSet.valueOf(optionSpec));
        lv.setServerPort((Integer)optionSet.valueOf(optionSpec2));
        lv.setDemo(optionSet.has(optionSpec3));
        lv.setServerId((String)optionSet.valueOf(optionSpec4));
        boolean bl2 = bl = !optionSet.has(optionSpec5) && !optionSet.valuesOf(optionSpec6).contains("nogui");
        if (bl && !GraphicsEnvironment.isHeadless()) {
            lv.createGui();
        }
        return lv;
    }
}

