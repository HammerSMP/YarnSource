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
import net.minecraft.class_5455;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class GameJoinS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int playerEntityId;
    private long sha256Seed;
    private boolean hardcore;
    private GameMode gameMode;
    private GameMode field_25713;
    private Set<RegistryKey<World>> field_25320;
    private class_5455.class_5457 dimensionTracker;
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

    public GameJoinS2CPacket(int playerEntityId, GameMode gameMode, GameMode arg2, long sha256Seed, boolean hardcore, Set<RegistryKey<World>> set, class_5455.class_5457 arg3, RegistryKey<DimensionType> arg4, RegistryKey<World> arg5, int maxPlayers, int chunkLoadDistance, boolean reducedDebugInfo, boolean showDeathScreen, boolean debugWorld, boolean flatWorld) {
        this.playerEntityId = playerEntityId;
        this.field_25320 = set;
        this.dimensionTracker = arg3;
        this.field_25321 = arg4;
        this.dimensionId = arg5;
        this.sha256Seed = sha256Seed;
        this.gameMode = gameMode;
        this.field_25713 = arg2;
        this.maxPlayers = maxPlayers;
        this.hardcore = hardcore;
        this.chunkLoadDistance = chunkLoadDistance;
        this.reducedDebugInfo = reducedDebugInfo;
        this.showDeathScreen = showDeathScreen;
        this.debugWorld = debugWorld;
        this.flatWorld = flatWorld;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.playerEntityId = buf.readInt();
        this.hardcore = buf.readBoolean();
        this.gameMode = GameMode.byId(buf.readByte());
        this.field_25713 = GameMode.byId(buf.readByte());
        int i = buf.readVarInt();
        this.field_25320 = Sets.newHashSet();
        for (int j = 0; j < i; ++j) {
            this.field_25320.add(RegistryKey.of(Registry.DIMENSION, buf.readIdentifier()));
        }
        this.dimensionTracker = buf.decode(class_5455.class_5457.field_25923);
        this.field_25321 = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, buf.readIdentifier());
        this.dimensionId = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());
        this.sha256Seed = buf.readLong();
        this.maxPlayers = buf.readVarInt();
        this.chunkLoadDistance = buf.readVarInt();
        this.reducedDebugInfo = buf.readBoolean();
        this.showDeathScreen = buf.readBoolean();
        this.debugWorld = buf.readBoolean();
        this.flatWorld = buf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeInt(this.playerEntityId);
        buf.writeBoolean(this.hardcore);
        buf.writeByte(this.gameMode.getId());
        buf.writeByte(this.field_25713.getId());
        buf.writeVarInt(this.field_25320.size());
        for (RegistryKey<World> lv : this.field_25320) {
            buf.writeIdentifier(lv.getValue());
        }
        buf.encode(class_5455.class_5457.field_25923, this.dimensionTracker);
        buf.writeIdentifier(this.field_25321.getValue());
        buf.writeIdentifier(this.dimensionId.getValue());
        buf.writeLong(this.sha256Seed);
        buf.writeVarInt(this.maxPlayers);
        buf.writeVarInt(this.chunkLoadDistance);
        buf.writeBoolean(this.reducedDebugInfo);
        buf.writeBoolean(this.showDeathScreen);
        buf.writeBoolean(this.debugWorld);
        buf.writeBoolean(this.flatWorld);
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
    public GameMode method_30116() {
        return this.field_25713;
    }

    @Environment(value=EnvType.CLIENT)
    public Set<RegistryKey<World>> method_29443() {
        return this.field_25320;
    }

    @Environment(value=EnvType.CLIENT)
    public class_5455 getDimension() {
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

