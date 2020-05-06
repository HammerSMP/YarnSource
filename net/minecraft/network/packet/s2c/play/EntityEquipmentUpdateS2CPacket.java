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
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityEquipmentUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private EquipmentSlot slot;
    private ItemStack stack = ItemStack.EMPTY;

    public EntityEquipmentUpdateS2CPacket() {
    }

    public EntityEquipmentUpdateS2CPacket(int i, EquipmentSlot arg, ItemStack arg2) {
        this.id = i;
        this.slot = arg;
        this.stack = arg2.copy();
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.id = arg.readVarInt();
        this.slot = arg.readEnumConstant(EquipmentSlot.class);
        this.stack = arg.readItemStack();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.id);
        arg.writeEnumConstant(this.slot);
        arg.writeItemStack(this.stack);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onEquipmentUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getStack() {
        return this.stack;
    }

    @Environment(value=EnvType.CLIENT)
    public int getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public EquipmentSlot getSlot() {
        return this.slot;
    }
}

