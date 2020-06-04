/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.longs.LongSets
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.class_5362;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Npc;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.raid.RaidManager;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.StructureManager;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.collection.TypeFilterableList;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.CsvWriter;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.ForcedChunkState;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IdCountsState;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerWorld
extends World
implements ServerWorldAccess {
    public static final BlockPos field_25144 = new BlockPos(100, 50, 0);
    private static final Logger LOGGER = LogManager.getLogger();
    private final Int2ObjectMap<Entity> entitiesById = new Int2ObjectLinkedOpenHashMap();
    private final Map<UUID, Entity> entitiesByUuid = Maps.newHashMap();
    private final Queue<Entity> entitiesToLoad = Queues.newArrayDeque();
    private final List<ServerPlayerEntity> players = Lists.newArrayList();
    private final ServerChunkManager serverChunkManager;
    boolean inEntityTick;
    private final MinecraftServer server;
    private final ServerWorldProperties field_24456;
    public boolean savingDisabled;
    private boolean allPlayersSleeping;
    private int idleTimeout;
    private final PortalForcer portalForcer;
    private final ServerTickScheduler<Block> blockTickScheduler = new ServerTickScheduler<Block>(this, arg -> arg == null || arg.getDefaultState().isAir(), Registry.BLOCK::getId, this::tickBlock);
    private final ServerTickScheduler<Fluid> fluidTickScheduler = new ServerTickScheduler<Fluid>(this, arg -> arg == null || arg == Fluids.EMPTY, Registry.FLUID::getId, this::tickFluid);
    private final Set<EntityNavigation> entityNavigations = Sets.newHashSet();
    protected final RaidManager raidManager;
    private final ObjectLinkedOpenHashSet<BlockEvent> syncedBlockEventQueue = new ObjectLinkedOpenHashSet();
    private boolean inBlockTick;
    private final List<Spawner> field_25141;
    @Nullable
    private final EnderDragonFight enderDragonFight;
    private final StructureAccessor structureAccessor;
    private final boolean field_25143;

    public ServerWorld(MinecraftServer minecraftServer, Executor executor, LevelStorage.Session arg2, ServerWorldProperties arg22, RegistryKey<World> arg3, RegistryKey<DimensionType> arg4, DimensionType arg5, WorldGenerationProgressListener arg6, ChunkGenerator arg7, boolean bl, long l, List<Spawner> list, boolean bl2) {
        super(arg22, arg3, arg4, arg5, minecraftServer::getProfiler, false, bl, l);
        this.field_25143 = bl2;
        this.server = minecraftServer;
        this.field_25141 = list;
        this.field_24456 = arg22;
        this.serverChunkManager = new ServerChunkManager(this, arg2, minecraftServer.getDataFixer(), minecraftServer.getStructureManager(), executor, arg7, minecraftServer.getPlayerManager().getViewDistance(), minecraftServer.syncChunkWrites(), arg6, () -> minecraftServer.getWorld(World.OVERWORLD).getPersistentStateManager());
        this.portalForcer = new PortalForcer(this);
        this.calculateAmbientDarkness();
        this.initWeatherGradients();
        this.getWorldBorder().setMaxWorldBorderRadius(minecraftServer.getMaxWorldBorderRadius());
        this.raidManager = this.getPersistentStateManager().getOrCreate(() -> new RaidManager(this), RaidManager.nameFor(this.getDimension()));
        if (!minecraftServer.isSinglePlayer()) {
            arg22.setGameMode(minecraftServer.getDefaultGameMode());
        }
        this.structureAccessor = new StructureAccessor(this, minecraftServer.getSaveProperties().getGeneratorOptions());
        this.enderDragonFight = this.getDimension().hasEnderDragonFight() ? new EnderDragonFight(this, minecraftServer.getSaveProperties().getGeneratorOptions().getSeed(), minecraftServer.getSaveProperties().method_29036()) : null;
    }

    public void method_27910(int i, int j, boolean bl, boolean bl2) {
        this.field_24456.setClearWeatherTime(i);
        this.field_24456.setRainTime(j);
        this.field_24456.setThunderTime(j);
        this.field_24456.setRaining(bl);
        this.field_24456.setThundering(bl2);
    }

    @Override
    public Biome getGeneratorStoredBiome(int i, int j, int k) {
        return this.getChunkManager().getChunkGenerator().getBiomeSource().getBiomeForNoiseGen(i, j, k);
    }

    public StructureAccessor getStructureAccessor() {
        return this.structureAccessor;
    }

    public void tick(BooleanSupplier booleanSupplier) {
        boolean bl4;
        Profiler lv = this.getProfiler();
        this.inBlockTick = true;
        lv.push("world border");
        this.getWorldBorder().tick();
        lv.swap("weather");
        boolean bl = this.isRaining();
        if (this.getDimension().hasSkyLight()) {
            if (this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
                int i = this.field_24456.getClearWeatherTime();
                int j = this.field_24456.getThunderTime();
                int k = this.field_24456.getRainTime();
                boolean bl2 = this.properties.isThundering();
                boolean bl3 = this.properties.isRaining();
                if (i > 0) {
                    --i;
                    j = bl2 ? 0 : 1;
                    k = bl3 ? 0 : 1;
                    bl2 = false;
                    bl3 = false;
                } else {
                    if (j > 0) {
                        if (--j == 0) {
                            bl2 = !bl2;
                        }
                    } else {
                        j = bl2 ? this.random.nextInt(12000) + 3600 : this.random.nextInt(168000) + 12000;
                    }
                    if (k > 0) {
                        if (--k == 0) {
                            bl3 = !bl3;
                        }
                    } else {
                        k = bl3 ? this.random.nextInt(12000) + 12000 : this.random.nextInt(168000) + 12000;
                    }
                }
                this.field_24456.setThunderTime(j);
                this.field_24456.setRainTime(k);
                this.field_24456.setClearWeatherTime(i);
                this.field_24456.setThundering(bl2);
                this.field_24456.setRaining(bl3);
            }
            this.thunderGradientPrev = this.thunderGradient;
            this.thunderGradient = this.properties.isThundering() ? (float)((double)this.thunderGradient + 0.01) : (float)((double)this.thunderGradient - 0.01);
            this.thunderGradient = MathHelper.clamp(this.thunderGradient, 0.0f, 1.0f);
            this.rainGradientPrev = this.rainGradient;
            this.rainGradient = this.properties.isRaining() ? (float)((double)this.rainGradient + 0.01) : (float)((double)this.rainGradient - 0.01);
            this.rainGradient = MathHelper.clamp(this.rainGradient, 0.0f, 1.0f);
        }
        if (this.rainGradientPrev != this.rainGradient) {
            this.server.getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(7, this.rainGradient), this.getRegistryKey());
        }
        if (this.thunderGradientPrev != this.thunderGradient) {
            this.server.getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(8, this.thunderGradient), this.getRegistryKey());
        }
        if (bl != this.isRaining()) {
            if (bl) {
                this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(2, 0.0f));
            } else {
                this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(1, 0.0f));
            }
            this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(7, this.rainGradient));
            this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(8, this.thunderGradient));
        }
        if (this.allPlayersSleeping && this.players.stream().noneMatch(arg -> !arg.isSpectator() && !arg.isSleepingLongEnough())) {
            this.allPlayersSleeping = false;
            if (this.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
                long l = this.properties.getTimeOfDay() + 24000L;
                this.method_29199(l - l % 24000L);
            }
            this.wakeSleepingPlayers();
            if (this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
                this.resetWeather();
            }
        }
        this.calculateAmbientDarkness();
        this.method_29203();
        lv.swap("chunkSource");
        this.getChunkManager().tick(booleanSupplier);
        lv.swap("tickPending");
        if (!this.isDebugWorld()) {
            this.blockTickScheduler.tick();
            this.fluidTickScheduler.tick();
        }
        lv.swap("raid");
        this.raidManager.tick();
        lv.swap("blockEvents");
        this.processSyncedBlockEvents();
        this.inBlockTick = false;
        lv.swap("entities");
        boolean bl2 = bl4 = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();
        if (bl4) {
            this.resetIdleTimeout();
        }
        if (bl4 || this.idleTimeout++ < 300) {
            Entity lv4;
            if (this.enderDragonFight != null) {
                this.enderDragonFight.tick();
            }
            this.inEntityTick = true;
            ObjectIterator objectIterator = this.entitiesById.int2ObjectEntrySet().iterator();
            while (objectIterator.hasNext()) {
                Int2ObjectMap.Entry entry = (Int2ObjectMap.Entry)objectIterator.next();
                Entity lv2 = (Entity)entry.getValue();
                Entity lv3 = lv2.getVehicle();
                if (!this.server.shouldSpawnAnimals() && (lv2 instanceof AnimalEntity || lv2 instanceof WaterCreatureEntity)) {
                    lv2.remove();
                }
                if (!this.server.shouldSpawnNpcs() && lv2 instanceof Npc) {
                    lv2.remove();
                }
                lv.push("checkDespawn");
                if (!lv2.removed) {
                    lv2.checkDespawn();
                }
                lv.pop();
                if (lv3 != null) {
                    if (!lv3.removed && lv3.hasPassenger(lv2)) continue;
                    lv2.stopRiding();
                }
                lv.push("tick");
                if (!lv2.removed && !(lv2 instanceof EnderDragonPart)) {
                    this.tickEntity(this::tickEntity, lv2);
                }
                lv.pop();
                lv.push("remove");
                if (lv2.removed) {
                    this.removeEntityFromChunk(lv2);
                    objectIterator.remove();
                    this.unloadEntity(lv2);
                }
                lv.pop();
            }
            this.inEntityTick = false;
            while ((lv4 = this.entitiesToLoad.poll()) != null) {
                this.loadEntityUnchecked(lv4);
            }
            this.tickBlockEntities();
        }
        lv.pop();
    }

    protected void method_29203() {
        if (!this.field_25143) {
            return;
        }
        long l = this.properties.getTime() + 1L;
        this.field_24456.method_29034(l);
        this.field_24456.getScheduledEvents().processEvents(this.server, l);
        if (this.properties.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            this.method_29199(this.properties.getTimeOfDay() + 1L);
        }
    }

    public void method_29199(long l) {
        this.field_24456.method_29035(l);
    }

    public void method_29202(boolean bl, boolean bl2) {
        for (Spawner lv : this.field_25141) {
            lv.spawn(this, bl, bl2);
        }
    }

    private void wakeSleepingPlayers() {
        this.players.stream().filter(LivingEntity::isSleeping).collect(Collectors.toList()).forEach(arg -> arg.wakeUp(false, false));
    }

    public void tickChunk(WorldChunk arg, int i) {
        BlockPos lv3;
        ChunkPos lv = arg.getPos();
        boolean bl = this.isRaining();
        int j = lv.getStartX();
        int k = lv.getStartZ();
        Profiler lv2 = this.getProfiler();
        lv2.push("thunder");
        if (bl && this.isThundering() && this.random.nextInt(100000) == 0 && this.hasRain(lv3 = this.getSurface(this.getRandomPosInChunk(j, 0, k, 15)))) {
            boolean bl2;
            LocalDifficulty lv4 = this.getLocalDifficulty(lv3);
            boolean bl3 = bl2 = this.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && this.random.nextDouble() < (double)lv4.getLocalDifficulty() * 0.01;
            if (bl2) {
                SkeletonHorseEntity lv5 = EntityType.SKELETON_HORSE.create(this);
                lv5.setTrapped(true);
                lv5.setBreedingAge(0);
                lv5.updatePosition(lv3.getX(), lv3.getY(), lv3.getZ());
                this.spawnEntity(lv5);
            }
            LightningEntity lv6 = EntityType.LIGHTNING_BOLT.create(this);
            lv6.method_29495(Vec3d.ofBottomCenter(lv3));
            lv6.method_29498(bl2);
            this.spawnEntity(lv6);
        }
        lv2.swap("iceandsnow");
        if (this.random.nextInt(16) == 0) {
            BlockPos lv7 = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, this.getRandomPosInChunk(j, 0, k, 15));
            BlockPos lv8 = lv7.down();
            Biome lv9 = this.getBiome(lv7);
            if (lv9.canSetIce(this, lv8)) {
                this.setBlockState(lv8, Blocks.ICE.getDefaultState());
            }
            if (bl && lv9.canSetSnow(this, lv7)) {
                this.setBlockState(lv7, Blocks.SNOW.getDefaultState());
            }
            if (bl && this.getBiome(lv8).getPrecipitation() == Biome.Precipitation.RAIN) {
                this.getBlockState(lv8).getBlock().rainTick(this, lv8);
            }
        }
        lv2.swap("tickBlocks");
        if (i > 0) {
            for (ChunkSection lv10 : arg.getSectionArray()) {
                if (lv10 == WorldChunk.EMPTY_SECTION || !lv10.hasRandomTicks()) continue;
                int l = lv10.getYOffset();
                for (int m = 0; m < i; ++m) {
                    FluidState lv13;
                    BlockPos lv11 = this.getRandomPosInChunk(j, l, k, 15);
                    lv2.push("randomTick");
                    BlockState lv12 = lv10.getBlockState(lv11.getX() - j, lv11.getY() - l, lv11.getZ() - k);
                    if (lv12.hasRandomTicks()) {
                        lv12.randomTick(this, lv11, this.random);
                    }
                    if ((lv13 = lv12.getFluidState()).hasRandomTicks()) {
                        lv13.onRandomTick(this, lv11, this.random);
                    }
                    lv2.pop();
                }
            }
        }
        lv2.pop();
    }

    protected BlockPos getSurface(BlockPos arg2) {
        BlockPos lv = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, arg2);
        Box lv2 = new Box(lv, new BlockPos(lv.getX(), this.getHeight(), lv.getZ())).expand(3.0);
        List<LivingEntity> list = this.getEntities(LivingEntity.class, lv2, (? super T arg) -> arg != null && arg.isAlive() && this.isSkyVisible(arg.getBlockPos()));
        if (!list.isEmpty()) {
            return list.get(this.random.nextInt(list.size())).getBlockPos();
        }
        if (lv.getY() == -1) {
            lv = lv.up(2);
        }
        return lv;
    }

    public boolean isInBlockTick() {
        return this.inBlockTick;
    }

    public void updateSleepingPlayers() {
        this.allPlayersSleeping = false;
        if (!this.players.isEmpty()) {
            int i = 0;
            int j = 0;
            for (ServerPlayerEntity lv : this.players) {
                if (lv.isSpectator()) {
                    ++i;
                    continue;
                }
                if (!lv.isSleeping()) continue;
                ++j;
            }
            this.allPlayersSleeping = j > 0 && j >= this.players.size() - i;
        }
    }

    @Override
    public ServerScoreboard getScoreboard() {
        return this.server.getScoreboard();
    }

    private void resetWeather() {
        this.field_24456.setRainTime(0);
        this.field_24456.setRaining(false);
        this.field_24456.setThunderTime(0);
        this.field_24456.setThundering(false);
    }

    public void resetIdleTimeout() {
        this.idleTimeout = 0;
    }

    private void tickFluid(ScheduledTick<Fluid> arg) {
        FluidState lv = this.getFluidState(arg.pos);
        if (lv.getFluid() == arg.getObject()) {
            lv.onScheduledTick(this, arg.pos);
        }
    }

    private void tickBlock(ScheduledTick<Block> arg) {
        BlockState lv = this.getBlockState(arg.pos);
        if (lv.isOf(arg.getObject())) {
            lv.scheduledTick(this, arg.pos, this.random);
        }
    }

    public void tickEntity(Entity arg) {
        if (!(arg instanceof PlayerEntity) && !this.getChunkManager().shouldTickEntity(arg)) {
            this.checkChunk(arg);
            return;
        }
        arg.resetPosition(arg.getX(), arg.getY(), arg.getZ());
        arg.prevYaw = arg.yaw;
        arg.prevPitch = arg.pitch;
        if (arg.updateNeeded) {
            ++arg.age;
            Profiler lv = this.getProfiler();
            lv.push(() -> Registry.ENTITY_TYPE.getId(arg.getType()).toString());
            lv.visit("tickNonPassenger");
            arg.tick();
            lv.pop();
        }
        this.checkChunk(arg);
        if (arg.updateNeeded) {
            for (Entity lv2 : arg.getPassengerList()) {
                this.tickPassenger(arg, lv2);
            }
        }
    }

    public void tickPassenger(Entity arg, Entity arg2) {
        if (arg2.removed || arg2.getVehicle() != arg) {
            arg2.stopRiding();
            return;
        }
        if (!(arg2 instanceof PlayerEntity) && !this.getChunkManager().shouldTickEntity(arg2)) {
            return;
        }
        arg2.resetPosition(arg2.getX(), arg2.getY(), arg2.getZ());
        arg2.prevYaw = arg2.yaw;
        arg2.prevPitch = arg2.pitch;
        if (arg2.updateNeeded) {
            ++arg2.age;
            Profiler lv = this.getProfiler();
            lv.push(() -> Registry.ENTITY_TYPE.getId(arg2.getType()).toString());
            lv.visit("tickPassenger");
            arg2.tickRiding();
            lv.pop();
        }
        this.checkChunk(arg2);
        if (arg2.updateNeeded) {
            for (Entity lv2 : arg2.getPassengerList()) {
                this.tickPassenger(arg2, lv2);
            }
        }
    }

    public void checkChunk(Entity arg) {
        if (!arg.method_29240()) {
            return;
        }
        this.getProfiler().push("chunkCheck");
        int i = MathHelper.floor(arg.getX() / 16.0);
        int j = MathHelper.floor(arg.getY() / 16.0);
        int k = MathHelper.floor(arg.getZ() / 16.0);
        if (!arg.updateNeeded || arg.chunkX != i || arg.chunkY != j || arg.chunkZ != k) {
            if (arg.updateNeeded && this.isChunkLoaded(arg.chunkX, arg.chunkZ)) {
                this.getChunk(arg.chunkX, arg.chunkZ).remove(arg, arg.chunkY);
            }
            if (arg.teleportRequested() || this.isChunkLoaded(i, k)) {
                this.getChunk(i, k).addEntity(arg);
            } else {
                if (arg.updateNeeded) {
                    LOGGER.warn("Entity {} left loaded chunk area", (Object)arg);
                }
                arg.updateNeeded = false;
            }
        }
        this.getProfiler().pop();
    }

    @Override
    public boolean canPlayerModifyAt(PlayerEntity arg, BlockPos arg2) {
        return !this.server.isSpawnProtected(this, arg2, arg) && this.getWorldBorder().contains(arg2);
    }

    public void save(@Nullable ProgressListener arg, boolean bl, boolean bl2) {
        ServerChunkManager lv = this.getChunkManager();
        if (bl2) {
            return;
        }
        if (arg != null) {
            arg.method_15412(new TranslatableText("menu.savingLevel"));
        }
        this.saveLevel();
        if (arg != null) {
            arg.method_15414(new TranslatableText("menu.savingChunks"));
        }
        lv.save(bl);
    }

    private void saveLevel() {
        if (this.enderDragonFight != null) {
            this.server.getSaveProperties().method_29037(this.enderDragonFight.toTag());
        }
        this.getChunkManager().getPersistentStateManager().save();
    }

    public List<Entity> getEntities(@Nullable EntityType<?> arg, Predicate<? super Entity> predicate) {
        ArrayList list = Lists.newArrayList();
        ServerChunkManager lv = this.getChunkManager();
        for (Entity lv2 : this.entitiesById.values()) {
            if (arg != null && lv2.getType() != arg || !lv.isChunkLoaded(MathHelper.floor(lv2.getX()) >> 4, MathHelper.floor(lv2.getZ()) >> 4) || !predicate.test(lv2)) continue;
            list.add(lv2);
        }
        return list;
    }

    public List<EnderDragonEntity> getAliveEnderDragons() {
        ArrayList list = Lists.newArrayList();
        for (Entity lv : this.entitiesById.values()) {
            if (!(lv instanceof EnderDragonEntity) || !lv.isAlive()) continue;
            list.add((EnderDragonEntity)lv);
        }
        return list;
    }

    public List<ServerPlayerEntity> getPlayers(Predicate<? super ServerPlayerEntity> predicate) {
        ArrayList list = Lists.newArrayList();
        for (ServerPlayerEntity lv : this.players) {
            if (!predicate.test(lv)) continue;
            list.add(lv);
        }
        return list;
    }

    @Nullable
    public ServerPlayerEntity getRandomAlivePlayer() {
        List<ServerPlayerEntity> list = this.getPlayers(LivingEntity::isAlive);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(this.random.nextInt(list.size()));
    }

    @Override
    public boolean spawnEntity(Entity arg) {
        return this.addEntity(arg);
    }

    public boolean tryLoadEntity(Entity arg) {
        return this.addEntity(arg);
    }

    public void onDimensionChanged(Entity arg) {
        boolean bl = arg.teleporting;
        arg.teleporting = true;
        this.tryLoadEntity(arg);
        arg.teleporting = bl;
        this.checkChunk(arg);
    }

    public void onPlayerTeleport(ServerPlayerEntity arg) {
        this.addPlayer(arg);
        this.checkChunk(arg);
    }

    public void onPlayerChangeDimension(ServerPlayerEntity arg) {
        this.addPlayer(arg);
        this.checkChunk(arg);
    }

    public void onPlayerConnected(ServerPlayerEntity arg) {
        this.addPlayer(arg);
    }

    public void onPlayerRespawned(ServerPlayerEntity arg) {
        this.addPlayer(arg);
    }

    private void addPlayer(ServerPlayerEntity arg) {
        Entity lv = this.entitiesByUuid.get(arg.getUuid());
        if (lv != null) {
            LOGGER.warn("Force-added player with duplicate UUID {}", (Object)arg.getUuid().toString());
            lv.detach();
            this.removePlayer((ServerPlayerEntity)lv);
        }
        this.players.add(arg);
        this.updateSleepingPlayers();
        Chunk lv2 = this.getChunk(MathHelper.floor(arg.getX() / 16.0), MathHelper.floor(arg.getZ() / 16.0), ChunkStatus.FULL, true);
        if (lv2 instanceof WorldChunk) {
            lv2.addEntity(arg);
        }
        this.loadEntityUnchecked(arg);
    }

    private boolean addEntity(Entity arg) {
        if (arg.removed) {
            LOGGER.warn("Tried to add entity {} but it was marked as removed already", (Object)EntityType.getId(arg.getType()));
            return false;
        }
        if (this.checkUuid(arg)) {
            return false;
        }
        Chunk lv = this.getChunk(MathHelper.floor(arg.getX() / 16.0), MathHelper.floor(arg.getZ() / 16.0), ChunkStatus.FULL, arg.teleporting);
        if (!(lv instanceof WorldChunk)) {
            return false;
        }
        lv.addEntity(arg);
        this.loadEntityUnchecked(arg);
        return true;
    }

    public boolean loadEntity(Entity arg) {
        if (this.checkUuid(arg)) {
            return false;
        }
        this.loadEntityUnchecked(arg);
        return true;
    }

    private boolean checkUuid(Entity arg) {
        Entity lv = this.entitiesByUuid.get(arg.getUuid());
        if (lv == null) {
            return false;
        }
        LOGGER.warn("Keeping entity {} that already exists with UUID {}", (Object)EntityType.getId(lv.getType()), (Object)arg.getUuid().toString());
        return true;
    }

    public void unloadEntities(WorldChunk arg) {
        this.unloadedBlockEntities.addAll(arg.getBlockEntities().values());
        for (TypeFilterableList<Entity> lv : arg.getEntitySectionArray()) {
            for (Entity lv2 : lv) {
                if (lv2 instanceof ServerPlayerEntity) continue;
                if (this.inEntityTick) {
                    throw Util.throwOrPause(new IllegalStateException("Removing entity while ticking!"));
                }
                this.entitiesById.remove(lv2.getEntityId());
                this.unloadEntity(lv2);
            }
        }
    }

    public void unloadEntity(Entity arg) {
        if (arg instanceof EnderDragonEntity) {
            for (EnderDragonPart lv : ((EnderDragonEntity)arg).getBodyParts()) {
                lv.remove();
            }
        }
        this.entitiesByUuid.remove(arg.getUuid());
        this.getChunkManager().unloadEntity(arg);
        if (arg instanceof ServerPlayerEntity) {
            ServerPlayerEntity lv2 = (ServerPlayerEntity)arg;
            this.players.remove(lv2);
        }
        this.getScoreboard().resetEntityScore(arg);
        if (arg instanceof MobEntity) {
            this.entityNavigations.remove(((MobEntity)arg).getNavigation());
        }
    }

    private void loadEntityUnchecked(Entity arg) {
        if (this.inEntityTick) {
            this.entitiesToLoad.add(arg);
        } else {
            this.entitiesById.put(arg.getEntityId(), (Object)arg);
            if (arg instanceof EnderDragonEntity) {
                for (EnderDragonPart lv : ((EnderDragonEntity)arg).getBodyParts()) {
                    this.entitiesById.put(lv.getEntityId(), (Object)lv);
                }
            }
            this.entitiesByUuid.put(arg.getUuid(), arg);
            this.getChunkManager().loadEntity(arg);
            if (arg instanceof MobEntity) {
                this.entityNavigations.add(((MobEntity)arg).getNavigation());
            }
        }
    }

    public void removeEntity(Entity arg) {
        if (this.inEntityTick) {
            throw Util.throwOrPause(new IllegalStateException("Removing entity while ticking!"));
        }
        this.removeEntityFromChunk(arg);
        this.entitiesById.remove(arg.getEntityId());
        this.unloadEntity(arg);
    }

    private void removeEntityFromChunk(Entity arg) {
        Chunk lv = this.getChunk(arg.chunkX, arg.chunkZ, ChunkStatus.FULL, false);
        if (lv instanceof WorldChunk) {
            ((WorldChunk)lv).remove(arg);
        }
    }

    public void removePlayer(ServerPlayerEntity arg) {
        arg.remove();
        this.removeEntity(arg);
        this.updateSleepingPlayers();
    }

    @Override
    public void setBlockBreakingInfo(int i, BlockPos arg, int j) {
        for (ServerPlayerEntity lv : this.server.getPlayerManager().getPlayerList()) {
            double f;
            double e;
            double d;
            if (lv == null || lv.world != this || lv.getEntityId() == i || !((d = (double)arg.getX() - lv.getX()) * d + (e = (double)arg.getY() - lv.getY()) * e + (f = (double)arg.getZ() - lv.getZ()) * f < 1024.0)) continue;
            lv.networkHandler.sendPacket(new BlockBreakingProgressS2CPacket(i, arg, j));
        }
    }

    @Override
    public void playSound(@Nullable PlayerEntity arg, double d, double e, double f, SoundEvent arg2, SoundCategory arg3, float g, float h) {
        this.server.getPlayerManager().sendToAround(arg, d, e, f, g > 1.0f ? (double)(16.0f * g) : 16.0, this.getRegistryKey(), new PlaySoundS2CPacket(arg2, arg3, d, e, f, g, h));
    }

    @Override
    public void playSoundFromEntity(@Nullable PlayerEntity arg, Entity arg2, SoundEvent arg3, SoundCategory arg4, float f, float g) {
        this.server.getPlayerManager().sendToAround(arg, arg2.getX(), arg2.getY(), arg2.getZ(), f > 1.0f ? (double)(16.0f * f) : 16.0, this.getRegistryKey(), new PlaySoundFromEntityS2CPacket(arg3, arg4, arg2, f, g));
    }

    @Override
    public void syncGlobalEvent(int i, BlockPos arg, int j) {
        this.server.getPlayerManager().sendToAll(new WorldEventS2CPacket(i, arg, j, true));
    }

    @Override
    public void syncWorldEvent(@Nullable PlayerEntity arg, int i, BlockPos arg2, int j) {
        this.server.getPlayerManager().sendToAround(arg, arg2.getX(), arg2.getY(), arg2.getZ(), 64.0, this.getRegistryKey(), new WorldEventS2CPacket(i, arg2, j, false));
    }

    @Override
    public void updateListeners(BlockPos arg, BlockState arg2, BlockState arg3, int i) {
        this.getChunkManager().markForUpdate(arg);
        VoxelShape lv = arg2.getCollisionShape(this, arg);
        VoxelShape lv2 = arg3.getCollisionShape(this, arg);
        if (!VoxelShapes.matchesAnywhere(lv, lv2, BooleanBiFunction.NOT_SAME)) {
            return;
        }
        for (EntityNavigation lv3 : this.entityNavigations) {
            if (lv3.shouldRecalculatePath()) continue;
            lv3.onBlockChanged(arg);
        }
    }

    @Override
    public void sendEntityStatus(Entity arg, byte b) {
        this.getChunkManager().sendToNearbyPlayers(arg, new EntityStatusS2CPacket(arg, b));
    }

    @Override
    public ServerChunkManager getChunkManager() {
        return this.serverChunkManager;
    }

    @Override
    public Explosion createExplosion(@Nullable Entity arg, @Nullable DamageSource arg2, @Nullable class_5362 arg3, double d, double e, double f, float g, boolean bl, Explosion.DestructionType arg4) {
        Explosion lv = new Explosion(this, arg, arg2, arg3, d, e, f, g, bl, arg4);
        lv.collectBlocksAndDamageEntities();
        lv.affectWorld(false);
        if (arg4 == Explosion.DestructionType.NONE) {
            lv.clearAffectedBlocks();
        }
        for (ServerPlayerEntity lv2 : this.players) {
            if (!(lv2.squaredDistanceTo(d, e, f) < 4096.0)) continue;
            lv2.networkHandler.sendPacket(new ExplosionS2CPacket(d, e, f, g, lv.getAffectedBlocks(), lv.getAffectedPlayers().get(lv2)));
        }
        return lv;
    }

    @Override
    public void addSyncedBlockEvent(BlockPos arg, Block arg2, int i, int j) {
        this.syncedBlockEventQueue.add((Object)new BlockEvent(arg, arg2, i, j));
    }

    private void processSyncedBlockEvents() {
        while (!this.syncedBlockEventQueue.isEmpty()) {
            BlockEvent lv = (BlockEvent)this.syncedBlockEventQueue.removeFirst();
            if (!this.processBlockEvent(lv)) continue;
            this.server.getPlayerManager().sendToAround(null, lv.getPos().getX(), lv.getPos().getY(), lv.getPos().getZ(), 64.0, this.getRegistryKey(), new BlockEventS2CPacket(lv.getPos(), lv.getBlock(), lv.getType(), lv.getData()));
        }
    }

    private boolean processBlockEvent(BlockEvent arg) {
        BlockState lv = this.getBlockState(arg.getPos());
        if (lv.isOf(arg.getBlock())) {
            return lv.onSyncedBlockEvent(this, arg.getPos(), arg.getType(), arg.getData());
        }
        return false;
    }

    public ServerTickScheduler<Block> getBlockTickScheduler() {
        return this.blockTickScheduler;
    }

    public ServerTickScheduler<Fluid> getFluidTickScheduler() {
        return this.fluidTickScheduler;
    }

    @Override
    @Nonnull
    public MinecraftServer getServer() {
        return this.server;
    }

    public PortalForcer getPortalForcer() {
        return this.portalForcer;
    }

    public StructureManager getStructureManager() {
        return this.server.getStructureManager();
    }

    public <T extends ParticleEffect> int spawnParticles(T arg, double d, double e, double f, int i, double g, double h, double j, double k) {
        ParticleS2CPacket lv = new ParticleS2CPacket(arg, false, d, e, f, (float)g, (float)h, (float)j, (float)k, i);
        int l = 0;
        for (int m = 0; m < this.players.size(); ++m) {
            ServerPlayerEntity lv2 = this.players.get(m);
            if (!this.sendToPlayerIfNearby(lv2, false, d, e, f, lv)) continue;
            ++l;
        }
        return l;
    }

    public <T extends ParticleEffect> boolean spawnParticles(ServerPlayerEntity arg, T arg2, boolean bl, double d, double e, double f, int i, double g, double h, double j, double k) {
        ParticleS2CPacket lv = new ParticleS2CPacket(arg2, bl, d, e, f, (float)g, (float)h, (float)j, (float)k, i);
        return this.sendToPlayerIfNearby(arg, bl, d, e, f, lv);
    }

    private boolean sendToPlayerIfNearby(ServerPlayerEntity arg, boolean bl, double d, double e, double f, Packet<?> arg2) {
        if (arg.getServerWorld() != this) {
            return false;
        }
        BlockPos lv = arg.getBlockPos();
        if (lv.isWithinDistance(new Vec3d(d, e, f), bl ? 512.0 : 32.0)) {
            arg.networkHandler.sendPacket(arg2);
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public Entity getEntityById(int i) {
        return (Entity)this.entitiesById.get(i);
    }

    @Nullable
    public Entity getEntity(UUID uUID) {
        return this.entitiesByUuid.get(uUID);
    }

    @Nullable
    public BlockPos locateStructure(StructureFeature<?> arg, BlockPos arg2, int i, boolean bl) {
        if (!this.server.getSaveProperties().getGeneratorOptions().shouldGenerateStructures()) {
            return null;
        }
        return this.getChunkManager().getChunkGenerator().locateStructure(this, arg, arg2, i, bl);
    }

    @Nullable
    public BlockPos locateBiome(Biome arg, BlockPos arg2, int i, int j) {
        return this.getChunkManager().getChunkGenerator().getBiomeSource().method_24385(arg2.getX(), arg2.getY(), arg2.getZ(), i, j, (List<Biome>)ImmutableList.of((Object)arg), this.random, true);
    }

    @Override
    public RecipeManager getRecipeManager() {
        return this.server.getRecipeManager();
    }

    @Override
    public RegistryTagManager getTagManager() {
        return this.server.getTagManager();
    }

    @Override
    public boolean isSavingDisabled() {
        return this.savingDisabled;
    }

    public PersistentStateManager getPersistentStateManager() {
        return this.getChunkManager().getPersistentStateManager();
    }

    @Override
    @Nullable
    public MapState getMapState(String string) {
        return this.getServer().getWorld(World.OVERWORLD).getPersistentStateManager().get(() -> new MapState(string), string);
    }

    @Override
    public void putMapState(MapState arg) {
        this.getServer().getWorld(World.OVERWORLD).getPersistentStateManager().set(arg);
    }

    @Override
    public int getNextMapId() {
        return this.getServer().getWorld(World.OVERWORLD).getPersistentStateManager().getOrCreate(IdCountsState::new, "idcounts").getNextMapId();
    }

    public void setSpawnPos(BlockPos arg) {
        ChunkPos lv = new ChunkPos(new BlockPos(this.properties.getSpawnX(), 0, this.properties.getSpawnZ()));
        this.properties.setSpawnPos(arg);
        this.getChunkManager().removeTicket(ChunkTicketType.START, lv, 11, Unit.INSTANCE);
        this.getChunkManager().addTicket(ChunkTicketType.START, new ChunkPos(arg), 11, Unit.INSTANCE);
        this.getServer().getPlayerManager().sendToAll(new PlayerSpawnPositionS2CPacket(arg));
    }

    public BlockPos getSpawnPos() {
        BlockPos lv = new BlockPos(this.properties.getSpawnX(), this.properties.getSpawnY(), this.properties.getSpawnZ());
        if (!this.getWorldBorder().contains(lv)) {
            lv = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
        }
        return lv;
    }

    public LongSet getForcedChunks() {
        ForcedChunkState lv = this.getPersistentStateManager().get(ForcedChunkState::new, "chunks");
        return lv != null ? LongSets.unmodifiable((LongSet)lv.getChunks()) : LongSets.EMPTY_SET;
    }

    public boolean setChunkForced(int i, int j, boolean bl) {
        boolean bl3;
        ForcedChunkState lv = this.getPersistentStateManager().getOrCreate(ForcedChunkState::new, "chunks");
        ChunkPos lv2 = new ChunkPos(i, j);
        long l = lv2.toLong();
        if (bl) {
            boolean bl2 = lv.getChunks().add(l);
            if (bl2) {
                this.getChunk(i, j);
            }
        } else {
            bl3 = lv.getChunks().remove(l);
        }
        lv.setDirty(bl3);
        if (bl3) {
            this.getChunkManager().setChunkForced(lv2, bl);
        }
        return bl3;
    }

    public List<ServerPlayerEntity> getPlayers() {
        return this.players;
    }

    @Override
    public void onBlockChanged(BlockPos arg, BlockState arg22, BlockState arg3) {
        Optional<PointOfInterestType> optional2;
        Optional<PointOfInterestType> optional = PointOfInterestType.from(arg22);
        if (Objects.equals(optional, optional2 = PointOfInterestType.from(arg3))) {
            return;
        }
        BlockPos lv = arg.toImmutable();
        optional.ifPresent(arg2 -> this.getServer().execute(() -> {
            this.getPointOfInterestStorage().remove(lv);
            DebugInfoSender.sendPoiRemoval(this, lv);
        }));
        optional2.ifPresent(arg2 -> this.getServer().execute(() -> {
            this.getPointOfInterestStorage().add(lv, (PointOfInterestType)arg2);
            DebugInfoSender.sendPoiAddition(this, lv);
        }));
    }

    public PointOfInterestStorage getPointOfInterestStorage() {
        return this.getChunkManager().getPointOfInterestStorage();
    }

    public boolean isNearOccupiedPointOfInterest(BlockPos arg) {
        return this.isNearOccupiedPointOfInterest(arg, 1);
    }

    public boolean isNearOccupiedPointOfInterest(ChunkSectionPos arg) {
        return this.isNearOccupiedPointOfInterest(arg.getCenterPos());
    }

    public boolean isNearOccupiedPointOfInterest(BlockPos arg, int i) {
        if (i > 6) {
            return false;
        }
        return this.getOccupiedPointOfInterestDistance(ChunkSectionPos.from(arg)) <= i;
    }

    public int getOccupiedPointOfInterestDistance(ChunkSectionPos arg) {
        return this.getPointOfInterestStorage().getDistanceFromNearestOccupied(arg);
    }

    public RaidManager getRaidManager() {
        return this.raidManager;
    }

    @Nullable
    public Raid getRaidAt(BlockPos arg) {
        return this.raidManager.getRaidAt(arg, 9216);
    }

    public boolean hasRaidAt(BlockPos arg) {
        return this.getRaidAt(arg) != null;
    }

    public void handleInteraction(EntityInteraction arg, Entity arg2, InteractionObserver arg3) {
        arg3.onInteractionWith(arg, arg2);
    }

    public void dump(Path path) throws IOException {
        Object entry2;
        Object lv2;
        ThreadedAnvilChunkStorage lv = this.getChunkManager().threadedAnvilChunkStorage;
        try (BufferedWriter writer = Files.newBufferedWriter(path.resolve("stats.txt"), new OpenOption[0]);){
            writer.write(String.format("spawning_chunks: %d\n", lv.getTicketManager().getSpawningChunkCount()));
            lv2 = this.getChunkManager().getSpawnInfo();
            if (lv2 != null) {
                for (Object entry2 : ((SpawnHelper.Info)lv2).getGroupToCount().object2IntEntrySet()) {
                    writer.write(String.format("spawn_count.%s: %d\n", ((SpawnGroup)entry2.getKey()).getName(), entry2.getIntValue()));
                }
            }
            writer.write(String.format("entities: %d\n", this.entitiesById.size()));
            writer.write(String.format("block_entities: %d\n", this.blockEntities.size()));
            writer.write(String.format("block_ticks: %d\n", ((ServerTickScheduler)this.getBlockTickScheduler()).getTicks()));
            writer.write(String.format("fluid_ticks: %d\n", ((ServerTickScheduler)this.getFluidTickScheduler()).getTicks()));
            writer.write("distance_manager: " + lv.getTicketManager().toDumpString() + "\n");
            writer.write(String.format("pending_tasks: %d\n", this.getChunkManager().getPendingTasks()));
        }
        CrashReport lv3 = new CrashReport("Level dump", new Exception("dummy"));
        this.addDetailsToCrashReport(lv3);
        BufferedWriter writer2 = Files.newBufferedWriter(path.resolve("example_crash.txt"), new OpenOption[0]);
        lv2 = null;
        try {
            writer2.write(lv3.asString());
        }
        catch (Throwable throwable) {
            lv2 = throwable;
            throw throwable;
        }
        finally {
            if (writer2 != null) {
                if (lv2 != null) {
                    try {
                        ((Writer)writer2).close();
                    }
                    catch (Throwable throwable) {
                        ((Throwable)lv2).addSuppressed(throwable);
                    }
                } else {
                    ((Writer)writer2).close();
                }
            }
        }
        Path path2 = path.resolve("chunks.csv");
        BufferedWriter writer3 = Files.newBufferedWriter(path2, new OpenOption[0]);
        Object object = null;
        try {
            lv.dump(writer3);
        }
        catch (Throwable entry2) {
            object = entry2;
            throw entry2;
        }
        finally {
            if (writer3 != null) {
                if (object != null) {
                    try {
                        ((Writer)writer3).close();
                    }
                    catch (Throwable entry2) {
                        ((Throwable)object).addSuppressed(entry2);
                    }
                } else {
                    ((Writer)writer3).close();
                }
            }
        }
        Path path3 = path.resolve("entities.csv");
        BufferedWriter writer4 = Files.newBufferedWriter(path3, new OpenOption[0]);
        entry2 = null;
        try {
            ServerWorld.dumpEntities(writer4, (Iterable<Entity>)this.entitiesById.values());
        }
        catch (Throwable throwable) {
            entry2 = throwable;
            throw throwable;
        }
        finally {
            if (writer4 != null) {
                if (entry2 != null) {
                    try {
                        ((Writer)writer4).close();
                    }
                    catch (Throwable throwable) {
                        ((Throwable)entry2).addSuppressed(throwable);
                    }
                } else {
                    ((Writer)writer4).close();
                }
            }
        }
        Path path4 = path.resolve("block_entities.csv");
        try (BufferedWriter writer5 = Files.newBufferedWriter(path4, new OpenOption[0]);){
            this.dumpBlockEntities(writer5);
        }
    }

    private static void dumpEntities(Writer writer, Iterable<Entity> iterable) throws IOException {
        CsvWriter lv = CsvWriter.makeHeader().addColumn("x").addColumn("y").addColumn("z").addColumn("uuid").addColumn("type").addColumn("alive").addColumn("display_name").addColumn("custom_name").startBody(writer);
        for (Entity lv2 : iterable) {
            Text lv3 = lv2.getCustomName();
            Text lv4 = lv2.getDisplayName();
            lv.printRow(lv2.getX(), lv2.getY(), lv2.getZ(), lv2.getUuid(), Registry.ENTITY_TYPE.getId(lv2.getType()), lv2.isAlive(), lv4.getString(), lv3 != null ? lv3.getString() : null);
        }
    }

    private void dumpBlockEntities(Writer writer) throws IOException {
        CsvWriter lv = CsvWriter.makeHeader().addColumn("x").addColumn("y").addColumn("z").addColumn("type").startBody(writer);
        for (BlockEntity lv2 : this.blockEntities) {
            BlockPos lv3 = lv2.getPos();
            lv.printRow(lv3.getX(), lv3.getY(), lv3.getZ(), Registry.BLOCK_ENTITY_TYPE.getId(lv2.getType()));
        }
    }

    @VisibleForTesting
    public void clearUpdatesInArea(BlockBox arg) {
        this.syncedBlockEventQueue.removeIf(arg2 -> arg.contains(arg2.getPos()));
    }

    @Override
    public void updateNeighbors(BlockPos arg, Block arg2) {
        if (!this.isDebugWorld()) {
            this.updateNeighborsAlways(arg, arg2);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public float getBrightness(Direction arg, boolean bl) {
        return 1.0f;
    }

    public Iterable<Entity> iterateEntities() {
        return Iterables.unmodifiableIterable((Iterable)this.entitiesById.values());
    }

    public String toString() {
        return "ServerLevel[" + this.field_24456.getLevelName() + "]";
    }

    public boolean method_28125() {
        return this.server.getSaveProperties().getGeneratorOptions().isFlatWorld();
    }

    @Override
    public long getSeed() {
        return this.server.getSaveProperties().getGeneratorOptions().getSeed();
    }

    @Nullable
    public EnderDragonFight method_29198() {
        return this.enderDragonFight;
    }

    public static void method_29200(ServerWorld arg) {
        BlockPos lv = field_25144;
        int i = lv.getX();
        int j = lv.getY() - 2;
        int k = lv.getZ();
        BlockPos.iterate(i - 2, j + 1, k - 2, i + 2, j + 3, k + 2).forEach(arg2 -> arg.setBlockState((BlockPos)arg2, Blocks.AIR.getDefaultState()));
        BlockPos.iterate(i - 2, j, k - 2, i + 2, j, k + 2).forEach(arg2 -> arg.setBlockState((BlockPos)arg2, Blocks.OBSIDIAN.getDefaultState()));
    }

    @Override
    public /* synthetic */ Scoreboard getScoreboard() {
        return this.getScoreboard();
    }

    @Override
    public /* synthetic */ ChunkManager getChunkManager() {
        return this.getChunkManager();
    }

    public /* synthetic */ TickScheduler getFluidTickScheduler() {
        return this.getFluidTickScheduler();
    }

    public /* synthetic */ TickScheduler getBlockTickScheduler() {
        return this.getBlockTickScheduler();
    }
}

