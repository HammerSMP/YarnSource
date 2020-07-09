/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  it.unimi.dsi.fastutil.shorts.ShortArraySet
 *  it.unimi.dsi.fastutil.shorts.ShortSet
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.server.world;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.shorts.ShortArraySet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
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
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
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
    private boolean field_25803;
    private final ShortSet[] field_25804 = new ShortSet[16];
    private int blockLightUpdateBits;
    private int skyLightUpdateBits;
    private final LightingProvider lightingProvider;
    private final LevelUpdateListener levelUpdateListener;
    private final PlayersWatchingChunkProvider playersWatchingChunkProvider;
    private boolean ticking;

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

    public void markForBlockUpdate(BlockPos pos) {
        WorldChunk lv = this.getWorldChunk();
        if (lv == null) {
            return;
        }
        byte b = (byte)ChunkSectionPos.getSectionCoord(pos.getY());
        if (this.field_25804[b] == null) {
            this.field_25803 = true;
            this.field_25804[b] = new ShortArraySet();
        }
        this.field_25804[b].add(ChunkSectionPos.getPackedLocalPos(pos));
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
        if (!this.field_25803 && this.skyLightUpdateBits == 0 && this.blockLightUpdateBits == 0) {
            return;
        }
        World lv = arg.getWorld();
        if (this.skyLightUpdateBits != 0 || this.blockLightUpdateBits != 0) {
            this.sendPacketToPlayersWatching(new LightUpdateS2CPacket(arg.getPos(), this.lightingProvider, this.skyLightUpdateBits, this.blockLightUpdateBits, false), true);
            this.skyLightUpdateBits = 0;
            this.blockLightUpdateBits = 0;
        }
        for (int i = 0; i < this.field_25804.length; ++i) {
            ShortSet shortSet = this.field_25804[i];
            if (shortSet == null) continue;
            ChunkSectionPos lv2 = ChunkSectionPos.from(arg.getPos(), i);
            if (shortSet.size() == 1) {
                BlockPos lv3 = lv2.method_30557(shortSet.iterator().nextShort());
                BlockState lv4 = lv.getBlockState(lv3);
                this.sendPacketToPlayersWatching(new BlockUpdateS2CPacket(lv3, lv4), false);
                this.method_30311(lv, lv3, lv4);
            } else {
                ChunkSection lv5 = arg.getSectionArray()[lv2.getY()];
                ChunkDeltaUpdateS2CPacket lv6 = new ChunkDeltaUpdateS2CPacket(lv2, shortSet, lv5);
                this.sendPacketToPlayersWatching(lv6, false);
                lv6.method_30621((arg2, arg3) -> this.method_30311(lv, (BlockPos)arg2, (BlockState)arg3));
            }
            this.field_25804[i] = null;
        }
        this.field_25803 = false;
    }

    private void method_30311(World arg, BlockPos arg2, BlockState arg3) {
        if (arg3.getBlock().hasBlockEntity()) {
            this.sendBlockEntityUpdatePacket(arg, arg2);
        }
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

