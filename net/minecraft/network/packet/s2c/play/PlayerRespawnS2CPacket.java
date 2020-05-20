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
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

public class PlayerRespawnS2CPacket
implements Packet<ClientPlayPacketListener> {
    private Identifier dimension;
    private long sha256Seed;
    private GameMode gameMode;
    private boolean debugWorld;
    private boolean flatWorld;
    private boolean keepPlayerAttributes;

    public PlayerRespawnS2CPacket() {
    }

    public PlayerRespawnS2CPacket(Identifier arg, long l, GameMode arg2, boolean bl, boolean bl2, boolean bl3) {
        this.dimension = arg;
        this.sha256Seed = l;
        this.gameMode = arg2;
        this.debugWorld = bl;
        this.flatWorld = bl2;
        this.keepPlayerAttributes = bl3;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onPlayerRespawn(this);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.dimension = arg.readIdentifier();
        this.sha256Seed = arg.readLong();
        this.gameMode = GameMode.byId(arg.readUnsignedByte());
        this.debugWorld = arg.readBoolean();
        this.flatWorld = arg.readBoolean();
        this.keepPlayerAttributes = arg.readBoolean();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeIdentifier(this.dimension);
        arg.writeLong(this.sha256Seed);
        arg.writeByte(this.gameMode.getId());
        arg.writeBoolean(this.debugWorld);
        arg.writeBoolean(this.flatWorld);
        arg.writeBoolean(this.keepPlayerAttributes);
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getDimension() {
        return this.dimension;
    }

    @Environment(value=EnvType.CLIENT)
    public long getSha256Seed() {
        return this.sha256Seed;
    }

    @Environment(value=EnvType.CLIENT)
    public GameMode getGameMode() {
        return this.gameMode;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isDebugWorld() {
        return this.debugWorld;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFlatWorld() {
        return this.flatWorld;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldKeepPlayerAttributes() {
        return this.keepPlayerAttributes;
    }
}

