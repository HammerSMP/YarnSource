/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ArrayPalette;
import net.minecraft.world.chunk.BiMapPalette;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PaletteResizeListener;

public class PalettedContainer<T>
implements PaletteResizeListener<T> {
    private final Palette<T> fallbackPalette;
    private final PaletteResizeListener<T> noOpPaletteResizeHandler = (i, object) -> 0;
    private final IdList<T> idList;
    private final Function<CompoundTag, T> elementDeserializer;
    private final Function<T, CompoundTag> elementSerializer;
    private final T field_12935;
    protected PackedIntegerArray data;
    private Palette<T> palette;
    private int paletteSize;
    private final ReentrantLock writeLock = new ReentrantLock();

    public void lock() {
        if (this.writeLock.isLocked() && !this.writeLock.isHeldByCurrentThread()) {
            String string = Thread.getAllStackTraces().keySet().stream().filter(Objects::nonNull).map(thread -> thread.getName() + ": \n\tat " + Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "))).collect(Collectors.joining("\n"));
            CrashReport lv = new CrashReport("Writing into PalettedContainer from multiple threads", new IllegalStateException());
            CrashReportSection lv2 = lv.addElement("Thread dumps");
            lv2.add("Thread dumps", string);
            throw new CrashException(lv);
        }
        this.writeLock.lock();
    }

    public void unlock() {
        this.writeLock.unlock();
    }

    public PalettedContainer(Palette<T> arg, IdList<T> arg2, Function<CompoundTag, T> function, Function<T, CompoundTag> function2, T object2) {
        this.fallbackPalette = arg;
        this.idList = arg2;
        this.elementDeserializer = function;
        this.elementSerializer = function2;
        this.field_12935 = object2;
        this.setPaletteSize(4);
    }

    private static int toIndex(int i, int j, int k) {
        return j << 8 | k << 4 | i;
    }

    private void setPaletteSize(int i) {
        if (i == this.paletteSize) {
            return;
        }
        this.paletteSize = i;
        if (this.paletteSize <= 4) {
            this.paletteSize = 4;
            this.palette = new ArrayPalette<T>(this.idList, this.paletteSize, this, this.elementDeserializer);
        } else if (this.paletteSize < 9) {
            this.palette = new BiMapPalette<T>(this.idList, this.paletteSize, this, this.elementDeserializer, this.elementSerializer);
        } else {
            this.palette = this.fallbackPalette;
            this.paletteSize = MathHelper.log2DeBruijn(this.idList.size());
        }
        this.palette.getIndex(this.field_12935);
        this.data = new PackedIntegerArray(this.paletteSize, 4096);
    }

    @Override
    public int onResize(int i, T object) {
        this.lock();
        PackedIntegerArray lv = this.data;
        Palette<T> lv2 = this.palette;
        this.setPaletteSize(i);
        for (int j = 0; j < lv.getSize(); ++j) {
            T object2 = lv2.getByIndex(lv.get(j));
            if (object2 == null) continue;
            this.set(j, object2);
        }
        int k = this.palette.getIndex(object);
        this.unlock();
        return k;
    }

    public T setSync(int i, int j, int k, T object) {
        this.lock();
        T object2 = this.setAndGetOldValue(PalettedContainer.toIndex(i, j, k), object);
        this.unlock();
        return object2;
    }

    public T set(int i, int j, int k, T object) {
        return this.setAndGetOldValue(PalettedContainer.toIndex(i, j, k), object);
    }

    protected T setAndGetOldValue(int i, T object) {
        int j = this.palette.getIndex(object);
        int k = this.data.setAndGetOldValue(i, j);
        T object2 = this.palette.getByIndex(k);
        return object2 == null ? this.field_12935 : object2;
    }

    protected void set(int i, T object) {
        int j = this.palette.getIndex(object);
        this.data.set(i, j);
    }

    public T get(int i, int j, int k) {
        return this.get(PalettedContainer.toIndex(i, j, k));
    }

    protected T get(int i) {
        T object = this.palette.getByIndex(this.data.get(i));
        return object == null ? this.field_12935 : object;
    }

    @Environment(value=EnvType.CLIENT)
    public void fromPacket(PacketByteBuf arg) {
        this.lock();
        byte i = arg.readByte();
        if (this.paletteSize != i) {
            this.setPaletteSize(i);
        }
        this.palette.fromPacket(arg);
        arg.readLongArray(this.data.getStorage());
        this.unlock();
    }

    public void toPacket(PacketByteBuf arg) {
        this.lock();
        arg.writeByte(this.paletteSize);
        this.palette.toPacket(arg);
        arg.writeLongArray(this.data.getStorage());
        this.unlock();
    }

    public void read(ListTag arg, long[] ls) {
        this.lock();
        int i = Math.max(4, MathHelper.log2DeBruijn(arg.size()));
        if (i != this.paletteSize) {
            this.setPaletteSize(i);
        }
        this.palette.fromTag(arg);
        int j = ls.length * 64 / 4096;
        if (this.palette == this.fallbackPalette) {
            BiMapPalette<T> lv = new BiMapPalette<T>(this.idList, i, this.noOpPaletteResizeHandler, this.elementDeserializer, this.elementSerializer);
            lv.fromTag(arg);
            PackedIntegerArray lv2 = new PackedIntegerArray(i, 4096, ls);
            for (int k = 0; k < 4096; ++k) {
                this.data.set(k, this.fallbackPalette.getIndex(lv.getByIndex(lv2.get(k))));
            }
        } else if (j == this.paletteSize) {
            System.arraycopy(ls, 0, this.data.getStorage(), 0, ls.length);
        } else {
            PackedIntegerArray lv3 = new PackedIntegerArray(j, 4096, ls);
            for (int l = 0; l < 4096; ++l) {
                this.data.set(l, lv3.get(l));
            }
        }
        this.unlock();
    }

    public void write(CompoundTag arg, String string, String string2) {
        this.lock();
        BiMapPalette<T> lv = new BiMapPalette<T>(this.idList, this.paletteSize, this.noOpPaletteResizeHandler, this.elementDeserializer, this.elementSerializer);
        T object = this.field_12935;
        int i = lv.getIndex(this.field_12935);
        int[] is = new int[4096];
        for (int j = 0; j < 4096; ++j) {
            T object2 = this.get(j);
            if (object2 != object) {
                object = object2;
                i = lv.getIndex(object2);
            }
            is[j] = i;
        }
        ListTag lv2 = new ListTag();
        lv.toTag(lv2);
        arg.put(string, lv2);
        int k = Math.max(4, MathHelper.log2DeBruijn(lv2.size()));
        PackedIntegerArray lv3 = new PackedIntegerArray(k, 4096);
        for (int l = 0; l < is.length; ++l) {
            lv3.set(l, is[l]);
        }
        arg.putLongArray(string2, lv3.getStorage());
        this.unlock();
    }

    public int getPacketSize() {
        return 1 + this.palette.getPacketSize() + PacketByteBuf.getVarIntSizeBytes(this.data.getSize()) + this.data.getStorage().length * 8;
    }

    public boolean method_19526(Predicate<T> predicate) {
        return this.palette.accepts(predicate);
    }

    public void count(CountConsumer<T> arg) {
        Int2IntOpenHashMap int2IntMap = new Int2IntOpenHashMap();
        this.data.forEach(arg_0 -> PalettedContainer.method_21734((Int2IntMap)int2IntMap, arg_0));
        int2IntMap.int2IntEntrySet().forEach(entry -> arg.accept(this.palette.getByIndex(entry.getIntKey()), entry.getIntValue()));
    }

    private static /* synthetic */ void method_21734(Int2IntMap int2IntMap, int i) {
        int2IntMap.put(i, int2IntMap.get(i) + 1);
    }

    @FunctionalInterface
    public static interface CountConsumer<T> {
        public void accept(T var1, int var2);
    }
}

