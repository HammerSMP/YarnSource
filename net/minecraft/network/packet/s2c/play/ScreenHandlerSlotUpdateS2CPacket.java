/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ScreenHandlerSlotUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int syncId;
    private int slot;
    private ItemStack stack = ItemStack.EMPTY;

    public ScreenHandlerSlotUpdateS2CPacket() {
    }

    public ScreenHandlerSlotUpdateS2CPacket(int i, int j, ItemStack arg) {
        this.syncId = i;
        this.slot = j;
        this.stack = arg.copy();
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onScreenHandlerSlotUpdate(this);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.syncId = arg.readByte();
        this.slot = arg.readShort();
        this.stack = arg.readItemStack();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeByte(this.syncId);
        arg.writeShort(this.slot);
        arg.writeItemStack(this.stack);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSyncId() {
        return this.syncId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getSlot() {
        return this.slot;
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getItemStack() {
        return this.stack;
    }
}

