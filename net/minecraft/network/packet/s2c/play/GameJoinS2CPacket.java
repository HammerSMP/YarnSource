/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class GameJoinS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int playerEntityId;
    private long sha256Seed;
    private boolean hardcore;
    private GameMode gameMode;
    private Set<RegistryKey<World>> field_25320;
    private RegistryTracker.Modifiable dimensionTracker;
    private RegistryKey<DimensionType> field_25321;
    private RegistryKey<World> dimensionId;
    private int maxPlayers;
    private int chunkLoadDistance;
    private boolean reducedDebugInfo;
    private boolean showDeathScreen;
    private boolean debugWorld;
    private boolean flatWorld;

    public GameJoinS2CPacket() {
    }

    public GameJoinS2CPacket(int i, GameMode arg, long l, boolean bl, Set<RegistryKey<World>> set, RegistryTracker.Modifiable arg2, RegistryKey<DimensionType> arg3, RegistryKey<World> arg4, int j, int k, boolean bl2, boolean bl3, boolean bl4, boolean bl5) {
        this.playerEntityId = i;
        this.field_25320 = set;
        this.dimensionTracker = arg2;
        this.field_25321 = arg3;
        this.dimensionId = arg4;
        this.sha256Seed = l;
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
        int j = arg.readVarInt();
        this.field_25320 = Sets.newHashSet();
        for (int k = 0; k < j; ++k) {
            this.field_25320.add(RegistryKey.of(Registry.DIMENSION, arg.readIdentifier()));
        }
        this.dimensionTracker = arg.decode(RegistryTracker.Modifiable.CODEC);
        this.field_25321 = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, arg.readIdentifier());
        this.dimensionId = RegistryKey.of(Registry.DIMENSION, arg.readIdentifier());
        this.sha256Seed = arg.readLong();
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
        arg.writeVarInt(this.field_25320.size());
        for (RegistryKey<World> lv : this.field_25320) {
            arg.writeIdentifier(lv.getValue());
        }
        arg.encode(RegistryTracker.Modifiable.CODEC, this.dimensionTracker);
        arg.writeIdentifier(this.field_25321.getValue());
        arg.writeIdentifier(this.dimensionId.getValue());
        arg.writeLong(this.sha256Seed);
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
    public long getSha256Seed() {
        return this.sha256Seed;
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
    public Set<RegistryKey<World>> method_29443() {
        return this.field_25320;
    }

    @Environment(value=EnvType.CLIENT)
    public RegistryTracker getDimension() {
        return this.dimensionTracker;
    }

    @Environment(value=EnvType.CLIENT)
    public RegistryKey<DimensionType> method_29444() {
        return this.field_25321;
    }

    @Environment(value=EnvType.CLIENT)
    public RegistryKey<World> getDimensionId() {
        return this.dimensionId;
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

