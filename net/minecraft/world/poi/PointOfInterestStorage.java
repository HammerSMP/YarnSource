/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 */
package net.minecraft.world.poi;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.SectionDistanceLevelPropagator;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestSet;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.storage.SerializingRegionBasedStorage;

public class PointOfInterestStorage
extends SerializingRegionBasedStorage<PointOfInterestSet> {
    private final PointOfInterestDistanceTracker pointOfInterestDistanceTracker;
    private final LongSet preloadedChunks = new LongOpenHashSet();

    public PointOfInterestStorage(File file, DataFixer dataFixer, boolean bl) {
        super(file, PointOfInterestSet::method_28364, PointOfInterestSet::new, dataFixer, DataFixTypes.POI_CHUNK, bl);
        this.pointOfInterestDistanceTracker = new PointOfInterestDistanceTracker();
    }

    public void add(BlockPos arg, PointOfInterestType arg2) {
        ((PointOfInterestSet)this.getOrCreate(ChunkSectionPos.from(arg).asLong())).add(arg, arg2);
    }

    public void remove(BlockPos arg) {
        ((PointOfInterestSet)this.getOrCreate(ChunkSectionPos.from(arg).asLong())).remove(arg);
    }

    public long count(Predicate<PointOfInterestType> predicate, BlockPos arg, int i, OccupationStatus arg2) {
        return this.getInCircle(predicate, arg, i, arg2).count();
    }

    public boolean method_26339(PointOfInterestType arg, BlockPos arg2) {
        Optional<PointOfInterestType> optional = ((PointOfInterestSet)this.getOrCreate(ChunkSectionPos.from(arg2).asLong())).getType(arg2);
        return optional.isPresent() && optional.get().equals(arg);
    }

    public Stream<PointOfInterest> getInSquare(Predicate<PointOfInterestType> predicate, BlockPos arg, int i, OccupationStatus arg22) {
        int j = Math.floorDiv(i, 16) + 1;
        return ChunkPos.stream(new ChunkPos(arg), j).flatMap(arg2 -> this.getInChunk(predicate, (ChunkPos)arg2, arg22));
    }

    public Stream<PointOfInterest> getInCircle(Predicate<PointOfInterestType> predicate, BlockPos arg, int i, OccupationStatus arg22) {
        int j = i * i;
        return this.getInSquare(predicate, arg, i, arg22).filter(arg2 -> arg2.getPos().getSquaredDistance(arg) <= (double)j);
    }

    public Stream<PointOfInterest> getInChunk(Predicate<PointOfInterestType> predicate, ChunkPos arg, OccupationStatus arg2) {
        return IntStream.range(0, 16).boxed().flatMap(integer -> this.getInChunkSection(predicate, ChunkSectionPos.from(arg, integer).asLong(), arg2));
    }

    private Stream<PointOfInterest> getInChunkSection(Predicate<PointOfInterestType> predicate, long l, OccupationStatus arg) {
        return this.get(l).map(arg2 -> arg2.get(predicate, arg)).orElseGet(Stream::empty);
    }

    public Stream<BlockPos> getPositions(Predicate<PointOfInterestType> predicate, Predicate<BlockPos> predicate2, BlockPos arg, int i, OccupationStatus arg2) {
        return this.getInCircle(predicate, arg, i, arg2).map(PointOfInterest::getPos).filter(predicate2);
    }

    public Optional<BlockPos> getPosition(Predicate<PointOfInterestType> predicate, Predicate<BlockPos> predicate2, BlockPos arg, int i, OccupationStatus arg2) {
        return this.getPositions(predicate, predicate2, arg, i, arg2).findFirst();
    }

    public Optional<BlockPos> getNearestPosition(Predicate<PointOfInterestType> predicate, BlockPos arg, int i, OccupationStatus arg22) {
        return this.getInCircle(predicate, arg, i, arg22).map(PointOfInterest::getPos).sorted(Comparator.comparingDouble(arg2 -> arg2.getSquaredDistance(arg))).findFirst();
    }

    public Optional<BlockPos> getPosition(Predicate<PointOfInterestType> predicate, Predicate<BlockPos> predicate2, BlockPos arg2, int i) {
        return this.getInCircle(predicate, arg2, i, OccupationStatus.HAS_SPACE).filter(arg -> predicate2.test(arg.getPos())).findFirst().map(arg -> {
            arg.reserveTicket();
            return arg.getPos();
        });
    }

    public Optional<BlockPos> getPosition(Predicate<PointOfInterestType> predicate, Predicate<BlockPos> predicate2, OccupationStatus arg2, BlockPos arg22, int i, Random random) {
        List list = this.getInCircle(predicate, arg22, i, arg2).collect(Collectors.toList());
        Collections.shuffle(list, random);
        return list.stream().filter(arg -> predicate2.test(arg.getPos())).findFirst().map(PointOfInterest::getPos);
    }

    public boolean releaseTicket(BlockPos arg) {
        return ((PointOfInterestSet)this.getOrCreate(ChunkSectionPos.from(arg).asLong())).releaseTicket(arg);
    }

    public boolean test(BlockPos arg, Predicate<PointOfInterestType> predicate) {
        return this.get(ChunkSectionPos.from(arg).asLong()).map(arg2 -> arg2.test(arg, predicate)).orElse(false);
    }

    public Optional<PointOfInterestType> getType(BlockPos arg) {
        PointOfInterestSet lv = (PointOfInterestSet)this.getOrCreate(ChunkSectionPos.from(arg).asLong());
        return lv.getType(arg);
    }

    public int getDistanceFromNearestOccupied(ChunkSectionPos arg) {
        this.pointOfInterestDistanceTracker.update();
        return this.pointOfInterestDistanceTracker.getLevel(arg.asLong());
    }

    private boolean isOccupied(long l) {
        Optional optional = this.getIfLoaded(l);
        if (optional == null) {
            return false;
        }
        return optional.map(arg -> arg.get(PointOfInterestType.ALWAYS_TRUE, OccupationStatus.IS_OCCUPIED).count() > 0L).orElse(false);
    }

    @Override
    public void tick(BooleanSupplier booleanSupplier) {
        super.tick(booleanSupplier);
        this.pointOfInterestDistanceTracker.update();
    }

    @Override
    protected void onUpdate(long l) {
        super.onUpdate(l);
        this.pointOfInterestDistanceTracker.update(l, this.pointOfInterestDistanceTracker.getInitialLevel(l), false);
    }

    @Override
    protected void onLoad(long l) {
        this.pointOfInterestDistanceTracker.update(l, this.pointOfInterestDistanceTracker.getInitialLevel(l), false);
    }

    public void initForPalette(ChunkPos arg, ChunkSection arg2) {
        ChunkSectionPos lv = ChunkSectionPos.from(arg, arg2.getYOffset() >> 4);
        Util.ifPresentOrElse(this.get(lv.asLong()), arg3 -> arg3.updatePointsOfInterest(biConsumer -> {
            if (PointOfInterestStorage.shouldScan(arg2)) {
                this.scanAndPopulate(arg2, lv, (BiConsumer<BlockPos, PointOfInterestType>)biConsumer);
            }
        }), () -> {
            if (PointOfInterestStorage.shouldScan(arg2)) {
                PointOfInterestSet lv = (PointOfInterestSet)this.getOrCreate(lv.asLong());
                this.scanAndPopulate(arg2, lv, (arg_0, arg_1) -> lv.add(arg_0, arg_1));
            }
        });
    }

    private static boolean shouldScan(ChunkSection arg) {
        return PointOfInterestType.getAllAssociatedStates().anyMatch(arg::method_19523);
    }

    private void scanAndPopulate(ChunkSection arg, ChunkSectionPos arg2, BiConsumer<BlockPos, PointOfInterestType> biConsumer) {
        arg2.streamBlocks().forEach(arg22 -> {
            BlockState lv = arg.getBlockState(ChunkSectionPos.getLocalCoord(arg22.getX()), ChunkSectionPos.getLocalCoord(arg22.getY()), ChunkSectionPos.getLocalCoord(arg22.getZ()));
            PointOfInterestType.from(lv).ifPresent(arg2 -> biConsumer.accept((BlockPos)arg22, (PointOfInterestType)arg2));
        });
    }

    public void preloadChunks(WorldView arg3, BlockPos arg22, int i) {
        ChunkSectionPos.stream(new ChunkPos(arg22), Math.floorDiv(i, 16)).map(arg -> Pair.of((Object)arg, this.get(arg.asLong()))).filter(pair -> ((Optional)pair.getSecond()).map(PointOfInterestSet::isValid).orElse(false) == false).map(pair -> ((ChunkSectionPos)pair.getFirst()).toChunkPos()).filter(arg -> this.preloadedChunks.add(arg.toLong())).forEach(arg2 -> arg3.getChunk(arg2.x, arg2.z, ChunkStatus.EMPTY));
    }

    final class PointOfInterestDistanceTracker
    extends SectionDistanceLevelPropagator {
        private final Long2ByteMap distances;

        protected PointOfInterestDistanceTracker() {
            super(7, 16, 256);
            this.distances = new Long2ByteOpenHashMap();
            this.distances.defaultReturnValue((byte)7);
        }

        @Override
        protected int getInitialLevel(long l) {
            return PointOfInterestStorage.this.isOccupied(l) ? 0 : 7;
        }

        @Override
        protected int getLevel(long l) {
            return this.distances.get(l);
        }

        @Override
        protected void setLevel(long l, int i) {
            if (i > 6) {
                this.distances.remove(l);
            } else {
                this.distances.put(l, (byte)i);
            }
        }

        public void update() {
            super.applyPendingUpdates(Integer.MAX_VALUE);
        }
    }

    public static enum OccupationStatus {
        HAS_SPACE(PointOfInterest::hasSpace),
        IS_OCCUPIED(PointOfInterest::isOccupied),
        ANY(arg -> true);

        private final Predicate<? super PointOfInterest> predicate;

        private OccupationStatus(Predicate<? super PointOfInterest> predicate) {
            this.predicate = predicate;
        }

        public Predicate<? super PointOfInterest> getPredicate() {
            return this.predicate;
        }
    }
}

