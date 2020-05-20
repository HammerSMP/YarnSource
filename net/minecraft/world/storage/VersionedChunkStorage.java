/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  javax.annotation.Nullable
 */
package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.FeatureUpdater;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.StorageIoWorker;

public class VersionedChunkStorage
implements AutoCloseable {
    private final StorageIoWorker worker;
    protected final DataFixer dataFixer;
    @Nullable
    private FeatureUpdater featureUpdater;

    public VersionedChunkStorage(File file, DataFixer dataFixer, boolean bl) {
        this.dataFixer = dataFixer;
        this.worker = new StorageIoWorker(file, bl, "chunk");
    }

    public CompoundTag updateChunkTag(DimensionType arg, Supplier<PersistentStateManager> supplier, CompoundTag arg2) {
        int i = VersionedChunkStorage.getDataVersion(arg2);
        int j = 1493;
        if (i < 1493 && (arg2 = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, arg2, i, 1493)).getCompound("Level").getBoolean("hasLegacyStructureData")) {
            if (this.featureUpdater == null) {
                this.featureUpdater = FeatureUpdater.create(arg, supplier.get());
            }
            arg2 = this.featureUpdater.getUpdatedReferences(arg2);
        }
        arg2 = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, arg2, Math.max(1493, i));
        if (i < SharedConstants.getGameVersion().getWorldVersion()) {
            arg2.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        }
        return arg2;
    }

    public static int getDataVersion(CompoundTag arg) {
        return arg.contains("DataVersion", 99) ? arg.getInt("DataVersion") : -1;
    }

    @Nullable
    public CompoundTag getNbt(ChunkPos arg) throws IOException {
        return this.worker.getNbt(arg);
    }

    public void setTagAt(ChunkPos arg, CompoundTag arg2) {
        this.worker.setResult(arg, arg2);
        if (this.featureUpdater != null) {
            this.featureUpdater.markResolved(arg.toLong());
        }
    }

    public void completeAll() {
        this.worker.completeAll().join();
    }

    @Override
    public void close() throws IOException {
        this.worker.close();
    }
}

