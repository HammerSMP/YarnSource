/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class BoatPaddleStateC2SPacket
implements Packet<ServerPlayPacketListener> {
    private boolean leftPaddling;
    private boolean rightPaddling;

    public BoatPaddleStateC2SPacket() {
    }

    public BoatPaddleStateC2SPacket(boolean bl, boolean bl2) {
        this.leftPaddling = bl;
        this.rightPaddling = bl2;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.leftPaddling = arg.readBoolean();
        this.rightPaddling = arg.readBoolean();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeBoolean(this.leftPaddling);
        arg.writeBoolean(this.rightPaddling);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onBoatPaddleState(this);
    }

    public boolean isLeftPaddling() {
        return this.leftPaddling;
    }

    public boolean isRightPaddling() {
        return this.rightPaddling;
    }
}

