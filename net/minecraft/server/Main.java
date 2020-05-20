/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
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
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.OutputStream;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.minecraft.Bootstrap;
import net.minecraft.class_5219;
import net.minecraft.datafixer.Schemas;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.server.dedicated.EulaReader;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.util.UserCache;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] strings) {
        OptionParser optionParser = new OptionParser();
        OptionSpecBuilder optionSpec = optionParser.accepts("nogui");
        OptionSpecBuilder optionSpec2 = optionParser.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
        OptionSpecBuilder optionSpec3 = optionParser.accepts("demo");
        OptionSpecBuilder optionSpec4 = optionParser.accepts("bonusChest");
        OptionSpecBuilder optionSpec5 = optionParser.accepts("forceUpgrade");
        OptionSpecBuilder optionSpec6 = optionParser.accepts("eraseCache");
        AbstractOptionSpec optionSpec7 = optionParser.accepts("help").forHelp();
        ArgumentAcceptingOptionSpec optionSpec8 = optionParser.accepts("singleplayer").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec9 = optionParser.accepts("universe").withRequiredArg().defaultsTo((Object)".", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec optionSpec10 = optionParser.accepts("world").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec11 = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo((Object)-1, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec optionSpec12 = optionParser.accepts("serverId").withRequiredArg();
        NonOptionArgumentSpec optionSpec13 = optionParser.nonOptions();
        try {
            boolean bl;
            OptionSet optionSet = optionParser.parse(strings);
            if (optionSet.has((OptionSpec)optionSpec7)) {
                optionParser.printHelpOn((OutputStream)System.err);
                return;
            }
            CrashReport.initCrashReport();
            Bootstrap.initialize();
            Bootstrap.logMissing();
            Path path = Paths.get("server.properties", new String[0]);
            ServerPropertiesLoader lv = new ServerPropertiesLoader(path);
            lv.store();
            Path path2 = Paths.get("eula.txt", new String[0]);
            EulaReader lv2 = new EulaReader(path2);
            if (optionSet.has((OptionSpec)optionSpec2)) {
                LOGGER.info("Initialized '" + path.toAbsolutePath().toString() + "' and '" + path2.toAbsolutePath().toString() + "'");
                return;
            }
            if (!lv2.isEulaAgreedTo()) {
                LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
                return;
            }
            File file = new File((String)optionSet.valueOf((OptionSpec)optionSpec9));
            YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
            MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
            GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
            UserCache lv3 = new UserCache(gameProfileRepository, new File(file, MinecraftServer.USER_CACHE_FILE.getName()));
            String string = (String)Optional.ofNullable(optionSet.valueOf((OptionSpec)optionSpec10)).orElse(lv.getPropertiesHandler().levelName);
            LevelStorage lv4 = LevelStorage.create(file.toPath());
            LevelStorage.Session lv5 = lv4.createSession(string);
            MinecraftServer.method_27725(lv5, Schemas.getFixer(), optionSet.has((OptionSpec)optionSpec5), optionSet.has((OptionSpec)optionSpec6), () -> true);
            class_5219 lv6 = lv5.readLevelProperties();
            if (lv6 == null) {
                LevelInfo lv9;
                if (optionSet.has((OptionSpec)optionSpec3)) {
                    LevelInfo lv7 = MinecraftServer.DEMO_LEVEL_INFO;
                } else {
                    ServerPropertiesHandler lv8 = lv.getPropertiesHandler();
                    lv9 = new LevelInfo(lv8.levelName, lv8.gameMode, lv8.hardcore, lv8.difficulty, false, new GameRules(), optionSet.has((OptionSpec)optionSpec4) ? lv8.field_24623 : lv8.field_24623.method_28036());
                }
                lv6 = new LevelProperties(lv9);
            }
            final MinecraftDedicatedServer lv10 = new MinecraftDedicatedServer(lv5, lv6, lv, Schemas.getFixer(), minecraftSessionService, gameProfileRepository, lv3, WorldGenerationProgressLogger::new);
            lv10.setServerName((String)optionSet.valueOf((OptionSpec)optionSpec8));
            lv10.setServerPort((Integer)optionSet.valueOf((OptionSpec)optionSpec11));
            lv10.setDemo(optionSet.has((OptionSpec)optionSpec3));
            lv10.setServerId((String)optionSet.valueOf((OptionSpec)optionSpec12));
            boolean bl2 = bl = !optionSet.has((OptionSpec)optionSpec) && !optionSet.valuesOf((OptionSpec)optionSpec13).contains("nogui");
            if (bl && !GraphicsEnvironment.isHeadless()) {
                lv10.createGui();
            }
            lv10.start();
            Thread thread = new Thread("Server Shutdown Thread"){

                @Override
                public void run() {
                    lv10.stop(true);
                }
            };
            thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
            Runtime.getRuntime().addShutdownHook(thread);
        }
        catch (Exception exception) {
            LOGGER.fatal("Failed to start the minecraft server", (Throwable)exception);
        }
    }
}

