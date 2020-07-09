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

    public IdListPalette(IdList<T> arg, T object) {
        this.idList = arg;
        this.fallback = object;
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
    public T getByIndex(int i) {
        T object = this.idList.get(i);
        return object == null ? this.fallback : object;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void fromPacket(PacketByteBuf arg) {
    }

    @Override
    public void toPacket(PacketByteBuf arg) {
    }

    @Override
    public int getPacketSize() {
        return PacketByteBuf.getVarIntSizeBytes(0);
    }

    @Override
    public void fromTag(ListTag arg) {
    }
}

