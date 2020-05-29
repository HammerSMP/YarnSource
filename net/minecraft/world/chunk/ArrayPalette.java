/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.chunk;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IdList;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PaletteResizeListener;

public class ArrayPalette<T>
implements Palette<T> {
    private final IdList<T> idList;
    private final T[] array;
    private final PaletteResizeListener<T> resizeListener;
    private final Function<CompoundTag, T> valueDeserializer;
    private final int indexBits;
    private int size;

    public ArrayPalette(IdList<T> arg, int i, PaletteResizeListener<T> arg2, Function<CompoundTag, T> function) {
        this.idList = arg;
        this.array = new Object[1 << i];
        this.indexBits = i;
        this.resizeListener = arg2;
        this.valueDeserializer = function;
    }

    @Override
    public int getIndex(T object) {
        int j;
        for (int i = 0; i < this.size; ++i) {
            if (this.array[i] != object) continue;
            return i;
        }
        if ((j = this.size++) < this.array.length) {
            this.array[j] = object;
            return j;
        }
        return this.resizeListener.onResize(this.indexBits + 1, object);
    }

    @Override
    public boolean accepts(Predicate<T> predicate) {
        for (int i = 0; i < this.size; ++i) {
            if (!predicate.test(this.array[i])) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public T getByIndex(int i) {
        if (i >= 0 && i < this.size) {
            return this.array[i];
        }
        return null;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void fromPacket(PacketByteBuf arg) {
        this.size = arg.readVarInt();
        for (int i = 0; i < this.size; ++i) {
            this.array[i] = this.idList.get(arg.readVarInt());
        }
    }

    @Override
    public void toPacket(PacketByteBuf arg) {
        arg.writeVarInt(this.size);
        for (int i = 0; i < this.size; ++i) {
            arg.writeVarInt(this.idList.getId(this.array[i]));
        }
    }

    @Override
    public int getPacketSize() {
        int i = PacketByteBuf.getVarIntSizeBytes(this.getSize());
        for (int j = 0; j < this.getSize(); ++j) {
            i += PacketByteBuf.getVarIntSizeBytes(this.idList.getId(this.array[j]));
        }
        return i;
    }

    public int getSize() {
        return this.size;
    }

    @Override
    public void fromTag(ListTag arg) {
        for (int i = 0; i < arg.size(); ++i) {
            this.array[i] = this.valueDeserializer.apply(arg.getCompound(i));
        }
        this.size = arg.size();
    }
}

