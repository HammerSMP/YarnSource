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
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.FeatureUpdater;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
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

    public CompoundTag updateChunkTag(RegistryKey<World> arg, Supplier<PersistentStateManager> persistentStateManagerFactory, CompoundTag tag) {
        int i = VersionedChunkStorage.getDataVersion(tag);
        int j = 1493;
        if (i < 1493 && (tag = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, tag, i, 1493)).getCompound("Level").getBoolean("hasLegacyStructureData")) {
            if (this.featureUpdater == null) {
                this.featureUpdater = FeatureUpdater.create(arg, persistentStateManagerFactory.get());
            }
            tag = this.featureUpdater.getUpdatedReferences(tag);
        }
        tag = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, tag, Math.max(1493, i));
        if (i < SharedConstants.getGameVersion().getWorldVersion()) {
            tag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        }
        return tag;
    }

    public static int getDataVersion(CompoundTag tag) {
        return tag.contains("DataVersion", 99) ? tag.getInt("DataVersion") : -1;
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

