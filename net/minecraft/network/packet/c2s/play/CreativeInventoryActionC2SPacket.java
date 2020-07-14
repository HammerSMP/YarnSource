/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class CreativeInventoryActionC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int slot;
    private ItemStack stack = ItemStack.EMPTY;

    public CreativeInventoryActionC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public CreativeInventoryActionC2SPacket(int slot, ItemStack stack) {
        this.slot = slot;
        this.stack = stack.copy();
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onCreativeInventoryAction(this);
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.slot = buf.readShort();
        this.stack = buf.readItemStack();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeShort(this.slot);
        buf.writeItemStack(this.stack);
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getItemStack() {
        return this.stack;
    }
}

