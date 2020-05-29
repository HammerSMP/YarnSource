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
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class PlayerRespawnS2CPacket
implements Packet<ClientPlayPacketListener> {
    private RegistryKey<DimensionType> field_25322;
    private RegistryKey<World> dimension;
    private long sha256Seed;
    private GameMode gameMode;
    private boolean debugWorld;
    private boolean flatWorld;
    private boolean keepPlayerAttributes;

    public PlayerRespawnS2CPacket() {
    }

    public PlayerRespawnS2CPacket(RegistryKey<DimensionType> arg, RegistryKey<World> arg2, long l, GameMode arg3, boolean bl, boolean bl2, boolean bl3) {
        this.field_25322 = arg;
        this.dimension = arg2;
        this.sha256Seed = l;
        this.gameMode = arg3;
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
        this.field_25322 = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, arg.readIdentifier());
        this.dimension = RegistryKey.of(Registry.DIMENSION, arg.readIdentifier());
        this.sha256Seed = arg.readLong();
        this.gameMode = GameMode.byId(arg.readUnsignedByte());
        this.debugWorld = arg.readBoolean();
        this.flatWorld = arg.readBoolean();
        this.keepPlayerAttributes = arg.readBoolean();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeIdentifier(this.field_25322.getValue());
        arg.writeIdentifier(this.dimension.getValue());
        arg.writeLong(this.sha256Seed);
        arg.writeByte(this.gameMode.getId());
        arg.writeBoolean(this.debugWorld);
        arg.writeBoolean(this.flatWorld);
        arg.writeBoolean(this.keepPlayerAttributes);
    }

    @Environment(value=EnvType.CLIENT)
    public RegistryKey<DimensionType> method_29445() {
        return this.field_25322;
    }

    @Environment(value=EnvType.CLIENT)
    public RegistryKey<World> getDimension() {
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

