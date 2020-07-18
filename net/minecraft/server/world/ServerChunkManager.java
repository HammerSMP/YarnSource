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
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.LightType;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
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

    public ServerChunkManager(ServerWorld arg, LevelStorage.Session arg2, DataFixer dataFixer, StructureManager structureManager, Executor workerExecutor, ChunkGenerator chunkGenerator, int viewDistance, boolean bl, WorldGenerationProgressListener arg5, Supplier<PersistentStateManager> supplier) {
        this.world = arg;
        this.mainThreadExecutor = new MainThreadExecutor(arg);
        this.chunkGenerator = chunkGenerator;
        this.serverThread = Thread.currentThread();
        File file = arg2.getWorldDirectory(arg.getRegistryKey());
        File file2 = new File(file, "data");
        file2.mkdirs();
        this.persistentStateManager = new PersistentStateManager(file2, dataFixer);
        this.threadedAnvilChunkStorage = new ThreadedAnvilChunkStorage(arg, arg2, dataFixer, structureManager, workerExecutor, this.mainThreadExecutor, this, this.getChunkGenerator(), arg5, supplier, viewDistance, bl);
        this.lightProvider = this.threadedAnvilChunkStorage.getLightProvider();
        this.ticketManager = this.threadedAnvilChunkStorage.getTicketManager();
        this.initChunkCaches();
    }

    @Override
    public ServerLightingProvider getLightingProvider() {
        return this.lightProvider;
    }

    @Nullable
    private ChunkHolder getChunkHolder(long pos) {
        return this.threadedAnvilChunkStorage.getChunkHolder(pos);
    }

    public int getTotalChunksLoadedCount() {
        return this.threadedAnvilChunkStorage.getTotalChunksLoadedCount();
    }

    private void putInCache(long pos, Chunk chunk, ChunkStatus status) {
        for (int i = 3; i > 0; --i) {
            this.chunkPosCache[i] = this.chunkPosCache[i - 1];
            this.chunkStatusCache[i] = this.chunkStatusCache[i - 1];
            this.chunkCache[i] = this.chunkCache[i - 1];
        }
        this.chunkPosCache[0] = pos;
        this.chunkStatusCache[0] = status;
        this.chunkCache[0] = chunk;
    }

    @Override
    @Nullable
    public Chunk getChunk(int x, int z, ChunkStatus leastStatus, boolean create) {
        if (Thread.currentThread() != this.serverThread) {
            return CompletableFuture.supplyAsync(() -> this.getChunk(x, z, leastStatus, create), this.mainThreadExecutor).join();
        }
        Profiler lv = this.world.getProfiler();
        lv.visit("getChunk");
        long l = ChunkPos.toLong(x, z);
        for (int k = 0; k < 4; ++k) {
            Chunk lv2;
            if (l != this.chunkPosCache[k] || leastStatus != this.chunkStatusCache[k] || (lv2 = this.chunkCache[k]) == null && create) continue;
            return lv2;
        }
        lv.visit("getChunkCacheMiss");
        CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = this.getChunkFuture(x, z, leastStatus, create);
        this.mainThreadExecutor.runTasks(completableFuture::isDone);
        Chunk lv3 = (Chunk)completableFuture.join().map(arg -> arg, arg -> {
            if (create) {
                throw Util.throwOrPause(new IllegalStateException("Chunk not there when requested: " + arg));
            }
            return null;
        });
        this.putInCache(l, lv3, leastStatus);
        return lv3;
    }

    @Override
    @Nullable
    public WorldChunk getWorldChunk(int chunkX, int chunkZ) {
        if (Thread.currentThread() != this.serverThread) {
            return null;
        }
        this.world.getProfiler().visit("getChunkNow");
        long l = ChunkPos.toLong(chunkX, chunkZ);
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
    public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkFutureSyncOnMainThread(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        CompletionStage completableFuture2;
        boolean bl2;
        boolean bl = bl2 = Thread.currentThread() == this.serverThread;
        if (bl2) {
            CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture3 = this.getChunkFuture(chunkX, chunkZ, leastStatus, create);
            this.mainThreadExecutor.runTasks(completableFuture3::isDone);
        } else {
            completableFuture2 = CompletableFuture.supplyAsync(() -> this.getChunkFuture(chunkX, chunkZ, leastStatus, create), this.mainThreadExecutor).thenCompose(completableFuture -> completableFuture);
        }
        return completableFuture2;
    }

    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkFuture(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        ChunkPos lv = new ChunkPos(chunkX, chunkZ);
        long l = lv.toLong();
        int k = 33 + ChunkStatus.getTargetGenerationRadius(leastStatus);
        ChunkHolder lv2 = this.getChunkHolder(l);
        if (create) {
            this.ticketManager.addTicketWithLevel(ChunkTicketType.field_14032, lv, k, lv);
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
        return lv2.createFuture(leastStatus, this.threadedAnvilChunkStorage);
    }

    private boolean isMissingForLevel(@Nullable ChunkHolder holder, int maxLevel) {
        return holder == null || holder.getLevel() > maxLevel;
    }

    @Override
    public boolean isChunkLoaded(int x, int z) {
        int k;
        ChunkHolder lv = this.getChunkHolder(new ChunkPos(x, z).toLong());
        return !this.isMissingForLevel(lv, k = 33 + ChunkStatus.getTargetGenerationRadius(ChunkStatus.FULL));
    }

    @Override
    public BlockView getChunk(int chunkX, int chunkZ) {
        long l = ChunkPos.toLong(chunkX, chunkZ);
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
    public boolean shouldTickEntity(Entity entity) {
        long l = ChunkPos.toLong(MathHelper.floor(entity.getX()) >> 4, MathHelper.floor(entity.getZ()) >> 4);
        return this.isFutureReady(l, ChunkHolder::getEntityTickingFuture);
    }

    @Override
    public boolean shouldTickChunk(ChunkPos pos) {
        return this.isFutureReady(pos.toLong(), ChunkHolder::getEntityTickingFuture);
    }

    @Override
    public boolean shouldTickBlock(BlockPos pos) {
        long l = ChunkPos.toLong(pos.getX() >> 4, pos.getZ() >> 4);
        return this.isFutureReady(l, ChunkHolder::getTickingFuture);
    }

    private boolean isFutureReady(long pos, Function<ChunkHolder, CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>>> futureFunction) {
        ChunkHolder lv = this.getChunkHolder(pos);
        if (lv == null) {
            return false;
        }
        Either<WorldChunk, ChunkHolder.Unloaded> either = futureFunction.apply(lv).getNow(ChunkHolder.UNLOADED_WORLD_CHUNK);
        return either.left().isPresent();
    }

    public void save(boolean flush) {
        this.tick();
        this.threadedAnvilChunkStorage.save(flush);
    }

    @Override
    public void close() throws IOException {
        this.save(true);
        this.lightProvider.close();
        this.threadedAnvilChunkStorage.close();
    }

    public void tick(BooleanSupplier shouldKeepTicking) {
        this.world.getProfiler().push("purge");
        this.ticketManager.purge();
        this.tick();
        this.world.getProfiler().swap("chunks");
        this.tickChunks();
        this.world.getProfiler().swap("unload");
        this.threadedAnvilChunkStorage.tick(shouldKeepTicking);
        this.world.getProfiler().pop();
        this.initChunkCaches();
    }

    private void tickChunks() {
        long l = this.world.getTime();
        long m = l - this.lastMobSpawningTime;
        this.lastMobSpawningTime = l;
        WorldProperties lv = this.world.getLevelProperties();
        boolean bl = this.world.isDebugWorld();
        boolean bl2 = this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING);
        if (!bl) {
            SpawnHelper.Info info;
            this.world.getProfiler().push("pollingChunks");
            int i = this.world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
            boolean bl3 = lv.getTime() % 400L == 0L;
            this.world.getProfiler().push("naturalSpawnCount");
            int j = this.ticketManager.getSpawningChunkCount();
            this.spawnEntry = info = SpawnHelper.setupSpawn(j, this.world.iterateEntities(), (arg_0, arg_1) -> this.ifChunkLoaded(arg_0, arg_1));
            this.world.getProfiler().pop();
            ArrayList list = Lists.newArrayList(this.threadedAnvilChunkStorage.entryIterator());
            Collections.shuffle(list);
            list.forEach(arg2 -> {
                Optional optional = arg2.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left();
                if (!optional.isPresent()) {
                    return;
                }
                this.world.getProfiler().push("broadcast");
                arg2.flushUpdates((WorldChunk)optional.get());
                this.world.getProfiler().pop();
                Optional optional2 = arg2.getEntityTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left();
                if (!optional2.isPresent()) {
                    return;
                }
                WorldChunk worldChunk = (WorldChunk)optional2.get();
                ChunkPos cringe = arg2.getPos();
                if (this.threadedAnvilChunkStorage.isTooFarFromPlayersToSpawnMobs(cringe)) {
                    return;
                }
                worldChunk.setInhabitedTime(worldChunk.getInhabitedTime() + m);
                if (bl2 && (this.spawnMonsters || this.spawnAnimals) && this.world.getWorldBorder().contains(worldChunk.getPos())) {
                    SpawnHelper.spawn(this.world, worldChunk, info, this.spawnAnimals, this.spawnMonsters, bl3);
                }
                this.world.tickChunk(worldChunk, i);
            });
            this.world.getProfiler().push("customSpawners");
            if (bl2) {
                this.world.tickSpawners(this.spawnMonsters, this.spawnAnimals);
            }
            this.world.getProfiler().pop();
            this.world.getProfiler().pop();
        }
        this.threadedAnvilChunkStorage.tickPlayerMovement();
    }

    private void ifChunkLoaded(long pos, Consumer<WorldChunk> chunkConsumer) {
        ChunkHolder lv = this.getChunkHolder(pos);
        if (lv != null) {
            lv.getBorderFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left().ifPresent(chunkConsumer);
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

    public void markForUpdate(BlockPos pos) {
        int j;
        int i = pos.getX() >> 4;
        ChunkHolder lv = this.getChunkHolder(ChunkPos.toLong(i, j = pos.getZ() >> 4));
        if (lv != null) {
            lv.markForBlockUpdate(pos);
        }
    }

    @Override
    public void onLightUpdate(LightType type, ChunkSectionPos pos) {
        this.mainThreadExecutor.execute(() -> {
            ChunkHolder lv = this.getChunkHolder(pos.toChunkPos().toLong());
            if (lv != null) {
                lv.markForLightUpdate(type, pos.getSectionY());
            }
        });
    }

    public <T> void addTicket(ChunkTicketType<T> ticketType, ChunkPos pos, int radius, T argument) {
        this.ticketManager.addTicket(ticketType, pos, radius, argument);
    }

    public <T> void removeTicket(ChunkTicketType<T> ticketType, ChunkPos pos, int radius, T argument) {
        this.ticketManager.removeTicket(ticketType, pos, radius, argument);
    }

    @Override
    public void setChunkForced(ChunkPos pos, boolean forced) {
        this.ticketManager.setChunkForced(pos, forced);
    }

    public void updateCameraPosition(ServerPlayerEntity player) {
        this.threadedAnvilChunkStorage.updateCameraPosition(player);
    }

    public void unloadEntity(Entity arg) {
        this.threadedAnvilChunkStorage.unloadEntity(arg);
    }

    public void loadEntity(Entity arg) {
        this.threadedAnvilChunkStorage.loadEntity(arg);
    }

    public void sendToNearbyPlayers(Entity entity, Packet<?> packet) {
        this.threadedAnvilChunkStorage.sendToNearbyPlayers(entity, packet);
    }

    public void sendToOtherNearbyPlayers(Entity arg, Packet<?> arg2) {
        this.threadedAnvilChunkStorage.sendToOtherNearbyPlayers(arg, arg2);
    }

    public void applyViewDistance(int watchDistance) {
        this.threadedAnvilChunkStorage.setViewDistance(watchDistance);
    }

    @Override
    public void setMobSpawnOptions(boolean spawnMonsters, boolean spawnAnimals) {
        this.spawnMonsters = spawnMonsters;
        this.spawnAnimals = spawnAnimals;
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
            super("Chunk source main thread executor for " + arg2.getRegistryKey().getValue());
        }

        @Override
        protected Runnable createTask(Runnable runnable) {
            return runnable;
        }

        @Override
        protected boolean canExecute(Runnable task) {
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
        protected void executeTask(Runnable task) {
            ServerChunkManager.this.world.getProfiler().visit("runTask");
            super.executeTask(task);
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

