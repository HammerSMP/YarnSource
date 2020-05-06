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
import net.minecraft.world.border.WorldBorder;

public class WorldBorderS2CPacket
implements Packet<ClientPlayPacketListener> {
    private Type type;
    private int portalTeleportPosLimit;
    private double centerX;
    private double centerZ;
    private double size;
    private double oldSize;
    private long interpolationDuration;
    private int warningTime;
    private int warningBlocks;

    public WorldBorderS2CPacket() {
    }

    public WorldBorderS2CPacket(WorldBorder arg, Type arg2) {
        this.type = arg2;
        this.centerX = arg.getCenterX();
        this.centerZ = arg.getCenterZ();
        this.oldSize = arg.getSize();
        this.size = arg.getTargetSize();
        this.interpolationDuration = arg.getTargetRemainingTime();
        this.portalTeleportPosLimit = arg.getMaxWorldBorderRadius();
        this.warningBlocks = arg.getWarningBlocks();
        this.warningTime = arg.getWarningTime();
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.type = arg.readEnumConstant(Type.class);
        switch (this.type) {
            case SET_SIZE: {
                this.size = arg.readDouble();
                break;
            }
            case LERP_SIZE: {
                this.oldSize = arg.readDouble();
                this.size = arg.readDouble();
                this.interpolationDuration = arg.readVarLong();
                break;
            }
            case SET_CENTER: {
                this.centerX = arg.readDouble();
                this.centerZ = arg.readDouble();
                break;
            }
            case SET_WARNING_BLOCKS: {
                this.warningBlocks = arg.readVarInt();
                break;
            }
            case SET_WARNING_TIME: {
                this.warningTime = arg.readVarInt();
                break;
            }
            case INITIALIZE: {
                this.centerX = arg.readDouble();
                this.centerZ = arg.readDouble();
                this.oldSize = arg.readDouble();
                this.size = arg.readDouble();
                this.interpolationDuration = arg.readVarLong();
                this.portalTeleportPosLimit = arg.readVarInt();
                this.warningBlocks = arg.readVarInt();
                this.warningTime = arg.readVarInt();
            }
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeEnumConstant(this.type);
        switch (this.type) {
            case SET_SIZE: {
                arg.writeDouble(this.size);
                break;
            }
            case LERP_SIZE: {
                arg.writeDouble(this.oldSize);
                arg.writeDouble(this.size);
                arg.writeVarLong(this.interpolationDuration);
                break;
            }
            case SET_CENTER: {
                arg.writeDouble(this.centerX);
                arg.writeDouble(this.centerZ);
                break;
            }
            case SET_WARNING_TIME: {
                arg.writeVarInt(this.warningTime);
                break;
            }
            case SET_WARNING_BLOCKS: {
                arg.writeVarInt(this.warningBlocks);
                break;
            }
            case INITIALIZE: {
                arg.writeDouble(this.centerX);
                arg.writeDouble(this.centerZ);
                arg.writeDouble(this.oldSize);
                arg.writeDouble(this.size);
                arg.writeVarLong(this.interpolationDuration);
                arg.writeVarInt(this.portalTeleportPosLimit);
                arg.writeVarInt(this.warningBlocks);
                arg.writeVarInt(this.warningTime);
            }
        }
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onWorldBorder(this);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void apply(WorldBorder arg) {
        switch (this.type) {
            case SET_SIZE: {
                arg.setSize(this.size);
                break;
            }
            case LERP_SIZE: {
                arg.interpolateSize(this.oldSize, this.size, this.interpolationDuration);
                break;
            }
            case SET_CENTER: {
                arg.setCenter(this.centerX, this.centerZ);
                break;
            }
            case INITIALIZE: {
                arg.setCenter(this.centerX, this.centerZ);
                if (this.interpolationDuration > 0L) {
                    arg.interpolateSize(this.oldSize, this.size, this.interpolationDuration);
                } else {
                    arg.setSize(this.size);
                }
                arg.setMaxWorldBorderRadius(this.portalTeleportPosLimit);
                arg.setWarningBlocks(this.warningBlocks);
                arg.setWarningTime(this.warningTime);
                break;
            }
            case SET_WARNING_TIME: {
                arg.setWarningTime(this.warningTime);
                break;
            }
            case SET_WARNING_BLOCKS: {
                arg.setWarningBlocks(this.warningBlocks);
            }
        }
    }

    public static enum Type {
        SET_SIZE,
        LERP_SIZE,
        SET_CENTER,
        INITIALIZE,
        SET_WARNING_TIME,
        SET_WARNING_BLOCKS;

    }
}

