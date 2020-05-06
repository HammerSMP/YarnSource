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
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public class PlayerInteractBlockC2SPacket
implements Packet<ServerPlayPacketListener> {
    private BlockHitResult field_17602;
    private Hand hand;

    public PlayerInteractBlockC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public PlayerInteractBlockC2SPacket(Hand arg, BlockHitResult arg2) {
        this.hand = arg;
        this.field_17602 = arg2;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.hand = arg.readEnumConstant(Hand.class);
        this.field_17602 = arg.readBlockHitResult();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeEnumConstant(this.hand);
        arg.writeBlockHitResult(this.field_17602);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onPlayerInteractBlock(this);
    }

    public Hand getHand() {
        return this.hand;
    }

    public BlockHitResult getHitY() {
        return this.field_17602;
    }
}

