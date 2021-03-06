/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Either
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTaskPrioritySystem;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.LevelPrioritizedQueue;
import net.minecraft.server.world.PlayerChunkWatchingManager;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Util;
import net.minecraft.util.collection.TypeFilterableList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.CsvWriter;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.GameRules;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadedAnvilChunkStorage
extends VersionedChunkStorage
implements ChunkHolder.PlayersWatchingChunkProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int MAX_LEVEL = 33 + ChunkStatus.getMaxTargetGenerationRadius();
    private final Long2ObjectLinkedOpenHashMap<ChunkHolder> currentChunkHolders = new Long2ObjectLinkedOpenHashMap();
    private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> chunkHolders = this.currentChunkHolders.clone();
    private final Long2ObjectLinkedOpenHashMap<ChunkHolder> field_18807 = new Long2ObjectLinkedOpenHashMap();
    private final LongSet loadedChunks = new LongOpenHashSet();
    private final ServerWorld world;
    private final ServerLightingProvider serverLightingProvider;
    private final ThreadExecutor<Runnable> mainThreadExecutor;
    private final ChunkGenerator chunkGenerator;
    private final Supplier<PersistentStateManager> persistentStateManagerFactory;
    private final PointOfInterestStorage pointOfInterestStorage;
    private final LongSet unloadedChunks = new LongOpenHashSet();
    private boolean chunkHolderListDirty;
    private final ChunkTaskPrioritySystem chunkTaskPrioritySystem;
    private final MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> worldGenExecutor;
    private final MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> mainExecutor;
    private final WorldGenerationProgressListener worldGenerationProgressListener;
    private final TicketManager ticketManager;
    private final AtomicInteger totalChunksLoadedCount = new AtomicInteger();
    private final StructureManager structureManager;
    private final File saveDir;
    private final PlayerChunkWatchingManager playerChunkWatchingManager = new PlayerChunkWatchingManager();
    private final Int2ObjectMap<EntityTracker> entityTrackers = new Int2ObjectOpenHashMap();
    private final Long2ByteMap field_23786 = new Long2ByteOpenHashMap();
    private final Queue<Runnable> field_19343 = Queues.newConcurrentLinkedQueue();
    private int watchDistance;

    public ThreadedAnvilChunkStorage(ServerWorld arg, LevelStorage.Session arg2, DataFixer dataFixer, StructureManager arg3, Executor workerExecutor, ThreadExecutor<Runnable> mainThreadExecutor, ChunkProvider chunkProvider, ChunkGenerator chunkGenerator, WorldGenerationProgressListener worldGenerationProgressListener, Supplier<PersistentStateManager> supplier, int i, boolean bl) {
        super(new File(arg2.getWorldDirectory(arg.getRegistryKey()), "region"), dataFixer, bl);
        this.structureManager = arg3;
        this.saveDir = arg2.getWorldDirectory(arg.getRegistryKey());
        this.world = arg;
        this.chunkGenerator = chunkGenerator;
        this.mainThreadExecutor = mainThreadExecutor;
        TaskExecutor<Runnable> lv = TaskExecutor.create(workerExecutor, "worldgen");
        MessageListener<Runnable> lv2 = MessageListener.create("main", mainThreadExecutor::send);
        this.worldGenerationProgressListener = worldGenerationProgressListener;
        TaskExecutor<Runnable> lv3 = TaskExecutor.create(workerExecutor, "light");
        this.chunkTaskPrioritySystem = new ChunkTaskPrioritySystem((List<MessageListener<?>>)ImmutableList.of(lv, lv2, lv3), workerExecutor, Integer.MAX_VALUE);
        this.worldGenExecutor = this.chunkTaskPrioritySystem.createExecutor(lv, false);
        this.mainExecutor = this.chunkTaskPrioritySystem.createExecutor(lv2, false);
        this.serverLightingProvider = new ServerLightingProvider(chunkProvider, this, this.world.getDimension().hasSkyLight(), lv3, this.chunkTaskPrioritySystem.createExecutor(lv3, false));
        this.ticketManager = new TicketManager(workerExecutor, mainThreadExecutor);
        this.persistentStateManagerFactory = supplier;
        this.pointOfInterestStorage = new PointOfInterestStorage(new File(this.saveDir, "poi"), dataFixer, bl);
        this.setViewDistance(i);
    }

    private static double getSquaredDistance(ChunkPos pos, Entity entity) {
        double d = pos.x * 16 + 8;
        double e = pos.z * 16 + 8;
        double f = d - entity.getX();
        double g = e - entity.getZ();
        return f * f + g * g;
    }

    private static int getChebyshevDistance(ChunkPos pos, ServerPlayerEntity player, boolean useCameraPosition) {
        int l;
        int k;
        if (useCameraPosition) {
            ChunkSectionPos lv = player.getCameraPosition();
            int i = lv.getSectionX();
            int j = lv.getSectionZ();
        } else {
            k = MathHelper.floor(player.getX() / 16.0);
            l = MathHelper.floor(player.getZ() / 16.0);
        }
        return ThreadedAnvilChunkStorage.getChebyshevDistance(pos, k, l);
    }

    private static int getChebyshevDistance(ChunkPos pos, int x, int z) {
        int k = pos.x - x;
        int l = pos.z - z;
        return Math.max(Math.abs(k), Math.abs(l));
    }

    protected ServerLightingProvider getLightProvider() {
        return this.serverLightingProvider;
    }

    @Nullable
    protected ChunkHolder getCurrentChunkHolder(long pos) {
        return (ChunkHolder)this.currentChunkHolders.get(pos);
    }

    @Nullable
    protected ChunkHolder getChunkHolder(long pos) {
        return (ChunkHolder)this.chunkHolders.get(pos);
    }

    protected IntSupplier getCompletedLevelSupplier(long pos) {
        return () -> {
            ChunkHolder lv = this.getChunkHolder(pos);
            if (lv == null) {
                return LevelPrioritizedQueue.LEVEL_COUNT - 1;
            }
            return Math.min(lv.getCompletedLevel(), LevelPrioritizedQueue.LEVEL_COUNT - 1);
        };
    }

    @Environment(value=EnvType.CLIENT)
    public String method_23272(ChunkPos arg) {
        ChunkHolder lv = this.getChunkHolder(arg.toLong());
        if (lv == null) {
            return "null";
        }
        String string = lv.getLevel() + "\n";
        ChunkStatus lv2 = lv.method_23270();
        Chunk lv3 = lv.getCompletedChunk();
        if (lv2 != null) {
            string = string + "St: \u00a7" + lv2.getIndex() + lv2 + '\u00a7' + "r\n";
        }
        if (lv3 != null) {
            string = string + "Ch: \u00a7" + lv3.getStatus().getIndex() + lv3.getStatus() + '\u00a7' + "r\n";
        }
        ChunkHolder.LevelType lv4 = lv.getLevelType();
        string = string + "\u00a7" + lv4.ordinal() + (Object)((Object)lv4);
        return string + '\u00a7' + "r";
    }

    private CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> createChunkRegionFuture(ChunkPos centerChunk, final int margin, IntFunction<ChunkStatus> distanceToStatus) {
        ArrayList list2 = Lists.newArrayList();
        final int j = centerChunk.x;
        final int k = centerChunk.z;
        for (int l = -margin; l <= margin; ++l) {
            for (int m = -margin; m <= margin; ++m) {
                int n = Math.max(Math.abs(m), Math.abs(l));
                final ChunkPos lv = new ChunkPos(j + m, k + l);
                long o = lv.toLong();
                ChunkHolder lv2 = this.getCurrentChunkHolder(o);
                if (lv2 == null) {
                    return CompletableFuture.completedFuture(Either.right((Object)new ChunkHolder.Unloaded(){

                        public String toString() {
                            return "Unloaded " + lv.toString();
                        }
                    }));
                }
                ChunkStatus lv3 = distanceToStatus.apply(n);
                CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = lv2.createFuture(lv3, this);
                list2.add(completableFuture);
            }
        }
        CompletableFuture completableFuture2 = Util.combine(list2);
        return completableFuture2.thenApply(list -> {
            ArrayList list2 = Lists.newArrayList();
            int l = 0;
            for (final Either either : list) {
                Optional optional = either.left();
                if (!optional.isPresent()) {
                    final int m = l;
                    return Either.right((Object)new ChunkHolder.Unloaded(){

                        public String toString() {
                            return "Unloaded " + new ChunkPos(j + m % (margin * 2 + 1), k + m / (margin * 2 + 1)) + " " + ((ChunkHolder.Unloaded)either.right().get()).toString();
                        }
                    });
                }
                list2.add(optional.get());
                ++l;
            }
            return Either.left((Object)list2);
        });
    }

    public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> createEntityTickingChunkFuture(ChunkPos pos) {
        return this.createChunkRegionFuture(pos, 2, i -> ChunkStatus.FULL).thenApplyAsync(either -> either.mapLeft(list -> (WorldChunk)list.get(list.size() / 2)), (Executor)this.mainThreadExecutor);
    }

    @Nullable
    private ChunkHolder setLevel(long pos, int level, @Nullable ChunkHolder holder, int j) {
        if (j > MAX_LEVEL && level > MAX_LEVEL) {
            return holder;
        }
        if (holder != null) {
            holder.setLevel(level);
        }
        if (holder != null) {
            if (level > MAX_LEVEL) {
                this.unloadedChunks.add(pos);
            } else {
                this.unloadedChunks.remove(pos);
            }
        }
        if (level <= MAX_LEVEL && holder == null) {
            holder = (ChunkHolder)this.field_18807.remove(pos);
            if (holder != null) {
                holder.setLevel(level);
            } else {
                holder = new ChunkHolder(new ChunkPos(pos), level, this.serverLightingProvider, this.chunkTaskPrioritySystem, this);
            }
            this.currentChunkHolders.put(pos, (Object)holder);
            this.chunkHolderListDirty = true;
        }
        return holder;
    }

    @Override
    public void close() throws IOException {
        try {
            this.chunkTaskPrioritySystem.close();
            this.pointOfInterestStorage.close();
        }
        finally {
            super.close();
        }
    }

    protected void save(boolean flush) {
        if (flush) {
            List list = this.chunkHolders.values().stream().filter(ChunkHolder::isTicking).peek(ChunkHolder::updateTickingStatus).collect(Collectors.toList());
            MutableBoolean mutableBoolean = new MutableBoolean();
            do {
                mutableBoolean.setFalse();
                list.stream().map(arg -> {
                    CompletableFuture<Chunk> completableFuture;
                    do {
                        completableFuture = arg.getFuture();
                        this.mainThreadExecutor.runTasks(completableFuture::isDone);
                    } while (completableFuture != arg.getFuture());
                    return completableFuture.join();
                }).filter(arg -> arg instanceof ReadOnlyChunk || arg instanceof WorldChunk).filter(this::save).forEach(arg -> mutableBoolean.setTrue());
            } while (mutableBoolean.isTrue());
            this.unloadChunks(() -> true);
            this.completeAll();
            LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", (Object)this.saveDir.getName());
        } else {
            this.chunkHolders.values().stream().filter(ChunkHolder::isTicking).forEach(arg -> {
                Chunk lv = arg.getFuture().getNow(null);
                if (lv instanceof ReadOnlyChunk || lv instanceof WorldChunk) {
                    this.save(lv);
                    arg.updateTickingStatus();
                }
            });
        }
    }

    protected void tick(BooleanSupplier shouldKeepTicking) {
        Profiler lv = this.world.getProfiler();
        lv.push("poi");
        this.pointOfInterestStorage.tick(shouldKeepTicking);
        lv.swap("chunk_unload");
        if (!this.world.isSavingDisabled()) {
            this.unloadChunks(shouldKeepTicking);
        }
        lv.pop();
    }

    private void unloadChunks(BooleanSupplier shouldKeepTicking) {
        Runnable runnable;
        LongIterator longIterator = this.unloadedChunks.iterator();
        int i = 0;
        while (longIterator.hasNext() && (shouldKeepTicking.getAsBoolean() || i < 200 || this.unloadedChunks.size() > 2000)) {
            long l = longIterator.nextLong();
            ChunkHolder lv = (ChunkHolder)this.currentChunkHolders.remove(l);
            if (lv != null) {
                this.field_18807.put(l, (Object)lv);
                this.chunkHolderListDirty = true;
                ++i;
                this.tryUnloadChunk(l, lv);
            }
            longIterator.remove();
        }
        while ((shouldKeepTicking.getAsBoolean() || this.field_19343.size() > 2000) && (runnable = this.field_19343.poll()) != null) {
            runnable.run();
        }
    }

    private void tryUnloadChunk(long pos, ChunkHolder arg) {
        CompletableFuture<Chunk> completableFuture = arg.getFuture();
        ((CompletableFuture)completableFuture.thenAcceptAsync(arg2 -> {
            CompletableFuture<Chunk> completableFuture2 = arg.getFuture();
            if (completableFuture2 != completableFuture) {
                this.tryUnloadChunk(pos, arg);
                return;
            }
            if (this.field_18807.remove(pos, (Object)arg) && arg2 != null) {
                if (arg2 instanceof WorldChunk) {
                    ((WorldChunk)arg2).setLoadedToWorld(false);
                }
                this.save((Chunk)arg2);
                if (this.loadedChunks.remove(pos) && arg2 instanceof WorldChunk) {
                    WorldChunk lv = (WorldChunk)arg2;
                    this.world.unloadEntities(lv);
                }
                this.serverLightingProvider.updateChunkStatus(arg2.getPos());
                this.serverLightingProvider.tick();
                this.worldGenerationProgressListener.setChunkStatus(arg2.getPos(), null);
            }
        }, this.field_19343::add)).whenComplete((void_, throwable) -> {
            if (throwable != null) {
                LOGGER.error("Failed to save chunk " + arg.getPos(), throwable);
            }
        });
    }

    protected boolean updateHolderMap() {
        if (!this.chunkHolderListDirty) {
            return false;
        }
        this.chunkHolders = this.currentChunkHolders.clone();
        this.chunkHolderListDirty = false;
        return true;
    }

    public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> createChunkFuture(ChunkHolder arg, ChunkStatus arg2) {
        ChunkPos lv = arg.getPos();
        if (arg2 == ChunkStatus.EMPTY) {
            return this.loadChunk(lv);
        }
        CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = arg.createFuture(arg2.getPrevious(), this);
        return completableFuture.thenComposeAsync(either -> {
            Chunk lv;
            Optional optional = either.left();
            if (!optional.isPresent()) {
                return CompletableFuture.completedFuture(either);
            }
            if (arg2 == ChunkStatus.LIGHT) {
                this.ticketManager.addTicketWithLevel(ChunkTicketType.LIGHT, lv, 33 + ChunkStatus.getTargetGenerationRadius(ChunkStatus.FEATURES), lv);
            }
            if ((lv = (Chunk)optional.get()).getStatus().isAtLeast(arg2)) {
                CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture2;
                if (arg2 == ChunkStatus.LIGHT) {
                    CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = this.generateChunk(arg, arg2);
                } else {
                    completableFuture2 = arg2.runLoadTask(this.world, this.structureManager, this.serverLightingProvider, arg2 -> this.convertToFullChunk(arg), lv);
                }
                this.worldGenerationProgressListener.setChunkStatus(lv, arg2);
                return completableFuture2;
            }
            return this.generateChunk(arg, arg2);
        }, (Executor)this.mainThreadExecutor);
    }

    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> loadChunk(ChunkPos pos) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                this.world.getProfiler().visit("chunkLoad");
                CompoundTag lv = this.getUpdatedChunkTag(pos);
                if (lv != null) {
                    boolean bl;
                    boolean bl2 = bl = lv.contains("Level", 10) && lv.getCompound("Level").contains("Status", 8);
                    if (bl) {
                        ProtoChunk lv2 = ChunkSerializer.deserialize(this.world, this.structureManager, this.pointOfInterestStorage, pos, lv);
                        lv2.setLastSaveTime(this.world.getTime());
                        this.method_27053(pos, lv2.getStatus().getChunkType());
                        return Either.left((Object)lv2);
                    }
                    LOGGER.error("Chunk file at {} is missing level data, skipping", (Object)pos);
                }
            }
            catch (CrashException lv3) {
                Throwable throwable = lv3.getCause();
                if (throwable instanceof IOException) {
                    LOGGER.error("Couldn't load chunk {}", (Object)pos, (Object)throwable);
                }
                this.method_27054(pos);
                throw lv3;
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't load chunk {}", (Object)pos, (Object)exception);
            }
            this.method_27054(pos);
            return Either.left((Object)new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA));
        }, this.mainThreadExecutor);
    }

    private void method_27054(ChunkPos arg) {
        this.field_23786.put(arg.toLong(), (byte)-1);
    }

    private byte method_27053(ChunkPos arg, ChunkStatus.ChunkType arg2) {
        return this.field_23786.put(arg.toLong(), arg2 == ChunkStatus.ChunkType.field_12808 ? (byte)-1 : 1);
    }

    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> generateChunk(ChunkHolder arg, ChunkStatus arg2) {
        ChunkPos lv = arg.getPos();
        CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> completableFuture = this.createChunkRegionFuture(lv, arg2.getTaskMargin(), i -> this.getRequiredStatusForGeneration(arg2, i));
        this.world.getProfiler().visit(() -> "chunkGenerate " + arg2.getId());
        return completableFuture.thenComposeAsync(either -> (CompletableFuture)either.map(list -> {
            try {
                CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = arg2.runGenerationTask(this.world, this.chunkGenerator, this.structureManager, this.serverLightingProvider, arg2 -> this.convertToFullChunk(arg), (List<Chunk>)list);
                this.worldGenerationProgressListener.setChunkStatus(lv, arg2);
                return completableFuture;
            }
            catch (Exception exception) {
                CrashReport lv = CrashReport.create(exception, "Exception generating new chunk");
                CrashReportSection lv2 = lv.addElement("Chunk to be generated");
                lv2.add("Location", String.format("%d,%d", arg.x, arg.z));
                lv2.add("Position hash", ChunkPos.toLong(arg.x, arg.z));
                lv2.add("Generator", this.chunkGenerator);
                throw new CrashException(lv);
            }
        }, arg2 -> {
            this.releaseLightTicket(lv);
            return CompletableFuture.completedFuture(Either.right((Object)arg2));
        }), runnable -> this.worldGenExecutor.send(ChunkTaskPrioritySystem.createMessage(arg, runnable)));
    }

    protected void releaseLightTicket(ChunkPos pos) {
        this.mainThreadExecutor.send(Util.debugRunnable(() -> this.ticketManager.removeTicketWithLevel(ChunkTicketType.LIGHT, pos, 33 + ChunkStatus.getTargetGenerationRadius(ChunkStatus.FEATURES), pos), () -> "release light ticket " + pos));
    }

    private ChunkStatus getRequiredStatusForGeneration(ChunkStatus centerChunkTargetStatus, int distance) {
        ChunkStatus lv2;
        if (distance == 0) {
            ChunkStatus lv = centerChunkTargetStatus.getPrevious();
        } else {
            lv2 = ChunkStatus.getTargetGenerationStatus(ChunkStatus.getTargetGenerationRadius(centerChunkTargetStatus) + distance);
        }
        return lv2;
    }

    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> convertToFullChunk(ChunkHolder arg) {
        CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = arg.getFuture(ChunkStatus.FULL.getPrevious());
        return completableFuture.thenApplyAsync(either -> {
            ChunkStatus lv = ChunkHolder.getTargetGenerationStatus(arg.getLevel());
            if (!lv.isAtLeast(ChunkStatus.FULL)) {
                return ChunkHolder.UNLOADED_CHUNK;
            }
            return either.mapLeft(arg2 -> {
                WorldChunk lv3;
                ChunkPos lv = arg.getPos();
                if (arg2 instanceof ReadOnlyChunk) {
                    WorldChunk lv2 = ((ReadOnlyChunk)arg2).getWrappedChunk();
                } else {
                    lv3 = new WorldChunk(this.world, (ProtoChunk)arg2);
                    arg.method_20456(new ReadOnlyChunk(lv3));
                }
                lv3.setLevelTypeProvider(() -> ChunkHolder.getLevelType(arg.getLevel()));
                lv3.loadToWorld();
                if (this.loadedChunks.add(lv.toLong())) {
                    lv3.setLoadedToWorld(true);
                    this.world.addBlockEntities(lv3.getBlockEntities().values());
                    Iterable list = null;
                    for (TypeFilterableList<Entity> lv4 : lv3.getEntitySectionArray()) {
                        for (Entity lv5 : lv4) {
                            if (lv5 instanceof PlayerEntity || this.world.loadEntity(lv5)) continue;
                            if (list == null) {
                                list = Lists.newArrayList((Object[])new Entity[]{lv5});
                                continue;
                            }
                            list.add(lv5);
                        }
                    }
                    if (list != null) {
                        list.forEach(lv3::remove);
                    }
                }
                return lv3;
            });
        }, runnable -> this.mainExecutor.send(ChunkTaskPrioritySystem.createMessage(runnable, arg.getPos().toLong(), arg::getLevel)));
    }

    public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> createTickingFuture(ChunkHolder arg) {
        ChunkPos lv = arg.getPos();
        CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> completableFuture = this.createChunkRegionFuture(lv, 1, i -> ChunkStatus.FULL);
        CompletionStage completableFuture2 = completableFuture.thenApplyAsync(either -> either.flatMap(list -> {
            WorldChunk lv = (WorldChunk)list.get(list.size() / 2);
            lv.runPostProcessing();
            return Either.left((Object)lv);
        }), runnable -> this.mainExecutor.send(ChunkTaskPrioritySystem.createMessage(arg, runnable)));
        ((CompletableFuture)completableFuture2).thenAcceptAsync(either -> either.mapLeft(arg22 -> {
            this.totalChunksLoadedCount.getAndIncrement();
            Packet[] lvs = new Packet[2];
            this.getPlayersWatchingChunk(lv, false).forEach(arg2 -> this.sendChunkDataPackets((ServerPlayerEntity)arg2, lvs, (WorldChunk)arg22));
            return Either.left((Object)arg22);
        }), runnable -> this.mainExecutor.send(ChunkTaskPrioritySystem.createMessage(arg, runnable)));
        return completableFuture2;
    }

    public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> createBorderFuture(ChunkHolder arg) {
        return arg.createFuture(ChunkStatus.FULL, this).thenApplyAsync(either -> either.mapLeft(arg -> {
            WorldChunk lv = (WorldChunk)arg;
            lv.disableTickSchedulers();
            return lv;
        }), runnable -> this.mainExecutor.send(ChunkTaskPrioritySystem.createMessage(arg, runnable)));
    }

    public int getTotalChunksLoadedCount() {
        return this.totalChunksLoadedCount.get();
    }

    private boolean save(Chunk arg) {
        this.pointOfInterestStorage.method_20436(arg.getPos());
        if (!arg.needsSaving()) {
            return false;
        }
        arg.setLastSaveTime(this.world.getTime());
        arg.setShouldSave(false);
        ChunkPos lv = arg.getPos();
        try {
            ChunkStatus lv2 = arg.getStatus();
            if (lv2.getChunkType() != ChunkStatus.ChunkType.field_12807) {
                if (this.method_27055(lv)) {
                    return false;
                }
                if (lv2 == ChunkStatus.EMPTY && arg.getStructureStarts().values().stream().noneMatch(StructureStart::hasChildren)) {
                    return false;
                }
            }
            this.world.getProfiler().visit("chunkSave");
            CompoundTag lv3 = ChunkSerializer.serialize(this.world, arg);
            this.setTagAt(lv, lv3);
            this.method_27053(lv, lv2.getChunkType());
            return true;
        }
        catch (Exception exception) {
            LOGGER.error("Failed to save chunk {},{}", (Object)lv.x, (Object)lv.z, (Object)exception);
            return false;
        }
    }

    /*
     * WARNING - void declaration
     */
    private boolean method_27055(ChunkPos arg) {
        void lv2;
        byte b = this.field_23786.get(arg.toLong());
        if (b != 0) {
            return b == 1;
        }
        try {
            CompoundTag lv = this.getUpdatedChunkTag(arg);
            if (lv == null) {
                this.method_27054(arg);
                return false;
            }
        }
        catch (Exception exception) {
            LOGGER.error("Failed to read chunk {}", (Object)arg, (Object)exception);
            this.method_27054(arg);
            return false;
        }
        ChunkStatus.ChunkType lv3 = ChunkSerializer.getChunkType((CompoundTag)lv2);
        return this.method_27053(arg, lv3) == 1;
    }

    protected void setViewDistance(int watchDistance) {
        int j = MathHelper.clamp(watchDistance + 1, 3, 33);
        if (j != this.watchDistance) {
            int k = this.watchDistance;
            this.watchDistance = j;
            this.ticketManager.setWatchDistance(this.watchDistance);
            for (ChunkHolder lv : this.currentChunkHolders.values()) {
                ChunkPos lv2 = lv.getPos();
                Packet[] lvs = new Packet[2];
                this.getPlayersWatchingChunk(lv2, false).forEach(arg2 -> {
                    int j = ThreadedAnvilChunkStorage.getChebyshevDistance(lv2, arg2, true);
                    boolean bl = j <= k;
                    boolean bl2 = j <= this.watchDistance;
                    this.sendWatchPackets((ServerPlayerEntity)arg2, lv2, lvs, bl, bl2);
                });
            }
        }
    }

    protected void sendWatchPackets(ServerPlayerEntity player, ChunkPos pos, Packet<?>[] packets, boolean withinMaxWatchDistance, boolean withinViewDistance) {
        ChunkHolder lv;
        if (player.world != this.world) {
            return;
        }
        if (withinViewDistance && !withinMaxWatchDistance && (lv = this.getChunkHolder(pos.toLong())) != null) {
            WorldChunk lv2 = lv.getWorldChunk();
            if (lv2 != null) {
                this.sendChunkDataPackets(player, packets, lv2);
            }
            DebugInfoSender.sendChunkWatchingChange(this.world, pos);
        }
        if (!withinViewDistance && withinMaxWatchDistance) {
            player.sendUnloadChunkPacket(pos);
        }
    }

    public int getLoadedChunkCount() {
        return this.chunkHolders.size();
    }

    protected TicketManager getTicketManager() {
        return this.ticketManager;
    }

    protected Iterable<ChunkHolder> entryIterator() {
        return Iterables.unmodifiableIterable((Iterable)this.chunkHolders.values());
    }

    void dump(Writer writer) throws IOException {
        CsvWriter lv = CsvWriter.makeHeader().addColumn("x").addColumn("z").addColumn("level").addColumn("in_memory").addColumn("status").addColumn("full_status").addColumn("accessible_ready").addColumn("ticking_ready").addColumn("entity_ticking_ready").addColumn("ticket").addColumn("spawning").addColumn("entity_count").addColumn("block_entity_count").startBody(writer);
        for (Long2ObjectMap.Entry entry : this.chunkHolders.long2ObjectEntrySet()) {
            ChunkPos lv2 = new ChunkPos(entry.getLongKey());
            ChunkHolder lv3 = (ChunkHolder)entry.getValue();
            Optional<Chunk> optional = Optional.ofNullable(lv3.getCompletedChunk());
            Optional<Object> optional2 = optional.flatMap(arg -> arg instanceof WorldChunk ? Optional.of((WorldChunk)arg) : Optional.empty());
            lv.printRow(lv2.x, lv2.z, lv3.getLevel(), optional.isPresent(), optional.map(Chunk::getStatus).orElse(null), optional2.map(WorldChunk::getLevelType).orElse(null), ThreadedAnvilChunkStorage.getFutureStatus(lv3.getBorderFuture()), ThreadedAnvilChunkStorage.getFutureStatus(lv3.getTickingFuture()), ThreadedAnvilChunkStorage.getFutureStatus(lv3.getEntityTickingFuture()), this.ticketManager.getTicket(entry.getLongKey()), !this.isTooFarFromPlayersToSpawnMobs(lv2), optional2.map(arg -> Stream.of(arg.getEntitySectionArray()).mapToInt(TypeFilterableList::size).sum()).orElse(0), optional2.map(arg -> arg.getBlockEntities().size()).orElse(0));
        }
    }

    private static String getFutureStatus(CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture) {
        try {
            Either either = completableFuture.getNow(null);
            if (either != null) {
                return (String)either.map(arg -> "done", arg -> "unloaded");
            }
            return "not completed";
        }
        catch (CompletionException completionException) {
            return "failed " + completionException.getCause().getMessage();
        }
        catch (CancellationException cancellationException) {
            return "cancelled";
        }
    }

    @Nullable
    private CompoundTag getUpdatedChunkTag(ChunkPos pos) throws IOException {
        CompoundTag lv = this.getNbt(pos);
        if (lv == null) {
            return null;
        }
        return this.updateChunkTag(this.world.getRegistryKey(), this.persistentStateManagerFactory, lv);
    }

    boolean isTooFarFromPlayersToSpawnMobs(ChunkPos arg) {
        long l = arg.toLong();
        if (!this.ticketManager.method_20800(l)) {
            return true;
        }
        return this.playerChunkWatchingManager.getPlayersWatchingChunk(l).noneMatch(arg2 -> !arg2.isSpectator() && ThreadedAnvilChunkStorage.getSquaredDistance(arg, arg2) < 16384.0);
    }

    private boolean doesNotGenerateChunks(ServerPlayerEntity player) {
        return player.isSpectator() && !this.world.getGameRules().getBoolean(GameRules.SPECTATORS_GENERATE_CHUNKS);
    }

    void handlePlayerAddedOrRemoved(ServerPlayerEntity player, boolean added) {
        boolean bl2 = this.doesNotGenerateChunks(player);
        boolean bl3 = this.playerChunkWatchingManager.method_21715(player);
        int i = MathHelper.floor(player.getX()) >> 4;
        int j = MathHelper.floor(player.getZ()) >> 4;
        if (added) {
            this.playerChunkWatchingManager.add(ChunkPos.toLong(i, j), player, bl2);
            this.method_20726(player);
            if (!bl2) {
                this.ticketManager.handleChunkEnter(ChunkSectionPos.from(player), player);
            }
        } else {
            ChunkSectionPos lv = player.getCameraPosition();
            this.playerChunkWatchingManager.remove(lv.toChunkPos().toLong(), player);
            if (!bl3) {
                this.ticketManager.handleChunkLeave(lv, player);
            }
        }
        for (int k = i - this.watchDistance; k <= i + this.watchDistance; ++k) {
            for (int l = j - this.watchDistance; l <= j + this.watchDistance; ++l) {
                ChunkPos lv2 = new ChunkPos(k, l);
                this.sendWatchPackets(player, lv2, new Packet[2], !added, added);
            }
        }
    }

    private ChunkSectionPos method_20726(ServerPlayerEntity arg) {
        ChunkSectionPos lv = ChunkSectionPos.from(arg);
        arg.setCameraPosition(lv);
        arg.networkHandler.sendPacket(new ChunkRenderDistanceCenterS2CPacket(lv.getSectionX(), lv.getSectionZ()));
        return lv;
    }

    public void updateCameraPosition(ServerPlayerEntity player) {
        boolean bl3;
        for (EntityTracker lv : this.entityTrackers.values()) {
            if (lv.entity == player) {
                lv.updateCameraPosition(this.world.getPlayers());
                continue;
            }
            lv.updateCameraPosition(player);
        }
        int i = MathHelper.floor(player.getX()) >> 4;
        int j = MathHelper.floor(player.getZ()) >> 4;
        ChunkSectionPos lv2 = player.getCameraPosition();
        ChunkSectionPos lv3 = ChunkSectionPos.from(player);
        long l = lv2.toChunkPos().toLong();
        long m = lv3.toChunkPos().toLong();
        boolean bl = this.playerChunkWatchingManager.isWatchDisabled(player);
        boolean bl2 = this.doesNotGenerateChunks(player);
        boolean bl4 = bl3 = lv2.asLong() != lv3.asLong();
        if (bl3 || bl != bl2) {
            this.method_20726(player);
            if (!bl) {
                this.ticketManager.handleChunkLeave(lv2, player);
            }
            if (!bl2) {
                this.ticketManager.handleChunkEnter(lv3, player);
            }
            if (!bl && bl2) {
                this.playerChunkWatchingManager.disableWatch(player);
            }
            if (bl && !bl2) {
                this.playerChunkWatchingManager.enableWatch(player);
            }
            if (l != m) {
                this.playerChunkWatchingManager.movePlayer(l, m, player);
            }
        }
        int k = lv2.getSectionX();
        int n = lv2.getSectionZ();
        if (Math.abs(k - i) <= this.watchDistance * 2 && Math.abs(n - j) <= this.watchDistance * 2) {
            int o = Math.min(i, k) - this.watchDistance;
            int p = Math.min(j, n) - this.watchDistance;
            int q = Math.max(i, k) + this.watchDistance;
            int r = Math.max(j, n) + this.watchDistance;
            for (int s = o; s <= q; ++s) {
                for (int t = p; t <= r; ++t) {
                    ChunkPos lv4 = new ChunkPos(s, t);
                    boolean bl42 = ThreadedAnvilChunkStorage.getChebyshevDistance(lv4, k, n) <= this.watchDistance;
                    boolean bl5 = ThreadedAnvilChunkStorage.getChebyshevDistance(lv4, i, j) <= this.watchDistance;
                    this.sendWatchPackets(player, lv4, new Packet[2], bl42, bl5);
                }
            }
        } else {
            for (int u = k - this.watchDistance; u <= k + this.watchDistance; ++u) {
                for (int v = n - this.watchDistance; v <= n + this.watchDistance; ++v) {
                    ChunkPos lv5 = new ChunkPos(u, v);
                    boolean bl6 = true;
                    boolean bl7 = false;
                    this.sendWatchPackets(player, lv5, new Packet[2], true, false);
                }
            }
            for (int w = i - this.watchDistance; w <= i + this.watchDistance; ++w) {
                for (int x = j - this.watchDistance; x <= j + this.watchDistance; ++x) {
                    ChunkPos lv6 = new ChunkPos(w, x);
                    boolean bl8 = false;
                    boolean bl9 = true;
                    this.sendWatchPackets(player, lv6, new Packet[2], false, true);
                }
            }
        }
    }

    @Override
    public Stream<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos chunkPos, boolean onlyOnWatchDistanceEdge) {
        return this.playerChunkWatchingManager.getPlayersWatchingChunk(chunkPos.toLong()).filter(arg2 -> {
            int i = ThreadedAnvilChunkStorage.getChebyshevDistance(chunkPos, arg2, true);
            if (i > this.watchDistance) {
                return false;
            }
            return !onlyOnWatchDistanceEdge || i == this.watchDistance;
        });
    }

    protected void loadEntity(Entity arg) {
        if (arg instanceof EnderDragonPart) {
            return;
        }
        EntityType<?> lv = arg.getType();
        int i = lv.getMaxTrackDistance() * 16;
        int j = lv.getTrackTickInterval();
        if (this.entityTrackers.containsKey(arg.getEntityId())) {
            throw Util.throwOrPause(new IllegalStateException("Entity is already tracked!"));
        }
        EntityTracker lv2 = new EntityTracker(arg, i, j, lv.alwaysUpdateVelocity());
        this.entityTrackers.put(arg.getEntityId(), (Object)lv2);
        lv2.updateCameraPosition(this.world.getPlayers());
        if (arg instanceof ServerPlayerEntity) {
            ServerPlayerEntity lv3 = (ServerPlayerEntity)arg;
            this.handlePlayerAddedOrRemoved(lv3, true);
            for (EntityTracker lv4 : this.entityTrackers.values()) {
                if (lv4.entity == lv3) continue;
                lv4.updateCameraPosition(lv3);
            }
        }
    }

    protected void unloadEntity(Entity arg) {
        EntityTracker lv3;
        if (arg instanceof ServerPlayerEntity) {
            ServerPlayerEntity lv = (ServerPlayerEntity)arg;
            this.handlePlayerAddedOrRemoved(lv, false);
            for (EntityTracker lv2 : this.entityTrackers.values()) {
                lv2.stopTracking(lv);
            }
        }
        if ((lv3 = (EntityTracker)this.entityTrackers.remove(arg.getEntityId())) != null) {
            lv3.stopTracking();
        }
    }

    protected void tickPlayerMovement() {
        ArrayList list = Lists.newArrayList();
        List<ServerPlayerEntity> list2 = this.world.getPlayers();
        for (EntityTracker lv : this.entityTrackers.values()) {
            ChunkSectionPos lv3;
            ChunkSectionPos lv2 = lv.lastCameraPosition;
            if (!Objects.equals(lv2, lv3 = ChunkSectionPos.from(lv.entity))) {
                lv.updateCameraPosition(list2);
                Entity lv4 = lv.entity;
                if (lv4 instanceof ServerPlayerEntity) {
                    list.add((ServerPlayerEntity)lv4);
                }
                lv.lastCameraPosition = lv3;
            }
            lv.entry.tick();
        }
        if (!list.isEmpty()) {
            for (EntityTracker lv5 : this.entityTrackers.values()) {
                lv5.updateCameraPosition(list);
            }
        }
    }

    protected void sendToOtherNearbyPlayers(Entity entity, Packet<?> packet) {
        EntityTracker lv = (EntityTracker)this.entityTrackers.get(entity.getEntityId());
        if (lv != null) {
            lv.sendToOtherNearbyPlayers(packet);
        }
    }

    protected void sendToNearbyPlayers(Entity entity, Packet<?> packet) {
        EntityTracker lv = (EntityTracker)this.entityTrackers.get(entity.getEntityId());
        if (lv != null) {
            lv.sendToNearbyPlayers(packet);
        }
    }

    private void sendChunkDataPackets(ServerPlayerEntity player, Packet<?>[] packets, WorldChunk chunk) {
        if (packets[0] == null) {
            packets[0] = new ChunkDataS2CPacket(chunk, 65535, true);
            packets[1] = new LightUpdateS2CPacket(chunk.getPos(), this.serverLightingProvider, true);
        }
        player.sendInitialChunkPackets(chunk.getPos(), packets[0], packets[1]);
        DebugInfoSender.sendChunkWatchingChange(this.world, chunk.getPos());
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        for (EntityTracker lv : this.entityTrackers.values()) {
            Entity lv2 = lv.entity;
            if (lv2 == player || lv2.chunkX != chunk.getPos().x || lv2.chunkZ != chunk.getPos().z) continue;
            lv.updateCameraPosition(player);
            if (lv2 instanceof MobEntity && ((MobEntity)lv2).getHoldingEntity() != null) {
                list.add(lv2);
            }
            if (lv2.getPassengerList().isEmpty()) continue;
            list2.add(lv2);
        }
        if (!list.isEmpty()) {
            for (Entity lv3 : list) {
                player.networkHandler.sendPacket(new EntityAttachS2CPacket(lv3, ((MobEntity)lv3).getHoldingEntity()));
            }
        }
        if (!list2.isEmpty()) {
            for (Entity lv4 : list2) {
                player.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(lv4));
            }
        }
    }

    protected PointOfInterestStorage getPointOfInterestStorage() {
        return this.pointOfInterestStorage;
    }

    public CompletableFuture<Void> method_20576(WorldChunk arg) {
        return this.mainThreadExecutor.submit(() -> arg.enableTickSchedulers(this.world));
    }

    class EntityTracker {
        private final EntityTrackerEntry entry;
        private final Entity entity;
        private final int maxDistance;
        private ChunkSectionPos lastCameraPosition;
        private final Set<ServerPlayerEntity> playersTracking = Sets.newHashSet();

        public EntityTracker(Entity maxDistance, int tickInterval, int j, boolean bl) {
            this.entry = new EntityTrackerEntry(ThreadedAnvilChunkStorage.this.world, maxDistance, j, bl, this::sendToOtherNearbyPlayers);
            this.entity = maxDistance;
            this.maxDistance = tickInterval;
            this.lastCameraPosition = ChunkSectionPos.from(maxDistance);
        }

        public boolean equals(Object o) {
            if (o instanceof EntityTracker) {
                return ((EntityTracker)o).entity.getEntityId() == this.entity.getEntityId();
            }
            return false;
        }

        public int hashCode() {
            return this.entity.getEntityId();
        }

        public void sendToOtherNearbyPlayers(Packet<?> packet) {
            for (ServerPlayerEntity lv : this.playersTracking) {
                lv.networkHandler.sendPacket(packet);
            }
        }

        public void sendToNearbyPlayers(Packet<?> packet) {
            this.sendToOtherNearbyPlayers(packet);
            if (this.entity instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)this.entity).networkHandler.sendPacket(packet);
            }
        }

        public void stopTracking() {
            for (ServerPlayerEntity lv : this.playersTracking) {
                this.entry.stopTracking(lv);
            }
        }

        public void stopTracking(ServerPlayerEntity arg) {
            if (this.playersTracking.remove(arg)) {
                this.entry.stopTracking(arg);
            }
        }

        public void updateCameraPosition(ServerPlayerEntity player) {
            boolean bl;
            if (player == this.entity) {
                return;
            }
            Vec3d lv = player.getPos().subtract(this.entry.getLastPos());
            int i = Math.min(this.getMaxTrackDistance(), (ThreadedAnvilChunkStorage.this.watchDistance - 1) * 16);
            boolean bl2 = bl = lv.x >= (double)(-i) && lv.x <= (double)i && lv.z >= (double)(-i) && lv.z <= (double)i && this.entity.canBeSpectated(player);
            if (bl) {
                ChunkPos lv2;
                ChunkHolder lv3;
                boolean bl22 = this.entity.teleporting;
                if (!bl22 && (lv3 = ThreadedAnvilChunkStorage.this.getChunkHolder((lv2 = new ChunkPos(this.entity.chunkX, this.entity.chunkZ)).toLong())) != null && lv3.getWorldChunk() != null) {
                    boolean bl3 = bl22 = ThreadedAnvilChunkStorage.getChebyshevDistance(lv2, player, false) <= ThreadedAnvilChunkStorage.this.watchDistance;
                }
                if (bl22 && this.playersTracking.add(player)) {
                    this.entry.startTracking(player);
                }
            } else if (this.playersTracking.remove(player)) {
                this.entry.stopTracking(player);
            }
        }

        private int adjustTrackingDistance(int initialDistance) {
            return ThreadedAnvilChunkStorage.this.world.getServer().adjustTrackingDistance(initialDistance);
        }

        private int getMaxTrackDistance() {
            Collection<Entity> collection = this.entity.getPassengersDeep();
            int i = this.maxDistance;
            for (Entity lv : collection) {
                int j = lv.getType().getMaxTrackDistance() * 16;
                if (j <= i) continue;
                i = j;
            }
            return this.adjustTrackingDistance(i);
        }

        public void updateCameraPosition(List<ServerPlayerEntity> players) {
            for (ServerPlayerEntity lv : players) {
                this.updateCameraPosition(lv);
            }
        }
    }

    class TicketManager
    extends ChunkTicketManager {
        protected TicketManager(Executor mainThreadExecutor, Executor executor2) {
            super(mainThreadExecutor, executor2);
        }

        @Override
        protected boolean isUnloaded(long pos) {
            return ThreadedAnvilChunkStorage.this.unloadedChunks.contains(pos);
        }

        @Override
        @Nullable
        protected ChunkHolder getChunkHolder(long pos) {
            return ThreadedAnvilChunkStorage.this.getCurrentChunkHolder(pos);
        }

        @Override
        @Nullable
        protected ChunkHolder setLevel(long pos, int level, @Nullable ChunkHolder holder, int j) {
            return ThreadedAnvilChunkStorage.this.setLevel(pos, level, holder, j);
        }
    }
}

