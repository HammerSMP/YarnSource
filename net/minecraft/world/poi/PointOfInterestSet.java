/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectMap
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.util.Supplier
 */
package net.minecraft.world.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class PointOfInterestSet {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Short2ObjectMap<PointOfInterest> pointsOfInterestByPos = new Short2ObjectOpenHashMap();
    private final Map<PointOfInterestType, Set<PointOfInterest>> pointsOfInterestByType = Maps.newHashMap();
    private final Runnable updateListener;
    private boolean valid;

    public static Codec<PointOfInterestSet> method_28364(Runnable runnable) {
        return RecordCodecBuilder.create(instance -> instance.group((App)RecordCodecBuilder.point((Object)runnable), (App)Codec.BOOL.optionalFieldOf("Valid", (Object)false).forGetter(arg -> arg.valid), (App)PointOfInterest.method_28359(runnable).listOf().fieldOf("Records").forGetter(arg -> ImmutableList.copyOf((Collection)arg.pointsOfInterestByPos.values()))).apply((Applicative)instance, PointOfInterestSet::new)).orElseGet(Util.method_29188("Failed to read POI section: ", ((Logger)LOGGER)::error), () -> new PointOfInterestSet(runnable, false, (List<PointOfInterest>)ImmutableList.of()));
    }

    public PointOfInterestSet(Runnable updateListener) {
        this(updateListener, true, (List<PointOfInterest>)ImmutableList.of());
    }

    private PointOfInterestSet(Runnable runnable, boolean bl, List<PointOfInterest> list) {
        this.updateListener = runnable;
        this.valid = bl;
        list.forEach(this::add);
    }

    public Stream<PointOfInterest> get(Predicate<PointOfInterestType> predicate, PointOfInterestStorage.OccupationStatus arg) {
        return this.pointsOfInterestByType.entrySet().stream().filter(entry -> predicate.test((PointOfInterestType)entry.getKey())).flatMap(entry -> ((Set)entry.getValue()).stream()).filter(arg.getPredicate());
    }

    public void add(BlockPos pos, PointOfInterestType type) {
        if (this.add(new PointOfInterest(pos, type, this.updateListener))) {
            LOGGER.debug("Added POI of type {} @ {}", new Supplier[]{() -> type, () -> pos});
            this.updateListener.run();
        }
    }

    private boolean add(PointOfInterest poi) {
        BlockPos lv = poi.getPos();
        PointOfInterestType lv2 = poi.getType();
        short s = ChunkSectionPos.getPackedLocalPos(lv);
        PointOfInterest lv3 = (PointOfInterest)this.pointsOfInterestByPos.get(s);
        if (lv3 != null) {
            if (lv2.equals(lv3.getType())) {
                return false;
            }
            throw Util.throwOrPause(new IllegalStateException("POI data mismatch: already registered at " + lv));
        }
        this.pointsOfInterestByPos.put(s, (Object)poi);
        this.pointsOfInterestByType.computeIfAbsent(lv2, arg -> Sets.newHashSet()).add(poi);
        return true;
    }

    public void remove(BlockPos pos) {
        PointOfInterest lv = (PointOfInterest)this.pointsOfInterestByPos.remove(ChunkSectionPos.getPackedLocalPos(pos));
        if (lv == null) {
            LOGGER.error("POI data mismatch: never registered at " + pos);
            return;
        }
        this.pointsOfInterestByType.get(lv.getType()).remove(lv);
        Supplier[] arrsupplier = new Supplier[2];
        arrsupplier[0] = lv::getType;
        arrsupplier[1] = lv::getPos;
        LOGGER.debug("Removed POI of type {} @ {}", arrsupplier);
        this.updateListener.run();
    }

    public boolean releaseTicket(BlockPos pos) {
        PointOfInterest lv = (PointOfInterest)this.pointsOfInterestByPos.get(ChunkSectionPos.getPackedLocalPos(pos));
        if (lv == null) {
            throw Util.throwOrPause(new IllegalStateException("POI never registered at " + pos));
        }
        boolean bl = lv.releaseTicket();
        this.updateListener.run();
        return bl;
    }

    public boolean test(BlockPos pos, Predicate<PointOfInterestType> predicate) {
        short s = ChunkSectionPos.getPackedLocalPos(pos);
        PointOfInterest lv = (PointOfInterest)this.pointsOfInterestByPos.get(s);
        return lv != null && predicate.test(lv.getType());
    }

    public Optional<PointOfInterestType> getType(BlockPos pos) {
        short s = ChunkSectionPos.getPackedLocalPos(pos);
        PointOfInterest lv = (PointOfInterest)this.pointsOfInterestByPos.get(s);
        return lv != null ? Optional.of(lv.getType()) : Optional.empty();
    }

    public void updatePointsOfInterest(Consumer<BiConsumer<BlockPos, PointOfInterestType>> consumer) {
        if (!this.valid) {
            Short2ObjectOpenHashMap short2ObjectMap = new Short2ObjectOpenHashMap(this.pointsOfInterestByPos);
            this.clear();
            consumer.accept((arg_0, arg_1) -> this.method_20352((Short2ObjectMap)short2ObjectMap, arg_0, arg_1));
            this.valid = true;
            this.updateListener.run();
        }
    }

    private void clear() {
        this.pointsOfInterestByPos.clear();
        this.pointsOfInterestByType.clear();
    }

    boolean isValid() {
        return this.valid;
    }

    private /* synthetic */ void method_20352(Short2ObjectMap short2ObjectMap, BlockPos arg, PointOfInterestType arg2) {
        short s = ChunkSectionPos.getPackedLocalPos(arg);
        PointOfInterest lv = (PointOfInterest)short2ObjectMap.computeIfAbsent(s, i -> new PointOfInterest(arg, arg2, this.updateListener));
        this.add(lv);
    }
}

