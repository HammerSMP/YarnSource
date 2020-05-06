/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;

public class HandSwingC2SPacket
implements Packet<ServerPlayPacketListener> {
    private Hand hand;

    public HandSwingC2SPacket() {
    }

    public HandSwingC2SPacket(Hand arg) {
        this.hand = arg;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.hand = arg.readEnumConstant(Hand.class);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeEnumConstant(this.hand);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onHandSwing(this);
    }

    public Hand getHand() {
        return this.hand;
    }
}

