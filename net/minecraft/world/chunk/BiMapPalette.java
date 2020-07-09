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
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PaletteResizeListener;

public class BiMapPalette<T>
implements Palette<T> {
    private final IdList<T> idList;
    private final Int2ObjectBiMap<T> map;
    private final PaletteResizeListener<T> resizeHandler;
    private final Function<CompoundTag, T> elementDeserializer;
    private final Function<T, CompoundTag> elementSerializer;
    private final int indexBits;

    public BiMapPalette(IdList<T> arg, int i, PaletteResizeListener<T> arg2, Function<CompoundTag, T> function, Function<T, CompoundTag> function2) {
        this.idList = arg;
        this.indexBits = i;
        this.resizeHandler = arg2;
        this.elementDeserializer = function;
        this.elementSerializer = function2;
        this.map = new Int2ObjectBiMap(1 << i);
    }

    @Override
    public int getIndex(T object) {
        int i = this.map.getRawId(object);
        if (i == -1 && (i = this.map.add(object)) >= 1 << this.indexBits) {
            i = this.resizeHandler.onResize(this.indexBits + 1, object);
        }
        return i;
    }

    @Override
    public boolean accepts(Predicate<T> predicate) {
        for (int i = 0; i < this.getIndexBits(); ++i) {
            if (!predicate.test(this.map.get(i))) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public T getByIndex(int i) {
        return this.map.get(i);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void fromPacket(PacketByteBuf arg) {
        this.map.clear();
        int i = arg.readVarInt();
        for (int j = 0; j < i; ++j) {
            this.map.add(this.idList.get(arg.readVarInt()));
        }
    }

    @Override
    public void toPacket(PacketByteBuf arg) {
        int i = this.getIndexBits();
        arg.writeVarInt(i);
        for (int j = 0; j < i; ++j) {
            arg.writeVarInt(this.idList.getRawId(this.map.get(j)));
        }
    }

    @Override
    public int getPacketSize() {
        int i = PacketByteBuf.getVarIntSizeBytes(this.getIndexBits());
        for (int j = 0; j < this.getIndexBits(); ++j) {
            i += PacketByteBuf.getVarIntSizeBytes(this.idList.getRawId(this.map.get(j)));
        }
        return i;
    }

    public int getIndexBits() {
        return this.map.size();
    }

    @Override
    public void fromTag(ListTag arg) {
        this.map.clear();
        for (int i = 0; i < arg.size(); ++i) {
            this.map.add(this.elementDeserializer.apply(arg.getCompound(i)));
        }
    }

    public void toTag(ListTag arg) {
        for (int i = 0; i < this.getIndexBits(); ++i) {
            arg.add(this.elementSerializer.apply(this.map.get(i)));
        }
    }
}

