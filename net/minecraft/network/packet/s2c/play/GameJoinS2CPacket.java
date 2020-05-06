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
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;

public class GameJoinS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int playerEntityId;
    private long seed;
    private boolean hardcore;
    private GameMode gameMode;
    private DimensionType dimension;
    private int maxPlayers;
    private LevelGeneratorType generatorType;
    private int chunkLoadDistance;
    private boolean reducedDebugInfo;
    private boolean showsDeathScreen;

    public GameJoinS2CPacket() {
    }

    public GameJoinS2CPacket(int i, GameMode arg, long l, boolean bl, DimensionType arg2, int j, LevelGeneratorType arg3, int k, boolean bl2, boolean bl3) {
        this.playerEntityId = i;
        this.dimension = arg2;
        this.seed = l;
        this.gameMode = arg;
        this.maxPlayers = j;
        this.hardcore = bl;
        this.generatorType = arg3;
        this.chunkLoadDistance = k;
        this.reducedDebugInfo = bl2;
        this.showsDeathScreen = bl3;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.playerEntityId = arg.readInt();
        int i = arg.readUnsignedByte();
        this.hardcore = (i & 8) == 8;
        this.gameMode = GameMode.byId(i &= 0xFFFFFFF7);
        this.dimension = DimensionType.byRawId(arg.readInt());
        this.seed = arg.readLong();
        this.maxPlayers = arg.readUnsignedByte();
        this.generatorType = LevelGeneratorType.getTypeFromName(arg.readString(16));
        if (this.generatorType == null) {
            this.generatorType = LevelGeneratorType.DEFAULT;
        }
        this.chunkLoadDistance = arg.readVarInt();
        this.reducedDebugInfo = arg.readBoolean();
        this.showsDeathScreen = arg.readBoolean();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeInt(this.playerEntityId);
        int i = this.gameMode.getId();
        if (this.hardcore) {
            i |= 8;
        }
        arg.writeByte(i);
        arg.writeInt(this.dimension.getRawId());
        arg.writeLong(this.seed);
        arg.writeByte(this.maxPlayers);
        arg.writeString(this.generatorType.getName());
        arg.writeVarInt(this.chunkLoadDistance);
        arg.writeBoolean(this.reducedDebugInfo);
        arg.writeBoolean(this.showsDeathScreen);
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
    public DimensionType getDimension() {
        return this.dimension;
    }

    @Environment(value=EnvType.CLIENT)
    public LevelGeneratorType getGeneratorType() {
        return this.generatorType;
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
        return this.showsDeathScreen;
    }
}

