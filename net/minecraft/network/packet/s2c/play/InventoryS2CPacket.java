/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.collection.DefaultedList;

public class InventoryS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int syncId;
    private List<ItemStack> contents;

    public InventoryS2CPacket() {
    }

    public InventoryS2CPacket(int i, DefaultedList<ItemStack> arg) {
        this.syncId = i;
        this.contents = DefaultedList.ofSize(arg.size(), ItemStack.EMPTY);
        for (int j = 0; j < this.contents.size(); ++j) {
            this.contents.set(j, arg.get(j).copy());
        }
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.syncId = arg.readUnsignedByte();
        int i = arg.readShort();
        this.contents = DefaultedList.ofSize(i, ItemStack.EMPTY);
        for (int j = 0; j < i; ++j) {
            this.contents.set(j, arg.readItemStack());
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeByte(this.syncId);
        arg.writeShort(this.contents.size());
        for (ItemStack lv : this.contents) {
            arg.writeItemStack(lv);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onInventory(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSyncId() {
        return this.syncId;
    }

    @Environment(value=EnvType.CLIENT)
    public List<ItemStack> getContents() {
        return this.contents;
    }
}

