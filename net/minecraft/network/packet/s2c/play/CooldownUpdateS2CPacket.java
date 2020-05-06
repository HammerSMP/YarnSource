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
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class CooldownUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private Item item;
    private int cooldown;

    public CooldownUpdateS2CPacket() {
    }

    public CooldownUpdateS2CPacket(Item arg, int i) {
        this.item = arg;
        this.cooldown = i;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.item = Item.byRawId(arg.readVarInt());
        this.cooldown = arg.readVarInt();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(Item.getRawId(this.item));
        arg.writeVarInt(this.cooldown);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onCooldownUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public Item getItem() {
        return this.item;
    }

    @Environment(value=EnvType.CLIENT)
    public int getCooldown() {
        return this.cooldown;
    }
}

