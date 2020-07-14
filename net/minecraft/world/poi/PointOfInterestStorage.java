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

    public void add(BlockPos pos, PointOfInterestType type) {
        ((PointOfInterestSet)this.getOrCreate(ChunkSectionPos.from(pos).asLong())).add(pos, type);
    }

    public void remove(BlockPos pos) {
        ((PointOfInterestSet)this.getOrCreate(ChunkSectionPos.from(pos).asLong())).remove(pos);
    }

    public long count(Predicate<PointOfInterestType> typePredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
        return this.getInCircle(typePredicate, pos, radius, occupationStatus).count();
    }

    public boolean method_26339(PointOfInterestType arg, BlockPos arg2) {
        Optional<PointOfInterestType> optional = ((PointOfInterestSet)this.getOrCreate(ChunkSectionPos.from(arg2).asLong())).getType(arg2);
        return optional.isPresent() && optional.get().equals(arg);
    }

    public Stream<PointOfInterest> getInSquare(Predicate<PointOfInterestType> typePredicate, BlockPos pos, int radius, OccupationStatus arg22) {
        int j = Math.floorDiv(radius, 16) + 1;
        return ChunkPos.stream(new ChunkPos(pos), j).flatMap(arg2 -> this.getInChunk(typePredicate, (ChunkPos)arg2, arg22)).filter(arg2 -> {
            BlockPos lv = arg2.getPos();
            return Math.abs(lv.getX() - pos.getX()) <= radius && Math.abs(lv.getZ() - pos.getZ()) <= radius;
        });
    }

    public Stream<PointOfInterest> getInCircle(Predicate<PointOfInterestType> typePredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
        int j = radius * radius;
        return this.getInSquare(typePredicate, pos, radius, occupationStatus).filter(arg2 -> arg2.getPos().getSquaredDistance(pos) <= (double)j);
    }

    public Stream<PointOfInterest> getInChunk(Predicate<PointOfInterestType> predicate, ChunkPos arg, OccupationStatus occupationStatus) {
        return IntStream.range(0, 16).boxed().map(integer -> this.get(ChunkSectionPos.from(arg, integer).asLong())).filter(Optional::isPresent).flatMap(optional -> ((PointOfInterestSet)optional.get()).get(predicate, occupationStatus));
    }

    public Stream<BlockPos> getPositions(Predicate<PointOfInterestType> typePredicate, Predicate<BlockPos> posPredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
        return this.getInCircle(typePredicate, pos, radius, occupationStatus).map(PointOfInterest::getPos).filter(posPredicate);
    }

    public Optional<BlockPos> getPosition(Predicate<PointOfInterestType> typePredicate, Predicate<BlockPos> posPredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
        return this.getPositions(typePredicate, posPredicate, pos, radius, occupationStatus).findFirst();
    }

    public Optional<BlockPos> getNearestPosition(Predicate<PointOfInterestType> typePredicate, BlockPos arg, int i, OccupationStatus arg22) {
        return this.getInCircle(typePredicate, arg, i, arg22).map(PointOfInterest::getPos).min(Comparator.comparingDouble(arg2 -> arg2.getSquaredDistance(arg)));
    }

    public Optional<BlockPos> getPosition(Predicate<PointOfInterestType> typePredicate, Predicate<BlockPos> positionPredicate, BlockPos pos, int radius) {
        return this.getInCircle(typePredicate, pos, radius, OccupationStatus.HAS_SPACE).filter(arg -> positionPredicate.test(arg.getPos())).findFirst().map(arg -> {
            arg.reserveTicket();
            return arg.getPos();
        });
    }

    public Optional<BlockPos> getPosition(Predicate<PointOfInterestType> typePredicate, Predicate<BlockPos> positionPredicate, OccupationStatus occupationStatus, BlockPos pos, int radius, Random random) {
        List list = this.getInCircle(typePredicate, pos, radius, occupationStatus).collect(Collectors.toList());
        Collections.shuffle(list, random);
        return list.stream().filter(arg -> positionPredicate.test(arg.getPos())).findFirst().map(PointOfInterest::getPos);
    }

    public boolean releaseTicket(BlockPos pos) {
        return ((PointOfInterestSet)this.getOrCreate(ChunkSectionPos.from(pos).asLong())).releaseTicket(pos);
    }

    public boolean test(BlockPos pos, Predicate<PointOfInterestType> predicate) {
        return this.get(ChunkSectionPos.from(pos).asLong()).map(arg2 -> arg2.test(pos, predicate)).orElse(false);
    }

    public Optional<PointOfInterestType> getType(BlockPos pos) {
        PointOfInterestSet lv = (PointOfInterestSet)this.getOrCreate(ChunkSectionPos.from(pos).asLong());
        return lv.getType(pos);
    }

    public int getDistanceFromNearestOccupied(ChunkSectionPos pos) {
        this.pointOfInterestDistanceTracker.update();
        return this.pointOfInterestDistanceTracker.getLevel(pos.asLong());
    }

    private boolean isOccupied(long l) {
        Optional optional = this.getIfLoaded(l);
        if (optional == null) {
            return false;
        }
        return optional.map(arg -> arg.get(PointOfInterestType.ALWAYS_TRUE, OccupationStatus.IS_OCCUPIED).count() > 0L).orElse(false);
    }

    @Override
    public void tick(BooleanSupplier shouldKeepTicking) {
        super.tick(shouldKeepTicking);
        this.pointOfInterestDistanceTracker.update();
    }

    @Override
    protected void onUpdate(long pos) {
        super.onUpdate(pos);
        this.pointOfInterestDistanceTracker.update(pos, this.pointOfInterestDistanceTracker.getInitialLevel(pos), false);
    }

    @Override
    protected void onLoad(long pos) {
        this.pointOfInterestDistanceTracker.update(pos, this.pointOfInterestDistanceTracker.getInitialLevel(pos), false);
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
        return arg.method_19523(PointOfInterestType.field_25162::contains);
    }

    private void scanAndPopulate(ChunkSection arg, ChunkSectionPos arg2, BiConsumer<BlockPos, PointOfInterestType> biConsumer) {
        arg2.streamBlocks().forEach(arg22 -> {
            BlockState lv = arg.getBlockState(ChunkSectionPos.getLocalCoord(arg22.getX()), ChunkSectionPos.getLocalCoord(arg22.getY()), ChunkSectionPos.getLocalCoord(arg22.getZ()));
            PointOfInterestType.from(lv).ifPresent(arg2 -> biConsumer.accept((BlockPos)arg22, (PointOfInterestType)arg2));
        });
    }

    public void preloadChunks(WorldView world, BlockPos pos, int radius) {
        ChunkSectionPos.stream(new ChunkPos(pos), Math.floorDiv(radius, 16)).map(arg -> Pair.of((Object)arg, this.get(arg.asLong()))).filter(pair -> ((Optional)pair.getSecond()).map(PointOfInterestSet::isValid).orElse(false) == false).map(pair -> ((ChunkSectionPos)pair.getFirst()).toChunkPos()).filter(arg -> this.preloadedChunks.add(arg.toLong())).forEach(arg2 -> world.getChunk(arg2.x, arg2.z, ChunkStatus.EMPTY));
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
        protected int getInitialLevel(long id) {
            return PointOfInterestStorage.this.isOccupied(id) ? 0 : 7;
        }

        @Override
        protected int getLevel(long id) {
            return this.distances.get(id);
        }

        @Override
        protected void setLevel(long id, int level) {
            if (level > 6) {
                this.distances.remove(id);
            } else {
                this.distances.put(id, (byte)level);
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

