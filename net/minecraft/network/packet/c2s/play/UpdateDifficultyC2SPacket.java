/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.world.Difficulty;

public class UpdateDifficultyC2SPacket
implements Packet<ServerPlayPacketListener> {
    private Difficulty difficulty;

    public UpdateDifficultyC2SPacket() {
    }

    public UpdateDifficultyC2SPacket(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onUpdateDifficulty(this);
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.difficulty = Difficulty.byOrdinal(buf.readUnsignedByte());
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeByte(this.difficulty.getId());
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }
}

