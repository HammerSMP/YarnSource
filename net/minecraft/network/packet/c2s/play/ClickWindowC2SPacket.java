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
import net.minecraft.screen.slot.SlotActionType;

public class ClickWindowC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int syncId;
    private int slot;
    private int clickData;
    private short actionId;
    private ItemStack stack = ItemStack.EMPTY;
    private SlotActionType actionType;

    public ClickWindowC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public ClickWindowC2SPacket(int i, int j, int k, SlotActionType arg, ItemStack arg2, short s) {
        this.syncId = i;
        this.slot = j;
        this.clickData = k;
        this.stack = arg2.copy();
        this.actionId = s;
        this.actionType = arg;
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onClickWindow(this);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.syncId = arg.readByte();
        this.slot = arg.readShort();
        this.clickData = arg.readByte();
        this.actionId = arg.readShort();
        this.actionType = arg.readEnumConstant(SlotActionType.class);
        this.stack = arg.readItemStack();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeByte(this.syncId);
        arg.writeShort(this.slot);
        arg.writeByte(this.clickData);
        arg.writeShort(this.actionId);
        arg.writeEnumConstant(this.actionType);
        arg.writeItemStack(this.stack);
    }

    public int getSyncId() {
        return this.syncId;
    }

    public int getSlot() {
        return this.slot;
    }

    public int getClickData() {
        return this.clickData;
    }

    public short getActionId() {
        return this.actionId;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public SlotActionType getActionType() {
        return this.actionType;
    }
}

