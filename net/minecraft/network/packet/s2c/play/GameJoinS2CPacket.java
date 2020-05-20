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
import net.minecraft.class_5318;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

public class GameJoinS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int playerEntityId;
    private long seed;
    private boolean hardcore;
    private GameMode gameMode;
    private class_5318.class_5319 dimension;
    private Identifier field_25134;
    private int maxPlayers;
    private int chunkLoadDistance;
    private boolean reducedDebugInfo;
    private boolean showDeathScreen;
    private boolean debugWorld;
    private boolean flatWorld;

    public GameJoinS2CPacket() {
    }

    public GameJoinS2CPacket(int i, GameMode arg, long l, boolean bl, class_5318.class_5319 arg2, Identifier arg3, int j, int k, boolean bl2, boolean bl3, boolean bl4, boolean bl5) {
        this.playerEntityId = i;
        this.dimension = arg2;
        this.field_25134 = arg3;
        this.seed = l;
        this.gameMode = arg;
        this.maxPlayers = j;
        this.hardcore = bl;
        this.chunkLoadDistance = k;
        this.reducedDebugInfo = bl2;
        this.showDeathScreen = bl3;
        this.debugWorld = bl4;
        this.flatWorld = bl5;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.playerEntityId = arg.readInt();
        int i = arg.readUnsignedByte();
        this.hardcore = (i & 8) == 8;
        this.gameMode = GameMode.byId(i &= 0xFFFFFFF7);
        this.dimension = arg.method_29171(class_5318.class_5319.field_25119);
        this.field_25134 = arg.readIdentifier();
        this.seed = arg.readLong();
        this.maxPlayers = arg.readUnsignedByte();
        this.chunkLoadDistance = arg.readVarInt();
        this.reducedDebugInfo = arg.readBoolean();
        this.showDeathScreen = arg.readBoolean();
        this.debugWorld = arg.readBoolean();
        this.flatWorld = arg.readBoolean();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeInt(this.playerEntityId);
        int i = this.gameMode.getId();
        if (this.hardcore) {
            i |= 8;
        }
        arg.writeByte(i);
        arg.method_29172(class_5318.class_5319.field_25119, this.dimension);
        arg.writeIdentifier(this.field_25134);
        arg.writeLong(this.seed);
        arg.writeByte(this.maxPlayers);
        arg.writeVarInt(this.chunkLoadDistance);
        arg.writeBoolean(this.reducedDebugInfo);
        arg.writeBoolean(this.showDeathScreen);
        arg.writeBoolean(this.debugWorld);
        arg.writeBoolean(this.flatWorld);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onGameJoin(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getEntityId() {
        return this.playerEntityId;
    }

    @Environment(value=EnvType.CLIENT)
    public long getSeed() {
        return this.seed;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isHardcore() {
        return this.hardcore;
    }

    @Environment(value=EnvType.CLIENT)
    public GameMode getGameMode() {
        return this.gameMode;
    }

    @Environment(value=EnvType.CLIENT)
    public class_5318 getDimension() {
        return this.dimension;
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier method_29176() {
        return this.field_25134;
    }

    @Environment(value=EnvType.CLIENT)
    public int getChunkLoadDistance() {
        return this.chunkLoadDistance;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean showsDeathScreen() {
        return this.showDeathScreen;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isDebugWorld() {
        return this.debugWorld;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFlatWorld() {
        return this.flatWorld;
    }
}

