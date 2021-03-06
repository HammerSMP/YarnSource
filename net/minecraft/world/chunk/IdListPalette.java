/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.chunk;

import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IdList;
import net.minecraft.world.chunk.Palette;

public class IdListPalette<T>
implements Palette<T> {
    private final IdList<T> idList;
    private final T fallback;

    public IdListPalette(IdList<T> idList, T defaultValue) {
        this.idList = idList;
        this.fallback = defaultValue;
    }

    @Override
    public int getIndex(T object) {
        int i = this.idList.getRawId(object);
        return i == -1 ? 0 : i;
    }

    @Override
    public boolean accepts(Predicate<T> predicate) {
        return true;
    }

    @Override
    public T getByIndex(int index) {
        T object = this.idList.get(index);
        return object == null ? this.fallback : object;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void fromPacket(PacketByteBuf buf) {
    }

    @Override
    public void toPacket(PacketByteBuf buf) {
    }

    @Override
    public int getPacketSize() {
        return PacketByteBuf.getVarIntSizeBytes(0);
    }

    @Override
    public void fromTag(ListTag tag) {
    }
}

