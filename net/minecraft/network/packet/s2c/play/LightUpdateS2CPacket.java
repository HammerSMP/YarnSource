/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.light.LightingProvider;

public class LightUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int chunkX;
    private int chunkZ;
    private int skyLightMask;
    private int blockLightMask;
    private int filledSkyLightMask;
    private int filledBlockLightMask;
    private List<byte[]> skyLightUpdates;
    private List<byte[]> blockLightUpdates;
    private boolean field_25659;

    public LightUpdateS2CPacket() {
    }

    public LightUpdateS2CPacket(ChunkPos arg, LightingProvider arg2, boolean bl) {
        this.chunkX = arg.x;
        this.chunkZ = arg.z;
        this.field_25659 = bl;
        this.skyLightUpdates = Lists.newArrayList();
        this.blockLightUpdates = Lists.newArrayList();
        for (int i = 0; i < 18; ++i) {
            ChunkNibbleArray lv = arg2.get(LightType.SKY).getLightSection(ChunkSectionPos.from(arg, -1 + i));
            ChunkNibbleArray lv2 = arg2.get(LightType.BLOCK).getLightSection(ChunkSectionPos.from(arg, -1 + i));
            if (lv != null) {
                if (lv.isUninitialized()) {
                    this.filledSkyLightMask |= 1 << i;
                } else {
                    this.skyLightMask |= 1 << i;
                    this.skyLightUpdates.add((byte[])lv.asByteArray().clone());
                }
            }
            if (lv2 == null) continue;
            if (lv2.isUninitialized()) {
                this.filledBlockLightMask |= 1 << i;
                continue;
            }
            this.blockLightMask |= 1 << i;
            this.blockLightUpdates.add((byte[])lv2.asByteArray().clone());
        }
    }

    public LightUpdateS2CPacket(ChunkPos pos, LightingProvider lightProvider, int skyLightMask, int blockLightMask, boolean bl) {
        this.chunkX = pos.x;
        this.chunkZ = pos.z;
        this.field_25659 = bl;
        this.skyLightMask = skyLightMask;
        this.blockLightMask = blockLightMask;
        this.skyLightUpdates = Lists.newArrayList();
        this.blockLightUpdates = Lists.newArrayList();
        for (int k = 0; k < 18; ++k) {
            if ((this.skyLightMask & 1 << k) != 0) {
                ChunkNibbleArray lv = lightProvider.get(LightType.SKY).getLightSection(ChunkSectionPos.from(pos, -1 + k));
                if (lv == null || lv.isUninitialized()) {
                    this.skyLightMask &= ~(1 << k);
                    if (lv != null) {
                        this.filledSkyLightMask |= 1 << k;
                    }
                } else {
                    this.skyLightUpdates.add((byte[])lv.asByteArray().clone());
                }
            }
            if ((this.blockLightMask & 1 << k) == 0) continue;
            ChunkNibbleArray lv2 = lightProvider.get(LightType.BLOCK).getLightSection(ChunkSectionPos.from(pos, -1 + k));
            if (lv2 == null || lv2.isUninitialized()) {
                this.blockLightMask &= ~(1 << k);
                if (lv2 == null) continue;
                this.filledBlockLightMask |= 1 << k;
                continue;
            }
            this.blockLightUpdates.add((byte[])lv2.asByteArray().clone());
        }
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.chunkX = buf.readVarInt();
        this.chunkZ = buf.readVarInt();
        this.field_25659 = buf.readBoolean();
        this.skyLightMask = buf.readVarInt();
        this.blockLightMask = buf.readVarInt();
        this.filledSkyLightMask = buf.readVarInt();
        this.filledBlockLightMask = buf.readVarInt();
        this.skyLightUpdates = Lists.newArrayList();
        for (int i = 0; i < 18; ++i) {
            if ((this.skyLightMask & 1 << i) == 0) continue;
            this.skyLightUpdates.add(buf.readByteArray(2048));
        }
        this.blockLightUpdates = Lists.newArrayList();
        for (int j = 0; j < 18; ++j) {
            if ((this.blockLightMask & 1 << j) == 0) continue;
            this.blockLightUpdates.add(buf.readByteArray(2048));
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.chunkX);
        buf.writeVarInt(this.chunkZ);
        buf.writeBoolean(this.field_25659);
        buf.writeVarInt(this.skyLightMask);
        buf.writeVarInt(this.blockLightMask);
        buf.writeVarInt(this.filledSkyLightMask);
        buf.writeVarInt(this.filledBlockLightMask);
        for (byte[] bs : this.skyLightUpdates) {
            buf.writeByteArray(bs);
        }
        for (byte[] cs : this.blockLightUpdates) {
            buf.writeByteArray(cs);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onLightUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getChunkX() {
        return this.chunkX;
    }

    @Environment(value=EnvType.CLIENT)
    public int getChunkZ() {
        return this.chunkZ;
    }

    @Environment(value=EnvType.CLIENT)
    public int getSkyLightMask() {
        return this.skyLightMask;
    }

    @Environment(value=EnvType.CLIENT)
    public int getFilledSkyLightMask() {
        return this.filledSkyLightMask;
    }

    @Environment(value=EnvType.CLIENT)
    public List<byte[]> getSkyLightUpdates() {
        return this.skyLightUpdates;
    }

    @Environment(value=EnvType.CLIENT)
    public int getBlockLightMask() {
        return this.blockLightMask;
    }

    @Environment(value=EnvType.CLIENT)
    public int getFilledBlockLightMask() {
        return this.filledBlockLightMask;
    }

    @Environment(value=EnvType.CLIENT)
    public List<byte[]> getBlockLightUpdates() {
        return this.blockLightUpdates;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_30006() {
        return this.field_25659;
    }
}

