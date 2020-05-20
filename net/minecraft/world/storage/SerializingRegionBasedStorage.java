/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.OptionalDynamic
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.StorageIoWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SerializingRegionBasedStorage<R>
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final StorageIoWorker worker;
    private final Long2ObjectMap<Optional<R>> loadedElements = new Long2ObjectOpenHashMap();
    private final LongLinkedOpenHashSet unsavedElements = new LongLinkedOpenHashSet();
    private final Function<Runnable, Codec<R>> field_24750;
    private final Function<Runnable, R> factory;
    private final DataFixer dataFixer;
    private final DataFixTypes dataFixType;

    public SerializingRegionBasedStorage(File file, Function<Runnable, Codec<R>> function, Function<Runnable, R> function2, DataFixer dataFixer, DataFixTypes arg, boolean bl) {
        this.field_24750 = function;
        this.factory = function2;
        this.dataFixer = dataFixer;
        this.dataFixType = arg;
        this.worker = new StorageIoWorker(file, bl, file.getName());
    }

    protected void tick(BooleanSupplier booleanSupplier) {
        while (!this.unsavedElements.isEmpty() && booleanSupplier.getAsBoolean()) {
            ChunkPos lv = ChunkSectionPos.from(this.unsavedElements.firstLong()).toChunkPos();
            this.save(lv);
        }
    }

    @Nullable
    protected Optional<R> getIfLoaded(long l) {
        return (Optional)this.loadedElements.get(l);
    }

    protected Optional<R> get(long l) {
        ChunkSectionPos lv = ChunkSectionPos.from(l);
        if (this.isPosInvalid(lv)) {
            return Optional.empty();
        }
        Optional<R> optional = this.getIfLoaded(l);
        if (optional != null) {
            return optional;
        }
        this.loadDataAt(lv.toChunkPos());
        optional = this.getIfLoaded(l);
        if (optional == null) {
            throw Util.throwOrPause(new IllegalStateException());
        }
        return optional;
    }

    protected boolean isPosInvalid(ChunkSectionPos arg) {
        return World.isHeightInvalid(ChunkSectionPos.getWorldCoord(arg.getSectionY()));
    }

    protected R getOrCreate(long l) {
        Optional<R> optional = this.get(l);
        if (optional.isPresent()) {
            return optional.get();
        }
        R object = this.factory.apply(() -> this.onUpdate(l));
        this.loadedElements.put(l, Optional.of(object));
        return object;
    }

    private void loadDataAt(ChunkPos arg) {
        this.update(arg, NbtOps.INSTANCE, this.loadNbt(arg));
    }

    @Nullable
    private CompoundTag loadNbt(ChunkPos arg) {
        try {
            return this.worker.getNbt(arg);
        }
        catch (IOException iOException) {
            LOGGER.error("Error reading chunk {} data from disk", (Object)arg, (Object)iOException);
            return null;
        }
    }

    private <T> void update(ChunkPos arg, DynamicOps<T> dynamicOps, @Nullable T object2) {
        if (object2 == null) {
            for (int i = 0; i < 16; ++i) {
                this.loadedElements.put(ChunkSectionPos.from(arg, i).asLong(), Optional.empty());
            }
        } else {
            int k;
            Dynamic dynamic2 = new Dynamic(dynamicOps, object2);
            int j = SerializingRegionBasedStorage.getDataVersion(dynamic2);
            boolean bl = j != (k = SharedConstants.getGameVersion().getWorldVersion());
            Dynamic dynamic22 = this.dataFixer.update(this.dataFixType.getTypeReference(), dynamic2, j, k);
            OptionalDynamic optionalDynamic = dynamic22.get("Sections");
            for (int l = 0; l < 16; ++l) {
                long m = ChunkSectionPos.from(arg, l).asLong();
                Optional optional = optionalDynamic.get(Integer.toString(l)).result().flatMap(dynamic -> this.field_24750.apply(() -> this.onUpdate(m)).parse(dynamic).resultOrPartial(((Logger)LOGGER)::error));
                this.loadedElements.put(m, optional);
                optional.ifPresent(object -> {
                    this.onLoad(m);
                    if (bl) {
                        this.onUpdate(m);
                    }
                });
            }
        }
    }

    private void save(ChunkPos arg) {
        Dynamic<Tag> dynamic = this.method_20367(arg, NbtOps.INSTANCE);
        Tag lv = (Tag)dynamic.getValue();
        if (lv instanceof CompoundTag) {
            this.worker.setResult(arg, (CompoundTag)lv);
        } else {
            LOGGER.error("Expected compound tag, got {}", (Object)lv);
        }
    }

    private <T> Dynamic<T> method_20367(ChunkPos arg, DynamicOps<T> dynamicOps) {
        HashMap map = Maps.newHashMap();
        for (int i = 0; i < 16; ++i) {
            long l = ChunkSectionPos.from(arg, i).asLong();
            this.unsavedElements.remove(l);
            Optional optional = (Optional)this.loadedElements.get(l);
            if (optional == null || !optional.isPresent()) continue;
            DataResult dataResult = this.field_24750.apply(() -> this.onUpdate(l)).encodeStart(dynamicOps, optional.get());
            String string = Integer.toString(i);
            dataResult.resultOrPartial(((Logger)LOGGER)::error).ifPresent(object -> map.put(dynamicOps.createString(string), object));
        }
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("Sections"), (Object)dynamicOps.createMap((Map)map), (Object)dynamicOps.createString("DataVersion"), (Object)dynamicOps.createInt(SharedConstants.getGameVersion().getWorldVersion()))));
    }

    protected void onLoad(long l) {
    }

    protected void onUpdate(long l) {
        Optional optional = (Optional)this.loadedElements.get(l);
        if (optional == null || !optional.isPresent()) {
            LOGGER.warn("No data for position: {}", (Object)ChunkSectionPos.from(l));
            return;
        }
        this.unsavedElements.add(l);
    }

    private static int getDataVersion(Dynamic<?> dynamic) {
        return dynamic.get("DataVersion").asInt(1945);
    }

    public void method_20436(ChunkPos arg) {
        if (!this.unsavedElements.isEmpty()) {
            for (int i = 0; i < 16; ++i) {
                long l = ChunkSectionPos.from(arg, i).asLong();
                if (!this.unsavedElements.contains(l)) continue;
                this.save(arg);
                return;
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.worker.close();
    }
}

