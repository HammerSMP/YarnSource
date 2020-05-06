/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.authlib.GameProfile
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
import net.minecraft.class_5217;
import net.minecraft.class_5218;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSaveHandler;
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
    protected final int maxPlayers;
    private int viewDistance;
    private GameMode gameMode;
    private boolean cheatsAllowed;
    private int latencyUpdateTimer;

    public PlayerManager(MinecraftServer minecraftServer, WorldSaveHandler arg, int i) {
        this.server = minecraftServer;
        this.maxPlayers = i;
        this.saveHandler = arg;
        this.getUserBanList().setEnabled(true);
        this.getIpBanList().setEnabled(true);
    }

    public void onPlayerConnect(ClientConnection arg, ServerPlayerEntity arg22) {
        CompoundTag lv10;
        Entity lv11;
        TranslatableText lv8;
        GameProfile gameProfile = arg22.getGameProfile();
        UserCache lv = this.server.getUserCache();
        GameProfile gameProfile2 = lv.getByUuid(gameProfile.getId());
        String string = gameProfile2 == null ? gameProfile.getName() : gameProfile2.getName();
        lv.add(gameProfile);
        CompoundTag lv2 = this.loadPlayerData(arg22);
        ServerWorld lv3 = this.server.getWorld(arg22.dimension);
        arg22.setWorld(lv3);
        arg22.interactionManager.setWorld((ServerWorld)arg22.world);
        String string2 = "local";
        if (arg.getAddress() != null) {
            string2 = arg.getAddress().toString();
        }
        LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", (Object)arg22.getName().getString(), (Object)string2, (Object)arg22.getEntityId(), (Object)arg22.getX(), (Object)arg22.getY(), (Object)arg22.getZ());
        class_5217 lv4 = lv3.getLevelProperties();
        this.setGameMode(arg22, null, lv3);
        ServerPlayNetworkHandler lv5 = new ServerPlayNetworkHandler(this.server, arg, arg22);
        GameRules lv6 = lv3.getGameRules();
        boolean bl = lv6.getBoolean(GameRules.DO_IMMEDIATE_RESPAWN);
        boolean bl2 = lv6.getBoolean(GameRules.REDUCED_DEBUG_INFO);
        lv5.sendPacket(new GameJoinS2CPacket(arg22.getEntityId(), arg22.interactionManager.getGameMode(), class_5217.method_27418(lv4.getSeed()), lv4.isHardcore(), lv3.dimension.getType(), this.getMaxPlayerCount(), lv4.getGeneratorType(), this.viewDistance, bl2, !bl));
        lv5.sendPacket(new CustomPayloadS2CPacket(CustomPayloadS2CPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(this.getServer().getServerModName())));
        lv5.sendPacket(new DifficultyS2CPacket(lv4.getDifficulty(), lv4.isDifficultyLocked()));
        lv5.sendPacket(new PlayerAbilitiesS2CPacket(arg22.abilities));
        lv5.sendPacket(new HeldItemChangeS2CPacket(arg22.inventory.selectedSlot));
        lv5.sendPacket(new SynchronizeRecipesS2CPacket(this.server.getRecipeManager().values()));
        lv5.sendPacket(new SynchronizeTagsS2CPacket(this.server.getTagManager()));
        this.sendCommandTree(arg22);
        arg22.getStatHandler().updateStatSet();
        arg22.getRecipeBook().sendInitRecipesPacket(arg22);
        this.sendScoreboard(lv3.getScoreboard(), arg22);
        this.server.forcePlayerSampleUpdate();
        if (arg22.getGameProfile().getName().equalsIgnoreCase(string)) {
            TranslatableText lv7 = new TranslatableText("multiplayer.player.joined", arg22.getDisplayName());
        } else {
            lv8 = new TranslatableText("multiplayer.player.joined.renamed", arg22.getDisplayName(), string);
        }
        this.sendToAll(lv8.formatted(Formatting.YELLOW));
        lv5.requestTeleport(arg22.getX(), arg22.getY(), arg22.getZ(), arg22.yaw, arg22.pitch);
        this.players.add(arg22);
        this.playerMap.put(arg22.getUuid(), arg22);
        this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, arg22));
        for (int i = 0; i < this.players.size(); ++i) {
            arg22.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, this.players.get(i)));
        }
        lv3.onPlayerConnected(arg22);
        this.server.getBossBarManager().onPlayerConnect(arg22);
        this.sendWorldInfo(arg22, lv3);
        if (!this.server.getResourcePackUrl().isEmpty()) {
            arg22.sendResourcePackUrl(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
        }
        for (StatusEffectInstance lv9 : arg22.getStatusEffects()) {
            lv5.sendPacket(new EntityStatusEffectS2CPacket(arg22.getEntityId(), lv9));
        }
        if (lv2 != null && lv2.contains("RootVehicle", 10) && (lv11 = EntityType.loadEntityWithPassengers((lv10 = lv2.getCompound("RootVehicle")).getCompound("Entity"), lv3, arg2 -> {
            if (!lv3.tryLoadEntity((Entity)arg2)) {
                return null;
            }
            return arg2;
        })) != null) {
            Object uUID2;
            if (lv10.containsUuidNew("Attach")) {
                UUID uUID = lv10.getUuidNew("Attach");
            } else {
                uUID2 = null;
            }
            if (lv11.getUuid().equals(uUID2)) {
                arg22.startRiding(lv11, true);
            } else {
                for (Entity lv12 : lv11.getPassengersDeep()) {
                    if (!lv12.getUuid().equals(uUID2)) continue;
                    arg22.startRiding(lv12, true);
                    break;
                }
            }
            if (!arg22.hasVehicle()) {
                LOGGER.warn("Couldn't reattach entity to player");
                lv3.removeEntity(lv11);
                for (Entity lv13 : lv11.getPassengersDeep()) {
                    lv3.removeEntity(lv13);
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
        CompoundTag lv = this.server.method_27728().getPlayerData();
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
        this.server.getBossBarManager().onPlayerDisconnenct(arg);
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
        ServerPlayerInteractionManager lv5;
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
        if (this.server.isDemo()) {
            DemoServerPlayerInteractionManager lv4 = new DemoServerPlayerInteractionManager(this.server.getWorld(DimensionType.OVERWORLD));
        } else {
            lv5 = new ServerPlayerInteractionManager(this.server.getWorld(DimensionType.OVERWORLD));
        }
        return new ServerPlayerEntity(this.server, this.server.getWorld(DimensionType.OVERWORLD), gameProfile, lv5);
    }

    public ServerPlayerEntity respawnPlayer(ServerPlayerEntity arg, boolean bl) {
        ServerPlayerInteractionManager lv3;
        Optional optional2;
        this.players.remove(arg);
        arg.getServerWorld().removePlayer(arg);
        BlockPos lv = arg.getSpawnPointPosition();
        boolean bl2 = arg.isSpawnPointSet();
        if (lv != null) {
            Optional<Vec3d> optional = PlayerEntity.findRespawnPosition(this.server.getWorld(arg.getSpawnPointDimension()), lv, bl2, bl);
        } else {
            optional2 = Optional.empty();
        }
        DimensionType dimensionType = arg.dimension = optional2.isPresent() ? arg.getSpawnPointDimension() : DimensionType.OVERWORLD;
        if (this.server.isDemo()) {
            DemoServerPlayerInteractionManager lv2 = new DemoServerPlayerInteractionManager(this.server.getWorld(arg.dimension));
        } else {
            lv3 = new ServerPlayerInteractionManager(this.server.getWorld(arg.dimension));
        }
        ServerPlayerEntity lv4 = new ServerPlayerEntity(this.server, this.server.getWorld(arg.dimension), arg.getGameProfile(), lv3);
        lv4.networkHandler = arg.networkHandler;
        lv4.copyFrom(arg, bl);
        lv4.setEntityId(arg.getEntityId());
        lv4.setMainArm(arg.getMainArm());
        for (String string : arg.getScoreboardTags()) {
            lv4.addScoreboardTag(string);
        }
        ServerWorld lv5 = this.server.getWorld(arg.dimension);
        this.setGameMode(lv4, arg, lv5);
        boolean bl3 = false;
        if (optional2.isPresent()) {
            Vec3d lv6 = (Vec3d)optional2.get();
            lv4.refreshPositionAndAngles(lv6.x, lv6.y, lv6.z, 0.0f, 0.0f);
            lv4.setSpawnPoint(arg.dimension, lv, bl2, false);
            bl3 = !bl && lv5.getBlockState(lv).getBlock() instanceof RespawnAnchorBlock;
        } else if (lv != null) {
            lv4.networkHandler.sendPacket(new GameStateChangeS2CPacket(0, 0.0f));
        }
        while (!lv5.doesNotCollide(lv4) && lv4.getY() < 256.0) {
            lv4.updatePosition(lv4.getX(), lv4.getY() + 1.0, lv4.getZ());
        }
        class_5217 lv7 = lv4.world.getLevelProperties();
        lv4.networkHandler.sendPacket(new PlayerRespawnS2CPacket(lv4.dimension, class_5217.method_27418(lv7.getSeed()), lv7.getGeneratorType(), lv4.interactionManager.getGameMode(), bl));
        lv4.networkHandler.requestTeleport(lv4.getX(), lv4.getY(), lv4.getZ(), lv4.yaw, lv4.pitch);
        lv4.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(lv5.method_27911()));
        lv4.networkHandler.sendPacket(new DifficultyS2CPacket(lv7.getDifficulty(), lv7.isDifficultyLocked()));
        lv4.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(lv4.experienceProgress, lv4.totalExperience, lv4.experienceLevel));
        this.sendWorldInfo(lv4, lv5);
        this.sendCommandTree(lv4);
        lv5.onPlayerRespawned(lv4);
        this.players.add(lv4);
        this.playerMap.put(lv4.getUuid(), lv4);
        lv4.onSpawn();
        lv4.setHealth(lv4.getHealth());
        if (bl3) {
            lv4.networkHandler.sendPacket(new PlaySoundS2CPacket(SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, lv.getX(), lv.getY(), lv.getZ(), 1.0f, 1.0f));
        }
        return lv4;
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

    public void sendToDimension(Packet<?> arg, DimensionType arg2) {
        for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayerEntity lv = this.players.get(i);
            if (lv.dimension != arg2) continue;
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
            lv2.sendSystemMessage(arg2);
        }
    }

    public void sendToOtherTeams(PlayerEntity arg, Text arg2) {
        AbstractTeam lv = arg.getScoreboardTeam();
        if (lv == null) {
            this.sendToAll(arg2);
            return;
        }
        for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayerEntity lv2 = this.players.get(i);
            if (lv2.getScoreboardTeam() == lv) continue;
            lv2.sendSystemMessage(arg2);
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
        return this.ops.contains(gameProfile) || this.server.isHost(gameProfile) && this.server.method_27728().areCommandsAllowed() || this.cheatsAllowed;
    }

    @Nullable
    public ServerPlayerEntity getPlayer(String string) {
        for (ServerPlayerEntity lv : this.players) {
            if (!lv.getGameProfile().getName().equalsIgnoreCase(string)) continue;
            return lv;
        }
        return null;
    }

    public void sendToAround(@Nullable PlayerEntity arg, double d, double e, double f, double g, DimensionType arg2, Packet<?> arg3) {
        for (int i = 0; i < this.players.size(); ++i) {
            double k;
            double j;
            double h;
            ServerPlayerEntity lv = this.players.get(i);
            if (lv == arg || lv.dimension != arg2 || !((h = d - lv.getX()) * h + (j = e - lv.getY()) * j + (k = f - lv.getZ()) * k < g * g)) continue;
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
        WorldBorder lv = this.server.getWorld(DimensionType.OVERWORLD).getWorldBorder();
        arg.networkHandler.sendPacket(new WorldBorderS2CPacket(lv, WorldBorderS2CPacket.Type.INITIALIZE));
        arg.networkHandler.sendPacket(new WorldTimeUpdateS2CPacket(arg2.getTime(), arg2.getTimeOfDay(), arg2.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)));
        arg.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(arg2.method_27911()));
        if (arg2.isRaining()) {
            arg.networkHandler.sendPacket(new GameStateChangeS2CPacket(1, 0.0f));
            arg.networkHandler.sendPacket(new GameStateChangeS2CPacket(7, arg2.getRainGradient(1.0f)));
            arg.networkHandler.sendPacket(new GameStateChangeS2CPacket(8, arg2.getThunderGradient(1.0f)));
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

    private void setGameMode(ServerPlayerEntity arg, ServerPlayerEntity arg2, ServerWorld arg3) {
        if (arg2 != null) {
            arg.interactionManager.setGameMode(arg2.interactionManager.getGameMode());
        } else if (this.gameMode != null) {
            arg.interactionManager.setGameMode(this.gameMode);
        }
        arg.interactionManager.setGameModeIfNotPresent(arg3.getServer().method_27728().getGameMode());
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

    public void broadcastChatMessage(Text arg, boolean bl) {
        this.server.sendSystemMessage(arg);
        MessageType lv = bl ? MessageType.SYSTEM : MessageType.CHAT;
        this.sendToAll(new GameMessageS2CPacket(arg, lv));
    }

    public void sendToAll(Text arg) {
        this.broadcastChatMessage(arg, true);
    }

    public ServerStatHandler createStatHandler(PlayerEntity arg) {
        ServerStatHandler lv;
        UUID uUID = arg.getUuid();
        ServerStatHandler serverStatHandler = lv = uUID == null ? null : this.statisticsMap.get(uUID);
        if (lv == null) {
            File file3;
            File file = this.server.method_27050(class_5218.STATS).toFile();
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
            File file = this.server.method_27050(class_5218.ADVANCEMENTS).toFile();
            File file2 = new File(file, uUID + ".json");
            lv = new PlayerAdvancementTracker(this.server, file2, arg);
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
            lv.reload();
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

