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
import net.minecraft.class_5455;
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
    private final class_5455.class_5457 registryTracker;
    protected final int maxPlayers;
    private int viewDistance;
    private GameMode gameMode;
    private boolean cheatsAllowed;
    private int latencyUpdateTimer;

    public PlayerManager(MinecraftServer server, class_5455.class_5457 tracker, WorldSaveHandler saveHandler, int maxPlayers) {
        this.server = server;
        this.registryTracker = tracker;
        this.maxPlayers = maxPlayers;
        this.saveHandler = saveHandler;
    }

    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player) {
        CompoundTag lv13;
        Entity lv14;
        TranslatableText lv11;
        ServerWorld lv6;
        GameProfile gameProfile = player.getGameProfile();
        UserCache lv = this.server.getUserCache();
        GameProfile gameProfile2 = lv.getByUuid(gameProfile.getId());
        String string = gameProfile2 == null ? gameProfile.getName() : gameProfile2.getName();
        lv.add(gameProfile);
        CompoundTag lv2 = this.loadPlayerData(player);
        RegistryKey<World> lv3 = lv2 != null ? DimensionType.method_28521(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)lv2.get("Dimension"))).resultOrPartial(((Logger)LOGGER)::error).orElse(World.OVERWORLD) : World.OVERWORLD;
        ServerWorld lv4 = this.server.getWorld(lv3);
        if (lv4 == null) {
            LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", lv3);
            ServerWorld lv5 = this.server.getOverworld();
        } else {
            lv6 = lv4;
        }
        player.setWorld(lv6);
        player.interactionManager.setWorld((ServerWorld)player.world);
        String string2 = "local";
        if (connection.getAddress() != null) {
            string2 = connection.getAddress().toString();
        }
        LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", (Object)player.getName().getString(), (Object)string2, (Object)player.getEntityId(), (Object)player.getX(), (Object)player.getY(), (Object)player.getZ());
        WorldProperties lv7 = lv6.getLevelProperties();
        this.setGameMode(player, null, lv6);
        ServerPlayNetworkHandler lv8 = new ServerPlayNetworkHandler(this.server, connection, player);
        GameRules lv9 = lv6.getGameRules();
        boolean bl = lv9.getBoolean(GameRules.DO_IMMEDIATE_RESPAWN);
        boolean bl2 = lv9.getBoolean(GameRules.REDUCED_DEBUG_INFO);
        lv8.sendPacket(new GameJoinS2CPacket(player.getEntityId(), player.interactionManager.getGameMode(), player.interactionManager.method_30119(), BiomeAccess.hashSeed(lv6.getSeed()), lv7.isHardcore(), this.server.getWorldRegistryKeys(), this.registryTracker, lv6.getDimensionRegistryKey(), lv6.getRegistryKey(), this.getMaxPlayerCount(), this.viewDistance, bl2, !bl, lv6.isDebugWorld(), lv6.isFlat()));
        lv8.sendPacket(new CustomPayloadS2CPacket(CustomPayloadS2CPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(this.getServer().getServerModName())));
        lv8.sendPacket(new DifficultyS2CPacket(lv7.getDifficulty(), lv7.isDifficultyLocked()));
        lv8.sendPacket(new PlayerAbilitiesS2CPacket(player.abilities));
        lv8.sendPacket(new HeldItemChangeS2CPacket(player.inventory.selectedSlot));
        lv8.sendPacket(new SynchronizeRecipesS2CPacket(this.server.getRecipeManager().values()));
        lv8.sendPacket(new SynchronizeTagsS2CPacket(this.server.getTagManager()));
        this.sendCommandTree(player);
        player.getStatHandler().updateStatSet();
        player.getRecipeBook().sendInitRecipesPacket(player);
        this.sendScoreboard(lv6.getScoreboard(), player);
        this.server.forcePlayerSampleUpdate();
        if (player.getGameProfile().getName().equalsIgnoreCase(string)) {
            TranslatableText lv10 = new TranslatableText("multiplayer.player.joined", player.getDisplayName());
        } else {
            lv11 = new TranslatableText("multiplayer.player.joined.renamed", player.getDisplayName(), string);
        }
        this.broadcastChatMessage(lv11.formatted(Formatting.YELLOW), MessageType.SYSTEM, Util.NIL_UUID);
        lv8.requestTeleport(player.getX(), player.getY(), player.getZ(), player.yaw, player.pitch);
        this.players.add(player);
        this.playerMap.put(player.getUuid(), player);
        this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, player));
        for (int i = 0; i < this.players.size(); ++i) {
            player.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, this.players.get(i)));
        }
        lv6.onPlayerConnected(player);
        this.server.getBossBarManager().onPlayerConnect(player);
        this.sendWorldInfo(player, lv6);
        if (!this.server.getResourcePackUrl().isEmpty()) {
            player.sendResourcePackUrl(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
        }
        for (StatusEffectInstance lv12 : player.getStatusEffects()) {
            lv8.sendPacket(new EntityStatusEffectS2CPacket(player.getEntityId(), lv12));
        }
        if (lv2 != null && lv2.contains("RootVehicle", 10) && (lv14 = EntityType.loadEntityWithPassengers((lv13 = lv2.getCompound("RootVehicle")).getCompound("Entity"), lv6, vehicle -> {
            if (!lv6.tryLoadEntity((Entity)vehicle)) {
                return null;
            }
            return vehicle;
        })) != null) {
            Object uUID2;
            if (lv13.containsUuid("Attach")) {
                UUID uUID = lv13.getUuid("Attach");
            } else {
                uUID2 = null;
            }
            if (lv14.getUuid().equals(uUID2)) {
                player.startRiding(lv14, true);
            } else {
                for (Entity lv15 : lv14.getPassengersDeep()) {
                    if (!lv15.getUuid().equals(uUID2)) continue;
                    player.startRiding(lv15, true);
                    break;
                }
            }
            if (!player.hasVehicle()) {
                LOGGER.warn("Couldn't reattach entity to player");
                lv6.removeEntity(lv14);
                for (Entity lv16 : lv14.getPassengersDeep()) {
                    lv6.removeEntity(lv16);
                }
            }
        }
        player.onSpawn();
    }

    protected void sendScoreboard(ServerScoreboard scoreboard, ServerPlayerEntity player) {
        HashSet set = Sets.newHashSet();
        for (Team lv : scoreboard.getTeams()) {
            player.networkHandler.sendPacket(new TeamS2CPacket(lv, 0));
        }
        for (int i = 0; i < 19; ++i) {
            ScoreboardObjective lv2 = scoreboard.getObjectiveForSlot(i);
            if (lv2 == null || set.contains(lv2)) continue;
            List<Packet<?>> list = scoreboard.createChangePackets(lv2);
            for (Packet<?> lv3 : list) {
                player.networkHandler.sendPacket(lv3);
            }
            set.add(lv2);
        }
    }

    public void setMainWorld(ServerWorld world) {
        world.getWorldBorder().addListener(new WorldBorderListener(){

            @Override
            public void onSizeChange(WorldBorder border, double size) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_SIZE));
            }

            @Override
            public void onInterpolateSize(WorldBorder border, double fromSize, double toSize, long time) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.LERP_SIZE));
            }

            @Override
            public void onCenterChanged(WorldBorder border, double centerX, double centerZ) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_CENTER));
            }

            @Override
            public void onWarningTimeChanged(WorldBorder border, int warningTime) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_WARNING_TIME));
            }

            @Override
            public void onWarningBlocksChanged(WorldBorder border, int warningBlockDistance) {
                PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_WARNING_BLOCKS));
            }

            @Override
            public void onDamagePerBlockChanged(WorldBorder border, double damagePerBlock) {
            }

            @Override
            public void onSafeZoneChanged(WorldBorder border, double safeZoneRadius) {
            }
        });
    }

    @Nullable
    public CompoundTag loadPlayerData(ServerPlayerEntity player) {
        CompoundTag lv3;
        CompoundTag lv = this.server.getSaveProperties().getPlayerData();
        if (player.getName().getString().equals(this.server.getUserName()) && lv != null) {
            CompoundTag lv2 = lv;
            player.fromTag(lv2);
            LOGGER.debug("loading single player");
        } else {
            lv3 = this.saveHandler.loadPlayerData(player);
        }
        return lv3;
    }

    protected void savePlayerData(ServerPlayerEntity player) {
        PlayerAdvancementTracker lv2;
        this.saveHandler.savePlayerData(player);
        ServerStatHandler lv = this.statisticsMap.get(player.getUuid());
        if (lv != null) {
            lv.save();
        }
        if ((lv2 = this.advancementTrackers.get(player.getUuid())) != null) {
            lv2.save();
        }
    }

    public void remove(ServerPlayerEntity player) {
        Entity lv2;
        ServerWorld lv = player.getServerWorld();
        player.incrementStat(Stats.LEAVE_GAME);
        this.savePlayerData(player);
        if (player.hasVehicle() && (lv2 = player.getRootVehicle()).hasPlayerRider()) {
            LOGGER.debug("Removing player mount");
            player.stopRiding();
            lv.removeEntity(lv2);
            lv2.removed = true;
            for (Entity lv3 : lv2.getPassengersDeep()) {
                lv.removeEntity(lv3);
                lv3.removed = true;
            }
            lv.getChunk(player.chunkX, player.chunkZ).markDirty();
        }
        player.detach();
        lv.removePlayer(player);
        player.getAdvancementTracker().clearCriteria();
        this.players.remove(player);
        this.server.getBossBarManager().onPlayerDisconnect(player);
        UUID uUID = player.getUuid();
        ServerPlayerEntity lv4 = this.playerMap.get(uUID);
        if (lv4 == player) {
            this.playerMap.remove(uUID);
            this.statisticsMap.remove(uUID);
            this.advancementTrackers.remove(uUID);
        }
        this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.REMOVE_PLAYER, player));
    }

    @Nullable
    public Text checkCanJoin(SocketAddress address, GameProfile profile) {
        if (this.bannedProfiles.contains(profile)) {
            BannedPlayerEntry lv = (BannedPlayerEntry)this.bannedProfiles.get(profile);
            TranslatableText lv2 = new TranslatableText("multiplayer.disconnect.banned.reason", lv.getReason());
            if (lv.getExpiryDate() != null) {
                lv2.append(new TranslatableText("multiplayer.disconnect.banned.expiration", DATE_FORMATTER.format(lv.getExpiryDate())));
            }
            return lv2;
        }
        if (!this.isWhitelisted(profile)) {
            return new TranslatableText("multiplayer.disconnect.not_whitelisted");
        }
        if (this.bannedIps.isBanned(address)) {
            BannedIpEntry lv3 = this.bannedIps.get(address);
            TranslatableText lv4 = new TranslatableText("multiplayer.disconnect.banned_ip.reason", lv3.getReason());
            if (lv3.getExpiryDate() != null) {
                lv4.append(new TranslatableText("multiplayer.disconnect.banned_ip.expiration", DATE_FORMATTER.format(lv3.getExpiryDate())));
            }
            return lv4;
        }
        if (this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(profile)) {
            return new TranslatableText("multiplayer.disconnect.server_full");
        }
        return null;
    }

    public ServerPlayerEntity createPlayer(GameProfile profile) {
        ServerPlayerInteractionManager lv6;
        UUID uUID = PlayerEntity.getUuidFromProfile(profile);
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayerEntity lv = this.players.get(i);
            if (!lv.getUuid().equals(uUID)) continue;
            list.add(lv);
        }
        ServerPlayerEntity lv2 = this.playerMap.get(profile.getId());
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
        return new ServerPlayerEntity(this.server, lv4, profile, lv6);
    }

    public ServerPlayerEntity respawnPlayer(ServerPlayerEntity player, boolean alive) {
        ServerPlayerInteractionManager lv5;
        ServerWorld lv3;
        Optional optional2;
        this.players.remove(player);
        player.getServerWorld().removePlayer(player);
        BlockPos lv = player.getSpawnPointPosition();
        boolean bl2 = player.isSpawnPointSet();
        ServerWorld lv2 = this.server.getWorld(player.getSpawnPointDimension());
        if (lv2 != null && lv != null) {
            Optional<Vec3d> optional = PlayerEntity.findRespawnPosition(lv2, lv, bl2, alive);
        } else {
            optional2 = Optional.empty();
        }
        ServerWorld serverWorld = lv3 = lv2 != null && optional2.isPresent() ? lv2 : this.server.getOverworld();
        if (this.server.isDemo()) {
            DemoServerPlayerInteractionManager lv4 = new DemoServerPlayerInteractionManager(lv3);
        } else {
            lv5 = new ServerPlayerInteractionManager(lv3);
        }
        ServerPlayerEntity lv6 = new ServerPlayerEntity(this.server, lv3, player.getGameProfile(), lv5);
        lv6.networkHandler = player.networkHandler;
        lv6.copyFrom(player, alive);
        lv6.setEntityId(player.getEntityId());
        lv6.setMainArm(player.getMainArm());
        for (String string : player.getScoreboardTags()) {
            lv6.addScoreboardTag(string);
        }
        this.setGameMode(lv6, player, lv3);
        boolean bl3 = false;
        if (optional2.isPresent()) {
            Vec3d lv7 = (Vec3d)optional2.get();
            lv6.refreshPositionAndAngles(lv7.x, lv7.y, lv7.z, 0.0f, 0.0f);
            lv6.setSpawnPoint(lv3.getRegistryKey(), lv, bl2, false);
            bl3 = !alive && lv3.getBlockState(lv).getBlock() instanceof RespawnAnchorBlock;
        } else if (lv != null) {
            lv6.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.NO_RESPAWN_BLOCK, 0.0f));
        }
        while (!lv3.doesNotCollide(lv6) && lv6.getY() < 256.0) {
            lv6.updatePosition(lv6.getX(), lv6.getY() + 1.0, lv6.getZ());
        }
        WorldProperties lv8 = lv6.world.getLevelProperties();
        lv6.networkHandler.sendPacket(new PlayerRespawnS2CPacket(lv6.world.getDimensionRegistryKey(), lv6.world.getRegistryKey(), BiomeAccess.hashSeed(lv6.getServerWorld().getSeed()), lv6.interactionManager.getGameMode(), lv6.interactionManager.method_30119(), lv6.getServerWorld().isDebugWorld(), lv6.getServerWorld().isFlat(), alive));
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

    public void sendCommandTree(ServerPlayerEntity player) {
        GameProfile gameProfile = player.getGameProfile();
        int i = this.server.getPermissionLevel(gameProfile);
        this.sendCommandTree(player, i);
    }

    public void updatePlayerLatency() {
        if (++this.latencyUpdateTimer > 600) {
            this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_LATENCY, this.players));
            this.latencyUpdateTimer = 0;
        }
    }

    public void sendToAll(Packet<?> packet) {
        for (int i = 0; i < this.players.size(); ++i) {
            this.players.get((int)i).networkHandler.sendPacket(packet);
        }
    }

    public void sendToDimension(Packet<?> packet, RegistryKey<World> dimension) {
        for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayerEntity lv = this.players.get(i);
            if (lv.world.getRegistryKey() != dimension) continue;
            lv.networkHandler.sendPacket(packet);
        }
    }

    public void sendToTeam(PlayerEntity source, Text message) {
        AbstractTeam lv = source.getScoreboardTeam();
        if (lv == null) {
            return;
        }
        Collection<String> collection = lv.getPlayerList();
        for (String string : collection) {
            ServerPlayerEntity lv2 = this.getPlayer(string);
            if (lv2 == null || lv2 == source) continue;
            lv2.sendSystemMessage(message, source.getUuid());
        }
    }

    public void sendToOtherTeams(PlayerEntity source, Text message) {
        AbstractTeam lv = source.getScoreboardTeam();
        if (lv == null) {
            this.broadcastChatMessage(message, MessageType.SYSTEM, source.getUuid());
            return;
        }
        for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayerEntity lv2 = this.players.get(i);
            if (lv2.getScoreboardTeam() == lv) continue;
            lv2.sendSystemMessage(message, source.getUuid());
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

    public void addToOperators(GameProfile profile) {
        this.ops.add(new OperatorEntry(profile, this.server.getOpPermissionLevel(), this.ops.isOp(profile)));
        ServerPlayerEntity lv = this.getPlayer(profile.getId());
        if (lv != null) {
            this.sendCommandTree(lv);
        }
    }

    public void removeFromOperators(GameProfile profile) {
        this.ops.remove(profile);
        ServerPlayerEntity lv = this.getPlayer(profile.getId());
        if (lv != null) {
            this.sendCommandTree(lv);
        }
    }

    private void sendCommandTree(ServerPlayerEntity player, int permissionLevel) {
        if (player.networkHandler != null) {
            byte d;
            if (permissionLevel <= 0) {
                int b = 24;
            } else if (permissionLevel >= 4) {
                int c = 28;
            } else {
                d = (byte)(24 + permissionLevel);
            }
            player.networkHandler.sendPacket(new EntityStatusS2CPacket(player, d));
        }
        this.server.getCommandManager().sendCommandTree(player);
    }

    public boolean isWhitelisted(GameProfile profile) {
        return !this.whitelistEnabled || this.ops.contains(profile) || this.whitelist.contains(profile);
    }

    public boolean isOperator(GameProfile profile) {
        return this.ops.contains(profile) || this.server.isHost(profile) && this.server.getSaveProperties().areCommandsAllowed() || this.cheatsAllowed;
    }

    @Nullable
    public ServerPlayerEntity getPlayer(String name) {
        for (ServerPlayerEntity lv : this.players) {
            if (!lv.getGameProfile().getName().equalsIgnoreCase(name)) continue;
            return lv;
        }
        return null;
    }

    public void sendToAround(@Nullable PlayerEntity player, double x, double y, double z, double distance, RegistryKey<World> worldKey, Packet<?> packet) {
        for (int i = 0; i < this.players.size(); ++i) {
            double k;
            double j;
            double h;
            ServerPlayerEntity lv = this.players.get(i);
            if (lv == player || lv.world.getRegistryKey() != worldKey || !((h = x - lv.getX()) * h + (j = y - lv.getY()) * j + (k = z - lv.getZ()) * k < distance * distance)) continue;
            lv.networkHandler.sendPacket(packet);
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

    public void sendWorldInfo(ServerPlayerEntity player, ServerWorld world) {
        WorldBorder lv = this.server.getOverworld().getWorldBorder();
        player.networkHandler.sendPacket(new WorldBorderS2CPacket(lv, WorldBorderS2CPacket.Type.INITIALIZE));
        player.networkHandler.sendPacket(new WorldTimeUpdateS2CPacket(world.getTime(), world.getTimeOfDay(), world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)));
        player.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(world.getSpawnPos()));
        if (world.isRaining()) {
            player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STARTED, 0.0f));
            player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED, world.getRainGradient(1.0f)));
            player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED, world.getThunderGradient(1.0f)));
        }
    }

    public void sendPlayerStatus(ServerPlayerEntity player) {
        player.openHandledScreen(player.playerScreenHandler);
        player.markHealthDirty();
        player.networkHandler.sendPacket(new HeldItemChangeS2CPacket(player.inventory.selectedSlot));
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

    public void setWhitelistEnabled(boolean whitelistEnabled) {
        this.whitelistEnabled = whitelistEnabled;
    }

    public List<ServerPlayerEntity> getPlayersByIp(String ip) {
        ArrayList list = Lists.newArrayList();
        for (ServerPlayerEntity lv : this.players) {
            if (!lv.getIp().equals(ip)) continue;
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
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    private void setGameMode(ServerPlayerEntity player, @Nullable ServerPlayerEntity oldPlayer, ServerWorld world) {
        if (oldPlayer != null) {
            player.interactionManager.setGameMode(oldPlayer.interactionManager.getGameMode(), oldPlayer.interactionManager.method_30119());
        } else if (this.gameMode != null) {
            player.interactionManager.setGameMode(this.gameMode, GameMode.NOT_SET);
        }
        player.interactionManager.setGameModeIfNotPresent(world.getServer().getSaveProperties().getGameMode());
    }

    @Environment(value=EnvType.CLIENT)
    public void setCheatsAllowed(boolean cheatsAllowed) {
        this.cheatsAllowed = cheatsAllowed;
    }

    public void disconnectAllPlayers() {
        for (int i = 0; i < this.players.size(); ++i) {
            this.players.get((int)i).networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.server_shutdown"));
        }
    }

    public void broadcastChatMessage(Text message, MessageType type, UUID senderUuid) {
        this.server.sendSystemMessage(message, senderUuid);
        this.sendToAll(new GameMessageS2CPacket(message, type, senderUuid));
    }

    public ServerStatHandler createStatHandler(PlayerEntity player) {
        ServerStatHandler lv;
        UUID uUID = player.getUuid();
        ServerStatHandler serverStatHandler = lv = uUID == null ? null : this.statisticsMap.get(uUID);
        if (lv == null) {
            File file3;
            File file = this.server.getSavePath(WorldSavePath.STATS).toFile();
            File file2 = new File(file, uUID + ".json");
            if (!file2.exists() && (file3 = new File(file, player.getName().getString() + ".json")).exists() && file3.isFile()) {
                file3.renameTo(file2);
            }
            lv = new ServerStatHandler(this.server, file2);
            this.statisticsMap.put(uUID, lv);
        }
        return lv;
    }

    public PlayerAdvancementTracker getAdvancementTracker(ServerPlayerEntity player) {
        UUID uUID = player.getUuid();
        PlayerAdvancementTracker lv = this.advancementTrackers.get(uUID);
        if (lv == null) {
            File file = this.server.getSavePath(WorldSavePath.ADVANCEMENTS).toFile();
            File file2 = new File(file, uUID + ".json");
            lv = new PlayerAdvancementTracker(this.server.getDataFixer(), this, this.server.getAdvancementLoader(), file2, player);
            this.advancementTrackers.put(uUID, lv);
        }
        lv.setOwner(player);
        return lv;
    }

    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
        this.sendToAll(new ChunkLoadDistanceS2CPacket(viewDistance));
        for (ServerWorld lv : this.server.getWorlds()) {
            if (lv == null) continue;
            lv.getChunkManager().applyViewDistance(viewDistance);
        }
    }

    public List<ServerPlayerEntity> getPlayerList() {
        return this.players;
    }

    @Nullable
    public ServerPlayerEntity getPlayer(UUID uuid) {
        return this.playerMap.get(uuid);
    }

    public boolean canBypassPlayerLimit(GameProfile profile) {
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

