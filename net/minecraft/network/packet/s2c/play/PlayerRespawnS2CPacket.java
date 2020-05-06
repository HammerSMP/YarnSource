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

public class PlayerRespawnS2CPacket
implements Packet<ClientPlayPacketListener> {
    private DimensionType dimension;
    private long sha256Seed;
    private GameMode gameMode;
    private LevelGeneratorType generatorType;
    private boolean field_24451;

    public PlayerRespawnS2CPacket() {
    }

    public PlayerRespawnS2CPacket(DimensionType arg, long l, LevelGeneratorType arg2, GameMode arg3, boolean bl) {
        this.dimension = arg;
        this.sha256Seed = l;
        this.gameMode = arg3;
        this.generatorType = arg2;
        this.field_24451 = bl;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onPlayerRespawn(this);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.dimension = DimensionType.byRawId(arg.readInt());
        this.sha256Seed = arg.readLong();
        this.gameMode = GameMode.byId(arg.readUnsignedByte());
        this.generatorType = LevelGeneratorType.getTypeFromName(arg.readString(16));
        if (this.generatorType == null) {
            this.generatorType = LevelGeneratorType.DEFAULT;
        }
        this.field_24451 = arg.readBoolean();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeInt(this.dimension.getRawId());
        arg.writeLong(this.sha256Seed);
        arg.writeByte(this.gameMode.getId());
        arg.writeString(this.generatorType.getName());
        arg.writeBoolean(this.field_24451);
    }

    @Environment(value=EnvType.CLIENT)
    public DimensionType getDimension() {
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
    public LevelGeneratorType getGeneratorType() {
        return this.generatorType;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_27904() {
        return this.field_24451;
    }
}

