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

public class UpdateDifficultyLockC2SPacket
implements Packet<ServerPlayPacketListener> {
    private boolean difficultyLocked;

    public UpdateDifficultyLockC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public UpdateDifficultyLockC2SPacket(boolean difficultyLocked) {
        this.difficultyLocked = difficultyLocked;
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onUpdateDifficultyLock(this);
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.difficultyLocked = buf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeBoolean(this.difficultyLocked);
    }

    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }
}

