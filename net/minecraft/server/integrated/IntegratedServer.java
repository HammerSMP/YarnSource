/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_5455;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.LanServerPinger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.integrated.IntegratedPlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.UserCache;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.GameMode;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class IntegratedServer
extends MinecraftServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftClient client;
    private boolean paused;
    private int lanPort = -1;
    private LanServerPinger lanPinger;
    private UUID localPlayerUuid;

    public IntegratedServer(Thread thread, MinecraftClient arg, class_5455.class_5457 arg2, LevelStorage.Session arg3, ResourcePackManager arg4, ServerResourceManager arg5, SaveProperties arg6, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache arg7, WorldGenerationProgressListenerFactory arg8) {
        super(thread, arg2, arg3, arg6, arg4, arg.getNetworkProxy(), arg.getDataFixer(), arg5, minecraftSessionService, gameProfileRepository, arg7, arg8);
        this.setServerName(arg.getSession().getUsername());
        this.setDemo(arg.isDemo());
        this.setWorldHeight(256);
        this.setPlayerManager(new IntegratedPlayerManager(this, this.dimensionTracker, this.field_24371));
        this.client = arg;
    }

    @Override
    public boolean setupServer() {
        LOGGER.info("Starting integrated minecraft server version " + SharedConstants.getGameVersion().getName());
        this.setOnlineMode(true);
        this.setPvpEnabled(true);
        this.setFlightEnabled(true);
        LOGGER.info("Generating keypair");
        this.setKeyPair(NetworkEncryptionUtils.generateServerKeyPair());
        this.loadWorld();
        this.setMotd(this.getUserName() + " - " + this.getSaveProperties().getLevelName());
        return true;
    }

    @Override
    public void tick(BooleanSupplier shouldKeepTicking) {
        boolean bl = this.paused;
        this.paused = MinecraftClient.getInstance().getNetworkHandler() != null && MinecraftClient.getInstance().isPaused();
        Profiler lv = this.getProfiler();
        if (!bl && this.paused) {
            lv.push("autoSave");
            LOGGER.info("Saving and pausing game...");
            this.getPlayerManager().saveAllPlayerData();
            this.save(false, false, false);
            lv.pop();
        }
        if (this.paused) {
            return;
        }
        super.tick(shouldKeepTicking);
        int i = Math.max(2, this.client.options.viewDistance + -1);
        if (i != this.getPlayerManager().getViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", (Object)i, (Object)this.getPlayerManager().getViewDistance());
            this.getPlayerManager().setViewDistance(i);
        }
    }

    @Override
    public boolean shouldBroadcastRconToOps() {
        return true;
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return true;
    }

    @Override
    public File getRunDirectory() {
        return this.client.runDirectory;
    }

    @Override
    public boolean isDedicated() {
        return false;
    }

    @Override
    public int getRateLimit() {
        return 0;
    }

    @Override
    public boolean isUsingNativeTransport() {
        return false;
    }

    @Override
    public void setCrashReport(CrashReport report) {
        this.client.setCrashReport(report);
    }

    @Override
    public CrashReport populateCrashReport(CrashReport report) {
        report = super.populateCrashReport(report);
        report.getSystemDetailsSection().add("Type", "Integrated Server (map_client.txt)");
        report.getSystemDetailsSection().add("Is Modded", () -> this.getModdedStatusMessage().orElse("Probably not. Jar signature remains and both client + server brands are untouched."));
        return report;
    }

    @Override
    public Optional<String> getModdedStatusMessage() {
        String string = ClientBrandRetriever.getClientModName();
        if (!string.equals("vanilla")) {
            return Optional.of("Definitely; Client brand changed to '" + string + "'");
        }
        string = this.getServerModName();
        if (!"vanilla".equals(string)) {
            return Optional.of("Definitely; Server brand changed to '" + string + "'");
        }
        if (MinecraftClient.class.getSigners() == null) {
            return Optional.of("Very likely; Jar signature invalidated");
        }
        return Optional.empty();
    }

    @Override
    public void addSnooperInfo(Snooper snooper) {
        super.addSnooperInfo(snooper);
        snooper.addInfo("snooper_partner", this.client.getSnooper().getToken());
    }

    @Override
    public boolean openToLan(GameMode gameMode, boolean cheatsAllowed, int port) {
        try {
            this.getNetworkIo().bind(null, port);
            LOGGER.info("Started serving on {}", (Object)port);
            this.lanPort = port;
            this.lanPinger = new LanServerPinger(this.getServerMotd(), port + "");
            this.lanPinger.start();
            this.getPlayerManager().setGameMode(gameMode);
            this.getPlayerManager().setCheatsAllowed(cheatsAllowed);
            int j = this.getPermissionLevel(this.client.player.getGameProfile());
            this.client.player.setClientPermissionLevel(j);
            for (ServerPlayerEntity lv : this.getPlayerManager().getPlayerList()) {
                this.getCommandManager().sendCommandTree(lv);
            }
            return true;
        }
        catch (IOException iOException) {
            return false;
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        if (this.lanPinger != null) {
            this.lanPinger.interrupt();
            this.lanPinger = null;
        }
    }

    @Override
    public void stop(boolean bl) {
        this.submitAndJoin(() -> {
            ArrayList list = Lists.newArrayList(this.getPlayerManager().getPlayerList());
            for (ServerPlayerEntity lv : list) {
                if (lv.getUuid().equals(this.localPlayerUuid)) continue;
                this.getPlayerManager().remove(lv);
            }
        });
        super.stop(bl);
        if (this.lanPinger != null) {
            this.lanPinger.interrupt();
            this.lanPinger = null;
        }
    }

    @Override
    public boolean isRemote() {
        return this.lanPort > -1;
    }

    @Override
    public int getServerPort() {
        return this.lanPort;
    }

    @Override
    public void setDefaultGameMode(GameMode gameMode) {
        super.setDefaultGameMode(gameMode);
        this.getPlayerManager().setGameMode(gameMode);
    }

    @Override
    public boolean areCommandBlocksEnabled() {
        return true;
    }

    @Override
    public int getOpPermissionLevel() {
        return 2;
    }

    @Override
    public int getFunctionPermissionLevel() {
        return 2;
    }

    public void setLocalPlayerUuid(UUID localPlayerUuid) {
        this.localPlayerUuid = localPlayerUuid;
    }

    @Override
    public boolean isHost(GameProfile profile) {
        return profile.getName().equalsIgnoreCase(this.getUserName());
    }

    @Override
    public int adjustTrackingDistance(int initialDistance) {
        return (int)(this.client.options.entityDistanceScaling * (float)initialDistance);
    }

    @Override
    public boolean syncChunkWrites() {
        return this.client.options.field_25623;
    }
}

