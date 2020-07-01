/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  javax.annotation.Nullable
 */
package net.minecraft.world.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.ThrowableDeliverer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.RegionFile;

public final class RegionBasedStorage
implements AutoCloseable {
    private final Long2ObjectLinkedOpenHashMap<RegionFile> cachedRegionFiles = new Long2ObjectLinkedOpenHashMap();
    private final File directory;
    private final boolean dsync;

    RegionBasedStorage(File file, boolean bl) {
        this.directory = file;
        this.dsync = bl;
    }

    private RegionFile getRegionFile(ChunkPos arg) throws IOException {
        long l = ChunkPos.toLong(arg.getRegionX(), arg.getRegionZ());
        RegionFile lv = (RegionFile)this.cachedRegionFiles.getAndMoveToFirst(l);
        if (lv != null) {
            return lv;
        }
        if (this.cachedRegionFiles.size() >= 256) {
            ((RegionFile)this.cachedRegionFiles.removeLast()).close();
        }
        if (!this.directory.exists()) {
            this.directory.mkdirs();
        }
        File file = new File(this.directory, "r." + arg.getRegionX() + "." + arg.getRegionZ() + ".mca");
        RegionFile lv2 = new RegionFile(file, this.directory, this.dsync);
        this.cachedRegionFiles.putAndMoveToFirst(l, (Object)lv2);
        return lv2;
    }

    @Nullable
    public CompoundTag getTagAt(ChunkPos arg) throws IOException {
        RegionFile lv = this.getRegionFile(arg);
        try (DataInputStream dataInputStream = lv.getChunkInputStream(arg);){
            if (dataInputStream == null) {
                CompoundTag compoundTag = null;
                return compoundTag;
            }
            CompoundTag compoundTag = NbtIo.read(dataInputStream);
            return compoundTag;
        }
    }

    protected void write(ChunkPos arg, CompoundTag arg2) throws IOException {
        RegionFile lv = this.getRegionFile(arg);
        try (DataOutputStream dataOutputStream = lv.getChunkOutputStream(arg);){
            NbtIo.write(arg2, (DataOutput)dataOutputStream);
        }
    }

    @Override
    public void close() throws IOException {
        ThrowableDeliverer<IOException> lv = new ThrowableDeliverer<IOException>();
        for (RegionFile lv2 : this.cachedRegionFiles.values()) {
            try {
                lv2.close();
            }
            catch (IOException iOException) {
                lv.add(iOException);
            }
        }
        lv.deliver();
    }

    public void method_26982() throws IOException {
        for (RegionFile lv : this.cachedRegionFiles.values()) {
            lv.method_26981();
        }
    }
}

