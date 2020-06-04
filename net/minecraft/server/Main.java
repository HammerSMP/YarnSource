/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.datafixers.DataFixer
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

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
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
import net.minecraft.datafixer.Schemas;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.server.dedicated.EulaReader;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.text.Text;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
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
            boolean bl2;
            void lv12;
            boolean bl;
            SaveProperties lv6;
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
            if (optionSet.has((OptionSpec)optionSpec5)) {
                Main.method_29173(lv5, Schemas.getFixer(), optionSet.has((OptionSpec)optionSpec6), () -> true);
            }
            if ((lv6 = lv5.readLevelProperties()) == null) {
                LevelInfo lv9;
                if (optionSet.has((OptionSpec)optionSpec3)) {
                    LevelInfo lv7 = MinecraftServer.DEMO_LEVEL_INFO;
                } else {
                    ServerPropertiesHandler lv8 = lv.getPropertiesHandler();
                    lv9 = new LevelInfo(lv8.levelName, lv8.gameMode, lv8.hardcore, lv8.difficulty, false, new GameRules(), optionSet.has((OptionSpec)optionSpec4) ? lv8.field_24623.withBonusChest() : lv8.field_24623);
                }
                lv6 = new LevelProperties(lv9);
            }
            if (bl = optionSet.has((OptionSpec)optionSpec7)) {
                LOGGER.warn("Safe mode active, only vanilla datapack will be loaded");
            }
            ResourcePackManager<ResourcePackProfile> lv10 = MinecraftServer.createResourcePackManager(lv5.getDirectory(WorldSavePath.DATAPACKS), lv6, bl);
            CompletableFuture<ServerResourceManager> completableFuture = ServerResourceManager.reload(lv10.method_29211(), true, lv.getPropertiesHandler().functionPermissionLevel, Util.getServerWorkerExecutor(), Runnable::run);
            try {
                ServerResourceManager lv11 = completableFuture.get();
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", (Throwable)exception);
                lv10.close();
                return;
            }
            lv12.method_29475();
            final MinecraftDedicatedServer lv13 = new MinecraftDedicatedServer(lv5, lv10, (ServerResourceManager)lv12, lv6, lv, Schemas.getFixer(), minecraftSessionService, gameProfileRepository, lv3, WorldGenerationProgressLogger::new);
            lv13.setServerName((String)optionSet.valueOf((OptionSpec)optionSpec9));
            lv13.setServerPort((Integer)optionSet.valueOf((OptionSpec)optionSpec12));
            lv13.setDemo(optionSet.has((OptionSpec)optionSpec3));
            lv13.setServerId((String)optionSet.valueOf((OptionSpec)optionSpec13));
            boolean bl3 = bl2 = !optionSet.has((OptionSpec)optionSpec) && !optionSet.valuesOf((OptionSpec)optionSpec14).contains("nogui");
            if (bl2 && !GraphicsEnvironment.isHeadless()) {
                lv13.createGui();
            }
            lv13.start();
            Thread thread = new Thread("Server Shutdown Thread"){

                @Override
                public void run() {
                    lv13.stop(true);
                }
            };
            thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
            Runtime.getRuntime().addShutdownHook(thread);
        }
        catch (Exception exception2) {
            LOGGER.fatal("Failed to start the minecraft server", (Throwable)exception2);
        }
    }

    private static void method_29173(LevelStorage.Session arg, DataFixer dataFixer, boolean bl, BooleanSupplier booleanSupplier) {
        LOGGER.info("Forcing world upgrade!");
        SaveProperties lv = arg.readLevelProperties();
        if (lv != null) {
            WorldUpdater lv2 = new WorldUpdater(arg, dataFixer, lv, bl);
            Text lv3 = null;
            while (!lv2.isDone()) {
                int i;
                Text lv4 = lv2.getStatus();
                if (lv3 != lv4) {
                    lv3 = lv4;
                    LOGGER.info(lv2.getStatus().getString());
                }
                if ((i = lv2.getTotalChunkCount()) > 0) {
                    int j = lv2.getUpgradedChunkCount() + lv2.getSkippedChunkCount();
                    LOGGER.info("{}% completed ({} / {} chunks)...", (Object)MathHelper.floor((float)j / (float)i * 100.0f), (Object)j, (Object)i);
                }
                if (!booleanSupplier.getAsBoolean()) {
                    lv2.cancel();
                    continue;
                }
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException interruptedException) {}
            }
        }
    }
}

