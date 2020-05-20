/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Either
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5217;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.LightType;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.poi.PointOfInterestStorage;

public class ServerChunkManager
extends ChunkManager {
    private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.createOrderedList();
    private final ChunkTicketManager ticketManager;
    private final ChunkGenerator chunkGenerator;
    private final ServerWorld world;
    private final Thread serverThread;
    private final ServerLightingProvider lightProvider;
    private final MainThreadExecutor mainThreadExecutor;
    public final ThreadedAnvilChunkStorage threadedAnvilChunkStorage;
    private final PersistentStateManager persistentStateManager;
    private long lastMobSpawningTime;
    private boolean spawnMonsters = true;
    private boolean spawnAnimals = true;
    private final long[] chunkPosCache = new long[4];
    private final ChunkStatus[] chunkStatusCache = new ChunkStatus[4];
    private final Chunk[] chunkCache = new Chunk[4];
    @Nullable
    private SpawnHelper.Info spawnEntry;

    public ServerChunkManager(ServerWorld arg, LevelStorage.Session arg2, DataFixer dataFixer, StructureManager arg3, Executor executor, ChunkGenerator arg4, int i, boolean bl, WorldGenerationProgressListener arg5, Supplier<PersistentStateManager> supplier) {
        this.world = arg;
        this.mainThreadExecutor = new MainThreadExecutor(arg);
        this.chunkGenerator = arg4;
        this.serverThread = Thread.currentThread();
        File file = arg2.method_27424(arg.method_27983());
        File file2 = new File(file, "data");
        file2.mkdirs();
        this.persistentStateManager = new PersistentStateManager(file2, dataFixer);
        this.threadedAnvilChunkStorage = new ThreadedAnvilChunkStorage(arg, arg2, dataFixer, arg3, executor, this.mainThreadExecutor, this, this.getChunkGenerator(), arg5, supplier, i, bl);
        this.lightProvider = this.threadedAnvilChunkStorage.getLightProvider();
        this.ticketManager = this.threadedAnvilChunkStorage.getTicketManager();
        this.initChunkCaches();
    }

    @Override
    public ServerLightingProvider getLightingProvider() {
        return this.lightProvider;
    }

    @Nullable
    private ChunkHolder getChunkHolder(long l) {
        return this.threadedAnvilChunkStorage.getChunkHolder(l);
    }

    public int getTotalChunksLoadedCount() {
        return this.threadedAnvilChunkStorage.getTotalChunksLoadedCount();
    }

    private void putInCache(long l, Chunk arg, ChunkStatus arg2) {
        for (int i = 3; i > 0; --i) {
            this.chunkPosCache[i] = this.chunkPosCache[i - 1];
            this.chunkStatusCache[i] = this.chunkStatusCache[i - 1];
            this.chunkCache[i] = this.chunkCache[i - 1];
        }
        this.chunkPosCache[0] = l;
        this.chunkStatusCache[0] = arg2;
        this.chunkCache[0] = arg;
    }

    @Override
    @Nullable
    public Chunk getChunk(int i, int j, ChunkStatus arg2, boolean bl) {
        if (Thread.currentThread() != this.serverThread) {
            return CompletableFuture.supplyAsync(() -> this.getChunk(i, j, arg2, bl), this.mainThreadExecutor).join();
        }
        Profiler lv = this.world.getProfiler();
        lv.visit("getChunk");
        long l = ChunkPos.toLong(i, j);
        for (int k = 0; k < 4; ++k) {
            Chunk lv2;
            if (l != this.chunkPosCache[k] || arg2 != this.chunkStatusCache[k] || (lv2 = this.chunkCache[k]) == null && bl) continue;
            return lv2;
        }
        lv.visit("getChunkCacheMiss");
        CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = this.getChunkFuture(i, j, arg2, bl);
        this.mainThreadExecutor.runTasks(completableFuture::isDone);
        Chunk lv3 = (Chunk)completableFuture.join().map(arg -> arg, arg -> {
            if (bl) {
                throw Util.throwOrPause(new IllegalStateException("Chunk not there when requested: " + arg));
            }
            return null;
        });
        this.putInCache(l, lv3, arg2);
        return lv3;
    }

    @Override
    @Nullable
    public WorldChunk getWorldChunk(int i, int j) {
        if (Thread.currentThread() != this.serverThread) {
            return null;
        }
        this.world.getProfiler().visit("getChunkNow");
        long l = ChunkPos.toLong(i, j);
        for (int k = 0; k < 4; ++k) {
            if (l != this.chunkPosCache[k] || this.chunkStatusCache[k] != ChunkStatus.FULL) continue;
            Chunk lv = this.chunkCache[k];
            return lv instanceof WorldChunk ? (WorldChunk)lv : null;
        }
        ChunkHolder lv2 = this.getChunkHolder(l);
        if (lv2 == null) {
            return null;
        }
        Either either = lv2.getNowFuture(ChunkStatus.FULL).getNow(null);
        if (either == null) {
            return null;
        }
        Chunk lv3 = either.left().orElse(null);
        if (lv3 != null) {
            this.putInCache(l, lv3, ChunkStatus.FULL);
            if (lv3 instanceof WorldChunk) {
                return (WorldChunk)lv3;
            }
        }
        return null;
    }

    private void initChunkCaches() {
        Arrays.fill(this.chunkPosCache, ChunkPos.MARKER);
        Arrays.fill(this.chunkStatusCache, null);
        Arrays.fill(this.chunkCache, null);
    }

    @Environment(value=EnvType.CLIENT)
    public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkFutureSyncOnMainThread(int i, int j, ChunkStatus arg, boolean bl) {
        CompletionStage completableFuture2;
        boolean bl2;
        boolean bl3 = bl2 = Thread.currentThread() == this.serverThread;
        if (bl2) {
            CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture3 = this.getChunkFuture(i, j, arg, bl);
            this.mainThreadExecutor.runTasks(completableFuture3::isDone);
        } else {
            completableFuture2 = CompletableFuture.supplyAsync(() -> this.getChunkFuture(i, j, arg, bl), this.mainThreadExecutor).thenCompose(completableFuture -> completableFuture);
        }
        return completableFuture2;
    }

    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkFuture(int i, int j, ChunkStatus arg, boolean bl) {
        ChunkPos lv = new ChunkPos(i, j);
        long l = lv.toLong();
        int k = 33 + ChunkStatus.getTargetGenerationRadius(arg);
        ChunkHolder lv2 = this.getChunkHolder(l);
        if (bl) {
            this.ticketManager.addTicketWithLevel(ChunkTicketType.UNKNOWN, lv, k, lv);
            if (this.isMissingForLevel(lv2, k)) {
                Profiler lv3 = this.world.getProfiler();
                lv3.push("chunkLoad");
                this.tick();
                lv2 = this.getChunkHolder(l);
                lv3.pop();
                if (this.isMissingForLevel(lv2, k)) {
                    throw Util.throwOrPause(new IllegalStateException("No chunk holder after ticket has been added"));
                }
            }
        }
        if (this.isMissingForLevel(lv2, k)) {
            return ChunkHolder.UNLOADED_CHUNK_FUTURE;
        }
        return lv2.createFuture(arg, this.threadedAnvilChunkStorage);
    }

    private boolean isMissingForLevel(@Nullable ChunkHolder arg, int i) {
        return arg == null || arg.getLevel() > i;
    }

    @Override
    public boolean isChunkLoaded(int i, int j) {
        int k;
        ChunkHolder lv = this.getChunkHolder(new ChunkPos(i, j).toLong());
        return !this.isMissingForLevel(lv, k = 33 + ChunkStatus.getTargetGenerationRadius(ChunkStatus.FULL));
    }

    @Override
    public BlockView getChunk(int i, int j) {
        long l = ChunkPos.toLong(i, j);
        ChunkHolder lv = this.getChunkHolder(l);
        if (lv == null) {
            return null;
        }
        int k = CHUNK_STATUSES.size() - 1;
        do {
            ChunkStatus lv2;
            Optional optional;
            if ((optional = lv.getFuture(lv2 = CHUNK_STATUSES.get(k)).getNow(ChunkHolder.UNLOADED_CHUNK).left()).isPresent()) {
                return (BlockView)optional.get();
            }
            if (lv2 == ChunkStatus.LIGHT.getPrevious()) break;
            --k;
        } while (true);
        return null;
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    public boolean executeQueuedTasks() {
        return this.mainThreadExecutor.runTask();
    }

    private boolean tick() {
        boolean bl = this.ticketManager.tick(this.threadedAnvilChunkStorage);
        boolean bl2 = this.threadedAnvilChunkStorage.updateHolderMap();
        if (bl || bl2) {
            this.initChunkCaches();
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldTickEntity(Entity arg) {
        long l = ChunkPos.toLong(MathHelper.floor(arg.getX()) >> 4, MathHelper.floor(arg.getZ()) >> 4);
        return this.isFutureReady(l, ChunkHolder::getEntityTickingFuture);
    }

    @Override
    public boolean shouldTickChunk(ChunkPos arg) {
        return this.isFutureReady(arg.toLong(), ChunkHolder::getEntityTickingFuture);
    }

    @Override
    public boolean shouldTickBlock(BlockPos arg) {
        long l = ChunkPos.toLong(arg.getX() >> 4, arg.getZ() >> 4);
        return this.isFutureReady(l, ChunkHolder::getTickingFuture);
    }

    private boolean isFutureReady(long l, Function<ChunkHolder, CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>>> function) {
        ChunkHolder lv = this.getChunkHolder(l);
        if (lv == null) {
            return false;
        }
        Either<WorldChunk, ChunkHolder.Unloaded> either = function.apply(lv).getNow(ChunkHolder.UNLOADED_WORLD_CHUNK);
        return either.left().isPresent();
    }

    public void save(boolean bl) {
        this.tick();
        this.threadedAnvilChunkStorage.save(bl);
    }

    @Override
    public void close() throws IOException {
        this.save(true);
        this.lightProvider.close();
        this.threadedAnvilChunkStorage.close();
    }

    public void tick(BooleanSupplier booleanSupplier) {
        this.world.getProfiler().push("purge");
        this.ticketManager.purge();
        this.tick();
        this.world.getProfiler().swap("chunks");
        this.tickChunks();
        this.world.getProfiler().swap("unload");
        this.threadedAnvilChunkStorage.tick(booleanSupplier);
        this.world.getProfiler().pop();
        this.initChunkCaches();
    }

    private void tickChunks() {
        long l = this.world.getTime();
        long m = l - this.lastMobSpawningTime;
        this.lastMobSpawningTime = l;
        class_5217 lv = this.world.getLevelProperties();
        boolean bl = this.world.method_27982();
        boolean bl2 = this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING);
        if (!bl) {
            SpawnHelper.Info lv2;
            this.world.getProfiler().push("pollingChunks");
            int i = this.world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
            boolean bl3 = lv.getTime() % 400L == 0L;
            this.world.getProfiler().push("naturalSpawnCount");
            int j = this.ticketManager.getSpawningChunkCount();
            this.spawnEntry = lv2 = SpawnHelper.setupSpawn(j, this.world.iterateEntities(), (arg_0, arg_1) -> this.ifChunkLoaded(arg_0, arg_1));
            this.world.getProfiler().pop();
            ArrayList list = Lists.newArrayList(this.threadedAnvilChunkStorage.entryIterator());
            Collections.shuffle(list);
            list.forEach(arg2 -> {
                Optional optional = arg2.getEntityTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left();
                if (!optional.isPresent()) {
                    return;
                }
                WorldChunk lv = (WorldChunk)optional.get();
                this.world.getProfiler().push("broadcast");
                arg2.flushUpdates(lv);
                this.world.getProfiler().pop();
                ChunkPos lv2 = arg2.getPos();
                if (this.threadedAnvilChunkStorage.isTooFarFromPlayersToSpawnMobs(lv2)) {
                    return;
                }
                lv.setInhabitedTime(lv.getInhabitedTime() + m);
                if (bl2 && (this.spawnMonsters || this.spawnAnimals) && this.world.getWorldBorder().contains(lv.getPos())) {
                    SpawnHelper.spawn(this.world, lv, lv2, this.spawnAnimals, this.spawnMonsters, bl3);
                }
                this.world.tickChunk(lv, i);
            });
            this.world.getProfiler().push("customSpawners");
            if (bl2) {
                this.chunkGenerator.spawnEntities(this.world, this.spawnMonsters, this.spawnAnimals);
            }
            this.world.getProfiler().pop();
            this.world.getProfiler().pop();
        }
        this.threadedAnvilChunkStorage.tickPlayerMovement();
    }

    private void ifChunkLoaded(long l, Consumer<WorldChunk> consumer) {
        ChunkHolder lv = this.getChunkHolder(l);
        if (lv != null) {
            lv.getBorderFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left().ifPresent(consumer);
        }
    }

    @Override
    public String getDebugString() {
        return "ServerChunkCache: " + this.getLoadedChunkCount();
    }

    @VisibleForTesting
    public int getPendingTasks() {
        return this.mainThreadExecutor.getTaskCount();
    }

    public ChunkGenerator getChunkGenerator() {
        return this.chunkGenerator;
    }

    public int getLoadedChunkCount() {
        return this.threadedAnvilChunkStorage.getLoadedChunkCount();
    }

    public void markForUpdate(BlockPos arg) {
        int j;
        int i = arg.getX() >> 4;
        ChunkHolder lv = this.getChunkHolder(ChunkPos.toLong(i, j = arg.getZ() >> 4));
        if (lv != null) {
            lv.markForBlockUpdate(arg.getX() & 0xF, arg.getY(), arg.getZ() & 0xF);
        }
    }

    @Override
    public void onLightUpdate(LightType arg, ChunkSectionPos arg2) {
        this.mainThreadExecutor.execute(() -> {
            ChunkHolder lv = this.getChunkHolder(arg2.toChunkPos().toLong());
            if (lv != null) {
                lv.markForLightUpdate(arg, arg2.getSectionY());
            }
        });
    }

    public <T> void addTicket(ChunkTicketType<T> arg, ChunkPos arg2, int i, T object) {
        this.ticketManager.addTicket(arg, arg2, i, object);
    }

    public <T> void removeTicket(ChunkTicketType<T> arg, ChunkPos arg2, int i, T object) {
        this.ticketManager.removeTicket(arg, arg2, i, object);
    }

    @Override
    public void setChunkForced(ChunkPos arg, boolean bl) {
        this.ticketManager.setChunkForced(arg, bl);
    }

    public void updateCameraPosition(ServerPlayerEntity arg) {
        this.threadedAnvilChunkStorage.updateCameraPosition(arg);
    }

    public void unloadEntity(Entity arg) {
        this.threadedAnvilChunkStorage.unloadEntity(arg);
    }

    public void loadEntity(Entity arg) {
        this.threadedAnvilChunkStorage.loadEntity(arg);
    }

    public void sendToNearbyPlayers(Entity arg, Packet<?> arg2) {
        this.threadedAnvilChunkStorage.sendToNearbyPlayers(arg, arg2);
    }

    public void sendToOtherNearbyPlayers(Entity arg, Packet<?> arg2) {
        this.threadedAnvilChunkStorage.sendToOtherNearbyPlayers(arg, arg2);
    }

    public void applyViewDistance(int i) {
        this.threadedAnvilChunkStorage.setViewDistance(i);
    }

    @Override
    public void setMobSpawnOptions(boolean bl, boolean bl2) {
        this.spawnMonsters = bl;
        this.spawnAnimals = bl2;
    }

    @Environment(value=EnvType.CLIENT)
    public String method_23273(ChunkPos arg) {
        return this.threadedAnvilChunkStorage.method_23272(arg);
    }

    public PersistentStateManager getPersistentStateManager() {
        return this.persistentStateManager;
    }

    public PointOfInterestStorage getPointOfInterestStorage() {
        return this.threadedAnvilChunkStorage.getPointOfInterestStorage();
    }

    @Nullable
    public SpawnHelper.Info getSpawnInfo() {
        return this.spawnEntry;
    }

    @Override
    public /* synthetic */ LightingProvider getLightingProvider() {
        return this.getLightingProvider();
    }

    @Override
    public /* synthetic */ BlockView getWorld() {
        return this.getWorld();
    }

    final class MainThreadExecutor
    extends ThreadExecutor<Runnable> {
        private MainThreadExecutor(World arg2) {
            super("Chunk source main thread executor for " + Registry.DIMENSION_TYPE.getId(arg2.method_27983()));
        }

        @Override
        protected Runnable createTask(Runnable runnable) {
            return runnable;
        }

        @Override
        protected boolean canExecute(Runnable runnable) {
            return true;
        }

        @Override
        protected boolean shouldExecuteAsync() {
            return true;
        }

        @Override
        protected Thread getThread() {
            return ServerChunkManager.this.serverThread;
        }

        @Override
        protected void executeTask(Runnable runnable) {
            ServerChunkManager.this.world.getProfiler().visit("runTask");
            super.executeTask(runnable);
        }

        @Override
        protected boolean runTask() {
            if (ServerChunkManager.this.tick()) {
                return true;
            }
            ServerChunkManager.this.lightProvider.tick();
            return super.runTask();
        }
    }
}

