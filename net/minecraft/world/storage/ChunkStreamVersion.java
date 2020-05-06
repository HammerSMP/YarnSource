/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  javax.annotation.Nullable
 */
package net.minecraft.world.storage;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;

public class ChunkStreamVersion {
    private static final Int2ObjectMap<ChunkStreamVersion> VERSIONS = new Int2ObjectOpenHashMap();
    public static final ChunkStreamVersion GZIP = ChunkStreamVersion.add(new ChunkStreamVersion(1, GZIPInputStream::new, GZIPOutputStream::new));
    public static final ChunkStreamVersion DEFLATE = ChunkStreamVersion.add(new ChunkStreamVersion(2, InflaterInputStream::new, DeflaterOutputStream::new));
    public static final ChunkStreamVersion UNCOMPRESSED = ChunkStreamVersion.add(new ChunkStreamVersion(3, inputStream -> inputStream, outputStream -> outputStream));
    private final int id;
    private final Wrapper<InputStream> inputStreamWrapper;
    private final Wrapper<OutputStream> outputStreamWrapper;

    private ChunkStreamVersion(int i, Wrapper<InputStream> arg, Wrapper<OutputStream> arg2) {
        this.id = i;
        this.inputStreamWrapper = arg;
        this.outputStreamWrapper = arg2;
    }

    private static ChunkStreamVersion add(ChunkStreamVersion arg) {
        VERSIONS.put(arg.id, (Object)arg);
        return arg;
    }

    @Nullable
    public static ChunkStreamVersion get(int i) {
        return (ChunkStreamVersion)VERSIONS.get(i);
    }

    public static boolean exists(int i) {
        return VERSIONS.containsKey(i);
    }

    public int getId() {
        return this.id;
    }

    public OutputStream wrap(OutputStream outputStream) throws IOException {
        return this.outputStreamWrapper.wrap(outputStream);
    }

    public InputStream wrap(InputStream inputStream) throws IOException {
        return this.inputStreamWrapper.wrap(inputStream);
    }

    @FunctionalInterface
    static interface Wrapper<O> {
        public O wrap(O var1) throws IOException;
    }
}

