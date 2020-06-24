/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.authlib.GameProfile
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  io.netty.buffer.Unpooled
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HeldItemChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.BannedIpEntry;
import net.minecraft.server.BannedIpList;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.OperatorList;
import net.minecraft.server.Whitelist;
import net.minecraft.server.network.DemoServerPlayerInteractionManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PlayerManager {
    public static final File BANNED_PLAYERS_FILE = new File("banned-players.json");
    public static final File BANNED_IPS_FILE = new File("banned-ips.json");
    public static final File OPERATORS_FILE = new File("ops.json");
    public static final File WHITELIST_FILE = new File("whitelist.json");
    private static final Logger LOGGER = LogManager.getLogger();
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    private final MinecraftServer server;
    private final List<ServerPlayerEntity> players = Lists.newArrayList();
    private final Map<UUID, ServerPlayerEntity> playerMap = Maps.newHashMap();
    private final BannedPlayerList bannedProfiles = new BannedPlayerList(BANNED_PLAYERS_FILE);
    private final BannedIpList bannedIps = new BannedIpList(BANNED_IPS_FILE);
    private final OperatorList ops = new OperatorList(OPERATORS_FILE);
    private final Whitelist whitelist = new Whitelist(WHITELIST_FILE);
    private final Map<UUID, ServerStatHandler> statisticsMap = Maps.newHashMap();
    private final Map<UUID, PlayerAdvancementTracker> advancementTrackers = Maps.newHashMap();
    private final WorldSaveHandler saveHandler;
    private boolean whitelistEnabled;
    private final RegistryTracker.Modifiable field_24626;
    protected final int maxPlayers;
    private int viewDistance;
    private GameMode gameMode;
    private boolean cheatsAllowed;
    private int latencyUpdateTimer;

    public PlayerManager(MinecraftServer minecraftServer, RegistryTracker.Modifiable arg, WorldSaveHandler arg2, int i) {
        this.server = minecraftServer;
        this.field_24626 = arg;
        this.maxPlayers = i;
        this.saveHandler = arg2;
    }

    public void onPlayerConnect(ClientConnection arg, ServerPlayerEntity arg22) {
        CompoundTag lv13;
        Entity lv14;
        TranslatableText lv11;
        ServerWorld lv6;
        GameProfile gameProfile = arg22.getGameProfile();
        UserCache lv = this.server.getUserCache();
        GameProfile gameProfile2 = lv.getByUuid(gameProfile.getId());
        String string = gameProfile2 == null ? gameProfile.getName() : gameProfile2.getName();
        lv.add(gameProfile);
        CompoundTag lv2 = this.loadPlayerData(arg22);
        RegistryKey<World> lv3 = lv2 != null ? DimensionType.method_28521(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)lv2.get("Dimension"))).resultOrPartial(((Logger)LOGGER)::error).orElse(World.OVERWORLD) : World.OVERWORLD;
        ServerWorld lv4 = this.server.getWorld(lv3);
        if (lv4 == null) {
            LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", lv3);
            ServerWorld lv5 = this.server.getOverworld();
        } else {
            lv6 = lv4;
        }
        arg22.setWorld(lv6);
        arg22.interactionManager.setWorld((ServerWorld)arg22.world);
        String string2 = "local";
        if (arg.getAddress() != null) {
            string2 = arg.getAddress().toString();
        }
        LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", (Object)arg22.getName().getString(), (Object)string2, (Object)arg22.getEntityId(), (Object)arg22.getX(), (Object)arg22.getY(), (Object)arg22.getZ());
        WorldProperties lv7 = lv6.getLevelProperties();
        this.setGameMode(arg22, null, lv6);
        ServerPlayNetworkHandler lv8 = new ServerPlayNetworkHandler(this.server, arg, arg22);
        GameRules lv9 = lv6.getGameRules();
        boolean bl = lv9.getBoolean(GameRules.DO_IMMEDIATE_RESPAWN);
        boolean bl2 = lv9.getBoolean(GameRules.REDUCED_DEBUG_INFO);
        lv8.sendPacket(new GameJoinS2CPacket(arg22.getEntityId(), arg22.interactionManager.getGameMode(), arg22.interactionManager.method_30119(), BiomeAccess.hashSeed(lv6.getSeed()), lv7.isHardcore(), this.server.getWorldRegistryKeys(), this.field_24626, lv6.getDimensionRegistryKey(), lv6.getRegistryKey(), this.getMaxPlayerCount(), this.viewDistance, bl2, !bl, lv6.isDebugWorld(), lv6.isFlat()));
        lv8.sendPacket(new CustomPayloadS2CPacket(CustomPayloadS2CPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(this.getServer().getServerModName())));
        lv8.sendPacket(new DifficultyS2CPacket(lv7.getDifficulty(), lv7.isDifficultyLocked()));
        lv8.sendPacket(new PlayerAbilitiesS2CPacket(arg22.abilities));
        lv8.sendPacket(new HeldItemChangeS2CPacket(arg22.inventory.selectedSlot));
        lv8.sendPacket(new SynchronizeRecipesS2CPacket(this.server.getRecipeManager().values()));
        lv8.sendPacket(new SynchronizeTagsS2CPacket(this.server.getTagManager()));
        this.sendCommandTree(arg22);
        arg22.getStatHandler().updateStatSet();
        arg22.getRecipeBook().sendInitRecipesPacket(arg22);
        this.sendScoreboard(lv6.getScoreboard(), arg22);
        this.server.forcePlayerSampleUpdate();
        if (arg22.getGameProfile().getName().equalsIgnoreCase(string)) {
            TranslatableText lv10 = new TranslatableText("multiplayer.player.joined", arg22.getDisplayName());
        } else {
            lv11 = new TranslatableText("multiplayer.player.joined.renamed", arg22.getDisplayName(), string);
        }
        this.broadcastChatMessage(lv11.formatted(Formatting.YELLOW), MessageType.SYSTEM, Util.NIL_UUID);
        lv8.requestTeleport(arg22.getX(), arg22.getY(), arg22.getZ(), arg22.yaw, arg22.pitch);
        this.players.add(arg22);
        this.playerMap.put(arg22.getUuid(), arg22);
        this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, arg22));
        for (int i = 0; i < this.players.size(); ++i) {
            arg22.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, this.players.get(i)));
        }
        lv6.onPlayerConnected(arg22);
        this.server.getBossBarManager().onPlayerConnect(arg22);
        this.sendWorldInfo(arg22, lv6);
        if (!this.server.getResourcePackUrl().isEmpty()) {
            arg22.sendResourcePackUrl(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
        }
        for (StatusEffectInstance lv12 : arg22.getStatusEffects()) {
            lv8.sendPacket(new EntityStatusEffectS2CPacket(arg22.getEntityId(), lv12));
        }
        if (lv2 != null && lv2.contains("RootVehicle", 10) && (lv14 = EntityType.loadEntityWithPassengers((lv13 = lv2.getCompound("RootVehicle")).getCompound("Entity"), lv6, arg2 -> {
            if (!lv6.tryLoadEntity((Entity)arg2)) {
                return null;
            }
            return arg2;
        })) != null) {
            Object uUID2;
            if (lv13.containsUuid("Attach")) {
                UUID uUID = lv13.getUuid("Attach");
            } else {
                uUID2 = null;
            }
            if (lv14.getUuid().equals(uUID2)) {
                arg22.startRiding(lv14, true);
            } else {
                for (Entity lv15 : lv14.getPassengersDeep()) {
                    if (!lv15.getUuid().equals(uUID2)) continue;
                    arg22.startRiding(lv15, true);
                    break;
                }
            }
            if (!arg22.hasVehicle()) {
                LOGGER.warn("Couldn't reattach entity to player");
                lv6.removeEntity(lv14);
                for (Entity lv16 : lv14.getPassengersDeep()) {
                    lv6.removeEntity(lv16);
                }
            }
        }
        arg22.onSpawn();
    }

    protected void sendScoreboard(ServerScoreboard arg, ServerPlayerEntity arg2) {
        HashSet set = Sets.newHashSet();
        for (Team lv : arg.getTeams()) {
            arg2.networkHandler.sendPacket(new TeamS2CPacket(lv, 0));
        }
        for (int i = 0; i < 19; ++i) {
            ScoreboardObjective lv2 = arg.getObjectiveForSlot(i);
            if (lv2 == null || set.contains(lv2)) continue;
            List<Packet<?>> list = arg.createChangePackets(lv2);
            for (Packet<?> lv3 : list) {
                arg2.networkHandler.sendPacket(lv3);
            }
            set.add(lv2);
        }
    }

    public void setMainWorld(ServerWorld arg) {
        arg.getWorldBorder().addListener(new WorldBorderListener(){

            @Override
            public void onSizeChange(WorldBorder arg, double d) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(arg, WorldBorderS2CPacket.Type.SET_SIZE));
            }

            @Override
            public void onInterpolateSize(WorldBorder arg, double d, double e, long l) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(arg, WorldBorderS2CPacket.Type.LERP_SIZE));
            }

            @Override
            public void onCenterChanged(WorldBorder arg, double d, double e) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(arg, WorldBorderS2CPacket.Type.SET_CENTER));
            }

            @Override
            public void onWarningTimeChanged(WorldBorder arg, int i) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(arg, WorldBorderS2CPacket.Type.SET_WARNING_TIME));
            }

            @Override
            public void onWarningBlocksChanged(WorldBorder arg, int i) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(arg, WorldBorderS2CPacket.Type.SET_WARNING_BLOCKS));
            }

            @Override
            public void onDamagePerBlockChanged(WorldBorder arg, double d) {
            }

            @Override
            public void onSafeZoneChanged(WorldBorder arg, double d) {
            }
        });
    }

    @Nullable
    public CompoundTag loadPlayerData(ServerPlayerEntity arg) {
        CompoundTag lv3;
        CompoundTag lv = this.server.getSaveProperties().getPlayerData();
        if (arg.getName().getString().equals(this.server.getUserName()) && lv != null) {
            CompoundTag lv2 = lv;
            arg.fromTag(lv2);
            LOGGER.debug("loading single player");
        } else {
            lv3 = this.saveHandler.loadPlayerData(arg);
        }
        return lv3;
    }

    protected void savePlayerData(ServerPlayerEntity arg) {
        PlayerAdvancementTracker lv2;
        this.saveHandler.savePlayerData(arg);
        ServerStatHandler lv = this.statisticsMap.get(arg.getUuid());
        if (lv != null) {
            lv.save();
        }
        if ((lv2 = this.advancementTrackers.get(arg.getUuid())) != null) {
            lv2.save();
        }
    }

    public void remove(ServerPlayerEntity arg) {
        Entity lv2;
        ServerWorld lv = arg.getServerWorld();
        arg.incrementStat(Stats.LEAVE_GAME);
        this.savePlayerData(arg);
        if (arg.hasVehicle() && (lv2 = arg.getRootVehicle()).hasPlayerRider()) {
            LOGGER.debug("Removing player mount");
            arg.stopRiding();
            lv.removeEntity(lv2);
            lv2.removed = true;
            for (Entity lv3 : lv2.getPassengersDeep()) {
                lv.removeEntity(lv3);
                lv3.removed = true;
            }
            lv.getChunk(arg.chunkX, arg.chunkZ).markDirty();
        }
        arg.detach();
        lv.removePlayer(arg);
        arg.getAdvancementTracker().clearCriteria();
        this.players.remove(arg);
        this.server.getBossBarManager().onPlayerDisconnect(arg);
        UUID uUID = arg.getUuid();
        ServerPlayerEntity lv4 = this.playerMap.get(uUID);
        if (lv4 == arg) {
            this.playerMap.remove(uUID);
            this.statisticsMap.remove(uUID);
            this.advancementTrackers.remove(uUID);
        }
        this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.REMOVE_PLAYER, arg));
    }

    @Nullable
    public Text checkCanJoin(SocketAddress socketAddress, GameProfile gameProfile) {
        if (this.bannedProfiles.contains(gameProfile)) {
            BannedPlayerEntry lv = (BannedPlayerEntry)this.bannedProfiles.get(gameProfile);
            TranslatableText lv2 = new TranslatableText("multiplayer.disconnect.banned.reason", lv.getReason());
            if (lv.getExpiryDate() != null) {
                lv2.append(new TranslatableText("multiplayer.disconnect.banned.expiration", DATE_FORMATTER.format(lv.getExpiryDate())));
            }
            return lv2;
        }
        if (!this.isWhitelisted(gameProfile)) {
            return new TranslatableText("multiplayer.disconnect.not_whitelisted");
        }
        if (this.bannedIps.isBanned(socketAddress)) {
            BannedIpEntry lv3 = this.bannedIps.get(socketAddress);
            TranslatableText lv4 = new TranslatableText("multiplayer.disconnect.banned_ip.reason", lv3.getReason());
            if (lv3.getExpiryDate() != null) {
                lv4.append(new TranslatableText("multiplayer.disconnect.banned_ip.expiration", DATE_FORMATTER.format(lv3.getExpiryDate())));
            }
            return lv4;
        }
        if (this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(gameProfile)) {
            return new TranslatableText("multiplayer.disconnect.server_full");
        }
        return null;
    }

    public ServerPlayerEntity createPlayer(GameProfile gameProfile) {
        ServerPlayerInteractionManager lv6;
        UUID uUID = PlayerEntity.getUuidFromProfile(gameProfile);
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayerEntity lv = this.players.get(i);
            if (!lv.getUuid().equals(uUID)) continue;
            list.add(lv);
        }
        ServerPlayerEntity lv2 = this.playerMap.get(gameProfile.getId());
        if (lv2 != null && !list.contains(lv2)) {
            list.add(lv2);
        }
        for (ServerPlayerEntity lv3 : list) {
            lv3.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.duplicate_login"));
        }
        ServerWorld lv4 = this.server.getOverworld();
        if (this.server.isDemo()) {
            DemoServerPlayerInteractionManager lv5 = new DemoServerPlayerInteractionManager(lv4);
        } else {
            lv6 = new ServerPlayerInteractionManager(lv4);
        }
        return new ServerPlayerEntity(this.server, lv4, gameProfile, lv6);
    }

    public ServerPlayerEntity respawnPlayer(ServerPlayerEntity arg, boolean bl) {
        ServerPlayerInteractionManager lv5;
        ServerWorld lv3;
        Optional optional2;
        this.players.remove(arg);
        arg.getServerWorld().removePlayer(arg);
        BlockPos lv = arg.getSpawnPointPosition();
        boolean bl2 = arg.isSpawnPointSet();
        ServerWorld lv2 = this.server.getWorld(arg.getSpawnPointDimension());
        if (lv2 != null && lv != null) {
            Optional<Vec3d> optional = PlayerEntity.findRespawnPosition(lv2, lv, bl2, bl);
        } else {
            optional2 = Optional.empty();
        }
        ServerWorld serverWorld = lv3 = lv2 != null && optional2.isPresent() ? lv2 : this.server.getOverworld();
        if (this.server.isDemo()) {
            DemoServerPlayerInteractionManager lv4 = new DemoServerPlayerInteractionManager(lv3);
        } else {
            lv5 = new ServerPlayerInteractionManager(lv3);
        }
        ServerPlayerEntity lv6 = new ServerPlayerEntity(this.server, lv3, arg.getGameProfile(), lv5);
        lv6.networkHandler = arg.networkHandler;
        lv6.copyFrom(arg, bl);
        lv6.setEntityId(arg.getEntityId());
        lv6.setMainArm(arg.getMainArm());
        for (String string : arg.getScoreboardTags()) {
            lv6.addScoreboardTag(string);
        }
        this.setGameMode(lv6, arg, lv3);
        boolean bl3 = false;
        if (optional2.isPresent()) {
            Vec3d lv7 = (Vec3d)optional2.get();
            lv6.refreshPositionAndAngles(lv7.x, lv7.y, lv7.z, 0.0f, 0.0f);
            lv6.setSpawnPoint(lv3.getRegistryKey(), lv, bl2, false);
            bl3 = !bl && lv3.getBlockState(lv).getBlock() instanceof RespawnAnchorBlock;
        } else if (lv != null) {
            lv6.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.NO_RESPAWN_BLOCK, 0.0f));
        }
        while (!lv3.doesNotCollide(lv6) && lv6.getY() < 256.0) {
            lv6.updatePosition(lv6.getX(), lv6.getY() + 1.0, lv6.getZ());
        }
        WorldProperties lv8 = lv6.world.getLevelProperties();
        lv6.networkHandler.sendPacket(new PlayerRespawnS2CPacket(lv6.world.getDimensionRegistryKey(), lv6.world.getRegistryKey(), BiomeAccess.hashSeed(lv6.getServerWorld().getSeed()), lv6.interactionManager.getGameMode(), lv6.interactionManager.method_30119(), lv6.getServerWorld().isDebugWorld(), lv6.getServerWorld().isFlat(), bl));
        lv6.networkHandler.requestTeleport(lv6.getX(), lv6.getY(), lv6.getZ(), lv6.yaw, lv6.pitch);
        lv6.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(lv3.getSpawnPos()));
        lv6.networkHandler.sendPacket(new DifficultyS2CPacket(lv8.getDifficulty(), lv8.isDifficultyLocked()));
        lv6.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(lv6.experienceProgress, lv6.totalExperience, lv6.experienceLevel));
        this.sendWorldInfo(lv6, lv3);
        this.sendCommandTree(lv6);
        lv3.onPlayerRespawned(lv6);
        this.players.add(lv6);
        this.playerMap.put(lv6.getUuid(), lv6);
        lv6.onSpawn();
        lv6.setHealth(lv6.getHealth());
        if (bl3) {
            lv6.networkHandler.sendPacket(new PlaySoundS2CPacket(SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, lv.getX(), lv.getY(), lv.getZ(), 1.0f, 1.0f));
        }
        return lv6;
    }

    public void sendCommandTree(ServerPlayerEntity arg) {
        GameProfile gameProfile = arg.getGameProfile();
        int i = this.server.getPermissionLevel(gameProfile);
        this.sendCommandTree(arg, i);
    }

    public void updatePlayerLatency() {
        if (++this.latencyUpdateTimer > 600) {
            this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_LATENCY, this.players));
            this.latencyUpdateTimer = 0;
        }
    }

    public void sendToAll(Packet<?> arg) {
        for (int i = 0; i < this.players.size(); ++i) {
            this.players.get((int)i).networkHandler.sendPacket(arg);
        }
    }

    public void sendToDimension(Packet<?> arg, RegistryKey<World> arg2) {
        for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayerEntity lv = this.players.get(i);
            if (lv.world.getRegistryKey() != arg2) continue;
            lv.networkHandler.sendPacket(arg);
        }
    }

    public void sendToTeam(PlayerEntity arg, Text arg2) {
        AbstractTeam lv = arg.getScoreboardTeam();
        if (lv == null) {
            return;
        }
        Collection<String> collection = lv.getPlayerList();
        for (String string : collection) {
            ServerPlayerEntity lv2 = this.getPlayer(string);
            if (lv2 == null || lv2 == arg) continue;
            lv2.sendSystemMessage(arg2, arg.getUuid());
        }
    }

    public void sendToOtherTeams(PlayerEntity arg, Text arg2) {
        AbstractTeam lv = arg.getScoreboardTeam();
        if (lv == null) {
            this.broadcastChatMessage(arg2, MessageType.SYSTEM, arg.getUuid());
            return;
        }
        for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayerEntity lv2 = this.players.get(i);
            if (lv2.getScoreboardTeam() == lv) continue;
            lv2.sendSystemMessage(arg2, arg.getUuid());
        }
    }

    public String[] getPlayerNames() {
        String[] strings = new String[this.players.size()];
        for (int i = 0; i < this.players.size(); ++i) {
            strings[i] = this.players.get(i).getGameProfile().getName();
        }
        return strings;
    }

    public BannedPlayerList getUserBanList() {
        return this.bannedProfiles;
    }

    public BannedIpList getIpBanList() {
        return this.bannedIps;
    }

    public void addToOperators(GameProfile gameProfile) {
        this.ops.add(new OperatorEntry(gameProfile, this.server.getOpPermissionLevel(), this.ops.isOp(gameProfile)));
        ServerPlayerEntity lv = this.getPlayer(gameProfile.getId());
        if (lv != null) {
            this.sendCommandTree(lv);
        }
    }

    public void removeFromOperators(GameProfile gameProfile) {
        this.ops.remove(gameProfile);
        ServerPlayerEntity lv = this.getPlayer(gameProfile.getId());
        if (lv != null) {
            this.sendCommandTree(lv);
        }
    }

    private void sendCommandTree(ServerPlayerEntity arg, int i) {
        if (arg.networkHandler != null) {
            byte d;
            if (i <= 0) {
                int b = 24;
            } else if (i >= 4) {
                int c = 28;
            } else {
                d = (byte)(24 + i);
            }
            arg.networkHandler.sendPacket(new EntityStatusS2CPacket(arg, d));
        }
        this.server.getCommandManager().sendCommandTree(arg);
    }

    public boolean isWhitelisted(GameProfile gameProfile) {
        return !this.whitelistEnabled || this.ops.contains(gameProfile) || this.whitelist.contains(gameProfile);
    }

    public boolean isOperator(GameProfile gameProfile) {
        return this.ops.contains(gameProfile) || this.server.isHost(gameProfile) && this.server.getSaveProperties().areCommandsAllowed() || this.cheatsAllowed;
    }

    @Nullable
    public ServerPlayerEntity getPlayer(String string) {
        for (ServerPlayerEntity lv : this.players) {
            if (!lv.getGameProfile().getName().equalsIgnoreCase(string)) continue;
            return lv;
        }
        return null;
    }

    public void sendToAround(@Nullable PlayerEntity arg, double d, double e, double f, double g, RegistryKey<World> arg2, Packet<?> arg3) {
        for (int i = 0; i < this.players.size(); ++i) {
            double k;
            double j;
            double h;
            ServerPlayerEntity lv = this.players.get(i);
            if (lv == arg || lv.world.getRegistryKey() != arg2 || !((h = d - lv.getX()) * h + (j = e - lv.getY()) * j + (k = f - lv.getZ()) * k < g * g)) continue;
            lv.networkHandler.sendPacket(arg3);
        }
    }

    public void saveAllPlayerData() {
        for (int i = 0; i < this.players.size(); ++i) {
            this.savePlayerData(this.players.get(i));
        }
    }

    public Whitelist getWhitelist() {
        return this.whitelist;
    }

    public String[] getWhitelistedNames() {
        return this.whitelist.getNames();
    }

    public OperatorList getOpList() {
        return this.ops;
    }

    public String[] getOpNames() {
        return this.ops.getNames();
    }

    public void reloadWhitelist() {
    }

    public void sendWorldInfo(ServerPlayerEntity arg, ServerWorld arg2) {
        WorldBorder lv = this.server.getOverworld().getWorldBorder();
        arg.networkHandler.sendPacket(new WorldBorderS2CPacket(lv, WorldBorderS2CPacket.Type.INITIALIZE));
        arg.networkHandler.sendPacket(new WorldTimeUpdateS2CPacket(arg2.getTime(), arg2.getTimeOfDay(), arg2.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)));
        arg.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(arg2.getSpawnPos()));
        if (arg2.isRaining()) {
            arg.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STARTED, 0.0f));
            arg.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED, arg2.getRainGradient(1.0f)));
            arg.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED, arg2.getThunderGradient(1.0f)));
        }
    }

    public void sendPlayerStatus(ServerPlayerEntity arg) {
        arg.openHandledScreen(arg.playerScreenHandler);
        arg.markHealthDirty();
        arg.networkHandler.sendPacket(new HeldItemChangeS2CPacket(arg.inventory.selectedSlot));
    }

    public int getCurrentPlayerCount() {
        return this.players.size();
    }

    public int getMaxPlayerCount() {
        return this.maxPlayers;
    }

    public boolean isWhitelistEnabled() {
        return this.whitelistEnabled;
    }

    public void setWhitelistEnabled(boolean bl) {
        this.whitelistEnabled = bl;
    }

    public List<ServerPlayerEntity> getPlayersByIp(String string) {
        ArrayList list = Lists.newArrayList();
        for (ServerPlayerEntity lv : this.players) {
            if (!lv.getIp().equals(string)) continue;
            list.add(lv);
        }
        return list;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public CompoundTag getUserData() {
        return null;
    }

    @Environment(value=EnvType.CLIENT)
    public void setGameMode(GameMode arg) {
        this.gameMode = arg;
    }

    private void setGameMode(ServerPlayerEntity arg, @Nullable ServerPlayerEntity arg2, ServerWorld arg3) {
        if (arg2 != null) {
            arg.interactionManager.setGameMode(arg2.interactionManager.getGameMode(), arg2.interactionManager.method_30119());
        } else if (this.gameMode != null) {
            arg.interactionManager.setGameMode(this.gameMode, GameMode.NOT_SET);
        }
        arg.interactionManager.setGameModeIfNotPresent(arg3.getServer().getSaveProperties().getGameMode());
    }

    @Environment(value=EnvType.CLIENT)
    public void setCheatsAllowed(boolean bl) {
        this.cheatsAllowed = bl;
    }

    public void disconnectAllPlayers() {
        for (int i = 0; i < this.players.size(); ++i) {
            this.players.get((int)i).networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.server_shutdown"));
        }
    }

    public void broadcastChatMessage(Text arg, MessageType arg2, UUID uUID) {
        this.server.sendSystemMessage(arg, uUID);
        this.sendToAll(new GameMessageS2CPacket(arg, arg2, uUID));
    }

    public ServerStatHandler createStatHandler(PlayerEntity arg) {
        ServerStatHandler lv;
        UUID uUID = arg.getUuid();
        ServerStatHandler serverStatHandler = lv = uUID == null ? null : this.statisticsMap.get(uUID);
        if (lv == null) {
            File file3;
            File file = this.server.getSavePath(WorldSavePath.STATS).toFile();
            File file2 = new File(file, uUID + ".json");
            if (!file2.exists() && (file3 = new File(file, arg.getName().getString() + ".json")).exists() && file3.isFile()) {
                file3.renameTo(file2);
            }
            lv = new ServerStatHandler(this.server, file2);
            this.statisticsMap.put(uUID, lv);
        }
        return lv;
    }

    public PlayerAdvancementTracker getAdvancementTracker(ServerPlayerEntity arg) {
        UUID uUID = arg.getUuid();
        PlayerAdvancementTracker lv = this.advancementTrackers.get(uUID);
        if (lv == null) {
            File file = this.server.getSavePath(WorldSavePath.ADVANCEMENTS).toFile();
            File file2 = new File(file, uUID + ".json");
            lv = new PlayerAdvancementTracker(this.server.getDataFixer(), this, this.server.getAdvancementLoader(), file2, arg);
            this.advancementTrackers.put(uUID, lv);
        }
        lv.setOwner(arg);
        return lv;
    }

    public void setViewDistance(int i) {
        this.viewDistance = i;
        this.sendToAll(new ChunkLoadDistanceS2CPacket(i));
        for (ServerWorld lv : this.server.getWorlds()) {
            if (lv == null) continue;
            lv.getChunkManager().applyViewDistance(i);
        }
    }

    public List<ServerPlayerEntity> getPlayerList() {
        return this.players;
    }

    @Nullable
    public ServerPlayerEntity getPlayer(UUID uUID) {
        return this.playerMap.get(uUID);
    }

    public boolean canBypassPlayerLimit(GameProfile gameProfile) {
        return false;
    }

    public void onDataPacksReloaded() {
        for (PlayerAdvancementTracker lv : this.advancementTrackers.values()) {
            lv.reload(this.server.getAdvancementLoader());
        }
        this.sendToAll(new SynchronizeTagsS2CPacket(this.server.getTagManager()));
        SynchronizeRecipesS2CPacket lv2 = new SynchronizeRecipesS2CPacket(this.server.getRecipeManager().values());
        for (ServerPlayerEntity lv3 : this.players) {
            lv3.networkHandler.sendPacket(lv2);
            lv3.getRecipeBook().sendInitRecipesPacket(lv3);
        }
    }

    public boolean areCheatsAllowed() {
        return this.cheatsAllowed;
    }
}

