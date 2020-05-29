/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.server.world;

import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;

public class ChunkHolder {
    public static final Either<Chunk, Unloaded> UNLOADED_CHUNK = Either.right((Object)Unloaded.INSTANCE);
    public static final CompletableFuture<Either<Chunk, Unloaded>> UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
    public static final Either<WorldChunk, Unloaded> UNLOADED_WORLD_CHUNK = Either.right((Object)Unloaded.INSTANCE);
    private static final CompletableFuture<Either<WorldChunk, Unloaded>> UNLOADED_WORLD_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_WORLD_CHUNK);
    private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.createOrderedList();
    private static final LevelType[] LEVEL_TYPES = LevelType.values();
    private final AtomicReferenceArray<CompletableFuture<Either<Chunk, Unloaded>>> futuresByStatus = new AtomicReferenceArray(CHUNK_STATUSES.size());
    private volatile CompletableFuture<Either<WorldChunk, Unloaded>> borderFuture = UNLOADED_WORLD_CHUNK_FUTURE;
    private volatile CompletableFuture<Either<WorldChunk, Unloaded>> tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
    private volatile CompletableFuture<Either<WorldChunk, Unloaded>> entityTickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
    private CompletableFuture<Chunk> future = CompletableFuture.completedFuture(null);
    private int lastTickLevel;
    private int level;
    private int completedLevel;
    private final ChunkPos pos;
    private final short[] blockUpdatePositions = new short[64];
    private int blockUpdateCount;
    private int sectionsNeedingUpdateMask;
    private int blockLightUpdateBits;
    private int skyLightUpdateBits;
    private final LightingProvider lightingProvider;
    private final LevelUpdateListener levelUpdateListener;
    private final PlayersWatchingChunkProvider playersWatchingChunkProvider;
    private boolean ticking;
    private boolean field_25344;

    public ChunkHolder(ChunkPos arg, int i, LightingProvider arg2, LevelUpdateListener arg3, PlayersWatchingChunkProvider arg4) {
        this.pos = arg;
        this.lightingProvider = arg2;
        this.levelUpdateListener = arg3;
        this.playersWatchingChunkProvider = arg4;
        this.level = this.lastTickLevel = ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
        this.completedLevel = this.lastTickLevel;
        this.setLevel(i);
    }

    public CompletableFuture<Either<Chunk, Unloaded>> getFuture(ChunkStatus arg) {
        CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.futuresByStatus.get(arg.getIndex());
        return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
    }

    public CompletableFuture<Either<Chunk, Unloaded>> getNowFuture(ChunkStatus arg) {
        if (ChunkHolder.getTargetGenerationStatus(this.level).isAtLeast(arg)) {
            return this.getFuture(arg);
        }
        return UNLOADED_CHUNK_FUTURE;
    }

    public CompletableFuture<Either<WorldChunk, Unloaded>> getTickingFuture() {
        return this.tickingFuture;
    }

    public CompletableFuture<Either<WorldChunk, Unloaded>> getEntityTickingFuture() {
        return this.entityTickingFuture;
    }

    public CompletableFuture<Either<WorldChunk, Unloaded>> getBorderFuture() {
        return this.borderFuture;
    }

    @Nullable
    public WorldChunk getWorldChunk() {
        CompletableFuture<Either<WorldChunk, Unloaded>> completableFuture = this.getTickingFuture();
        Either either = completableFuture.getNow(null);
        if (either == null) {
            return null;
        }
        return either.left().orElse(null);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public ChunkStatus method_23270() {
        for (int i = CHUNK_STATUSES.size() - 1; i >= 0; --i) {
            ChunkStatus lv = CHUNK_STATUSES.get(i);
            CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.getFuture(lv);
            if (!completableFuture.getNow(UNLOADED_CHUNK).left().isPresent()) continue;
            return lv;
        }
        return null;
    }

    @Nullable
    public Chunk getCompletedChunk() {
        for (int i = CHUNK_STATUSES.size() - 1; i >= 0; --i) {
            Optional optional;
            ChunkStatus lv = CHUNK_STATUSES.get(i);
            CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.getFuture(lv);
            if (completableFuture.isCompletedExceptionally() || !(optional = completableFuture.getNow(UNLOADED_CHUNK).left()).isPresent()) continue;
            return (Chunk)optional.get();
        }
        return null;
    }

    public CompletableFuture<Chunk> getFuture() {
        return this.future;
    }

    public void markForBlockUpdate(ServerChunkManager arg, int i, int j, int k) {
        WorldChunk lv = this.getWorldChunk();
        if (lv == null) {
            return;
        }
        this.sectionsNeedingUpdateMask |= 1 << (j >> 4);
        if (this.blockUpdateCount < 64) {
            short s = (short)(i << 12 | k << 8 | j);
            for (int l = 0; l < this.blockUpdateCount; ++l) {
                if (this.blockUpdatePositions[l] != s) continue;
                return;
            }
            this.blockUpdatePositions[this.blockUpdateCount++] = s;
            if (this.blockUpdateCount == 64) {
                arg.method_29482(this.pos.x, this.pos.z);
            }
        }
    }

    public void markForLightUpdate(LightType arg, int i) {
        WorldChunk lv = this.getWorldChunk();
        if (lv == null) {
            return;
        }
        lv.setShouldSave(true);
        if (arg == LightType.SKY) {
            this.skyLightUpdateBits |= 1 << i - -1;
        } else {
            this.blockLightUpdateBits |= 1 << i - -1;
        }
    }

    public void flushUpdates(WorldChunk arg) {
        if (this.blockUpdateCount == 0 && this.skyLightUpdateBits == 0 && this.blockLightUpdateBits == 0) {
            return;
        }
        World lv = arg.getWorld();
        if (!(!this.field_25344 && this.blockUpdateCount != 64 || this.skyLightUpdateBits == 0 && this.blockLightUpdateBits == 0)) {
            this.sendPacketToPlayersWatching(new LightUpdateS2CPacket(arg.getPos(), this.lightingProvider, this.skyLightUpdateBits, this.blockLightUpdateBits), false);
        }
        if (this.blockUpdateCount == 1) {
            int i = (this.blockUpdatePositions[0] >> 12 & 0xF) + this.pos.x * 16;
            int j = this.blockUpdatePositions[0] & 0xFF;
            int k = (this.blockUpdatePositions[0] >> 8 & 0xF) + this.pos.z * 16;
            BlockPos lv2 = new BlockPos(i, j, k);
            this.sendPacketToPlayersWatching(new BlockUpdateS2CPacket(lv, lv2), false);
            if (lv.getBlockState(lv2).getBlock().hasBlockEntity()) {
                this.sendBlockEntityUpdatePacket(lv, lv2);
            }
        } else if (this.blockUpdateCount == 64) {
            this.sendPacketToPlayersWatching(new ChunkDataS2CPacket(arg, this.sectionsNeedingUpdateMask), false);
        } else if (this.blockUpdateCount != 0) {
            this.sendPacketToPlayersWatching(new ChunkDeltaUpdateS2CPacket(this.blockUpdateCount, this.blockUpdatePositions, arg), false);
            for (int l = 0; l < this.blockUpdateCount; ++l) {
                int m = (this.blockUpdatePositions[l] >> 12 & 0xF) + this.pos.x * 16;
                int n = this.blockUpdatePositions[l] & 0xFF;
                int o = (this.blockUpdatePositions[l] >> 8 & 0xF) + this.pos.z * 16;
                BlockPos lv3 = new BlockPos(m, n, o);
                if (!lv.getBlockState(lv3).getBlock().hasBlockEntity()) continue;
                this.sendBlockEntityUpdatePacket(lv, lv3);
            }
        }
        this.blockUpdateCount = 0;
        this.sectionsNeedingUpdateMask = 0;
        this.field_25344 = false;
        this.skyLightUpdateBits = 0;
        this.blockLightUpdateBits = 0;
    }

    private void sendBlockEntityUpdatePacket(World arg, BlockPos arg2) {
        BlockEntityUpdateS2CPacket lv2;
        BlockEntity lv = arg.getBlockEntity(arg2);
        if (lv != null && (lv2 = lv.toUpdatePacket()) != null) {
            this.sendPacketToPlayersWatching(lv2, false);
        }
    }

    private void sendPacketToPlayersWatching(Packet<?> arg, boolean bl) {
        this.playersWatchingChunkProvider.getPlayersWatchingChunk(this.pos, bl).forEach(arg2 -> arg2.networkHandler.sendPacket(arg));
    }

    public CompletableFuture<Either<Chunk, Unloaded>> createFuture(ChunkStatus arg, ThreadedAnvilChunkStorage arg2) {
        Either either;
        int i = arg.getIndex();
        CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.futuresByStatus.get(i);
        if (completableFuture != null && ((either = (Either)completableFuture.getNow(null)) == null || either.left().isPresent())) {
            return completableFuture;
        }
        if (ChunkHolder.getTargetGenerationStatus(this.level).isAtLeast(arg)) {
            CompletableFuture<Either<Chunk, Unloaded>> completableFuture2 = arg2.createChunkFuture(this, arg);
            this.updateFuture(completableFuture2);
            this.futuresByStatus.set(i, completableFuture2);
            return completableFuture2;
        }
        return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
    }

    private void updateFuture(CompletableFuture<? extends Either<? extends Chunk, Unloaded>> completableFuture) {
        this.future = this.future.thenCombine(completableFuture, (arg3, either) -> (Chunk)either.map(arg -> arg, arg2 -> arg3));
    }

    @Environment(value=EnvType.CLIENT)
    public LevelType getLevelType() {
        return ChunkHolder.getLevelType(this.level);
    }

    public ChunkPos getPos() {
        return this.pos;
    }

    public int getLevel() {
        return this.level;
    }

    public int getCompletedLevel() {
        return this.completedLevel;
    }

    private void setCompletedLevel(int i) {
        this.completedLevel = i;
    }

    public void setLevel(int i) {
        this.level = i;
    }

    protected void tick(ThreadedAnvilChunkStorage arg) {
        ChunkStatus lv = ChunkHolder.getTargetGenerationStatus(this.lastTickLevel);
        ChunkStatus lv2 = ChunkHolder.getTargetGenerationStatus(this.level);
        boolean bl = this.lastTickLevel <= ThreadedAnvilChunkStorage.MAX_LEVEL;
        boolean bl2 = this.level <= ThreadedAnvilChunkStorage.MAX_LEVEL;
        LevelType lv3 = ChunkHolder.getLevelType(this.lastTickLevel);
        LevelType lv4 = ChunkHolder.getLevelType(this.level);
        if (bl) {
            int i;
            Either either2 = Either.right((Object)new Unloaded(){

                public String toString() {
                    return "Unloaded ticket level " + ChunkHolder.this.pos.toString();
                }
            });
            int n = i = bl2 ? lv2.getIndex() + 1 : 0;
            while (i <= lv.getIndex()) {
                CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.futuresByStatus.get(i);
                if (completableFuture != null) {
                    completableFuture.complete((Either<Chunk, Unloaded>)either2);
                } else {
                    this.futuresByStatus.set(i, CompletableFuture.completedFuture(either2));
                }
                ++i;
            }
        }
        boolean bl3 = lv3.isAfter(LevelType.BORDER);
        boolean bl4 = lv4.isAfter(LevelType.BORDER);
        this.ticking |= bl4;
        if (!bl3 && bl4) {
            this.borderFuture = arg.createBorderFuture(this);
            this.updateFuture(this.borderFuture);
        }
        if (bl3 && !bl4) {
            CompletableFuture<Either<WorldChunk, Unloaded>> completableFuture2 = this.borderFuture;
            this.borderFuture = UNLOADED_WORLD_CHUNK_FUTURE;
            this.updateFuture((CompletableFuture<? extends Either<? extends Chunk, Unloaded>>)completableFuture2.thenApply(either -> either.ifLeft(arg::method_20576)));
        }
        boolean bl5 = lv3.isAfter(LevelType.TICKING);
        boolean bl6 = lv4.isAfter(LevelType.TICKING);
        if (!bl5 && bl6) {
            this.tickingFuture = arg.createTickingFuture(this);
            this.updateFuture(this.tickingFuture);
        }
        if (bl5 && !bl6) {
            this.tickingFuture.complete(UNLOADED_WORLD_CHUNK);
            this.tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
        }
        boolean bl7 = lv3.isAfter(LevelType.ENTITY_TICKING);
        boolean bl8 = lv4.isAfter(LevelType.ENTITY_TICKING);
        if (!bl7 && bl8) {
            if (this.entityTickingFuture != UNLOADED_WORLD_CHUNK_FUTURE) {
                throw Util.throwOrPause(new IllegalStateException());
            }
            this.entityTickingFuture = arg.createEntityTickingChunkFuture(this.pos);
            this.updateFuture(this.entityTickingFuture);
        }
        if (bl7 && !bl8) {
            this.entityTickingFuture.complete(UNLOADED_WORLD_CHUNK);
            this.entityTickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
        }
        this.levelUpdateListener.updateLevel(this.pos, this::getCompletedLevel, this.level, this::setCompletedLevel);
        this.lastTickLevel = this.level;
    }

    public static ChunkStatus getTargetGenerationStatus(int i) {
        if (i < 33) {
            return ChunkStatus.FULL;
        }
        return ChunkStatus.getTargetGenerationStatus(i - 33);
    }

    public static LevelType getLevelType(int i) {
        return LEVEL_TYPES[MathHelper.clamp(33 - i + 1, 0, LEVEL_TYPES.length - 1)];
    }

    public boolean isTicking() {
        return this.ticking;
    }

    public void updateTickingStatus() {
        this.ticking = ChunkHolder.getLevelType(this.level).isAfter(LevelType.BORDER);
    }

    public void method_20456(ReadOnlyChunk arg) {
        for (int i = 0; i < this.futuresByStatus.length(); ++i) {
            Optional optional;
            CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.futuresByStatus.get(i);
            if (completableFuture == null || !(optional = completableFuture.getNow(UNLOADED_CHUNK).left()).isPresent() || !(optional.get() instanceof ProtoChunk)) continue;
            this.futuresByStatus.set(i, CompletableFuture.completedFuture(Either.left((Object)arg)));
        }
        this.updateFuture(CompletableFuture.completedFuture(Either.left((Object)arg.getWrappedChunk())));
    }

    public void method_29481() {
        this.field_25344 = true;
    }

    public static interface PlayersWatchingChunkProvider {
        public Stream<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos var1, boolean var2);
    }

    public static interface LevelUpdateListener {
        public void updateLevel(ChunkPos var1, IntSupplier var2, int var3, IntConsumer var4);
    }

    public static interface Unloaded {
        public static final Unloaded INSTANCE = new Unloaded(){

            public String toString() {
                return "UNLOADED";
            }
        };
    }

    public static enum LevelType {
        INACCESSIBLE,
        BORDER,
        TICKING,
        ENTITY_TICKING;


        public boolean isAfter(LevelType arg) {
            return this.ordinal() >= arg.ordinal();
        }
    }
}

