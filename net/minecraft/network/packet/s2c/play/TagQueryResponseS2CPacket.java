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

    public TagQueryResponseS2CPacket(int i, @Nullable CompoundTag arg) {
        this.transactionId = i;
        this.tag = arg;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.transactionId = arg.readVarInt();
        this.tag = arg.readCompoundTag();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.transactionId);
        arg.writeCompoundTag(this.tag);
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

