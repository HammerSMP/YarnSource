/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class TagQueryResponseS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int transactionId;
    @Nullable
    private CompoundTag tag;

    public TagQueryResponseS2CPacket() {
    }

    public TagQueryResponseS2CPacket(int transactionId, @Nullable CompoundTag tag) {
        this.transactionId = transactionId;
        this.tag = tag;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.transactionId = buf.readVarInt();
        this.tag = buf.readCompoundTag();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.transactionId);
        buf.writeCompoundTag(this.tag);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onTagQuery(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getTransactionId() {
        return this.transactionId;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public CompoundTag getTag() {
        return this.tag;
    }

    @Override
    public boolean isWritingErrorSkippable() {
        return true;
    }
}

