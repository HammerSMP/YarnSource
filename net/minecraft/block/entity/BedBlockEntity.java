/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BedBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.DyeColor;

public class BedBlockEntity
extends BlockEntity {
    private DyeColor color;

    public BedBlockEntity() {
        super(BlockEntityType.BED);
    }

    public BedBlockEntity(DyeColor color) {
        this();
        this.setColor(color);
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 11, this.toInitialChunkDataTag());
    }

    @Environment(value=EnvType.CLIENT)
    public DyeColor getColor() {
        if (this.color == null) {
            this.color = ((BedBlock)this.getCachedState().getBlock()).getColor();
        }
        return this.color;
    }

    public void setColor(DyeColor color) {
        this.color = color;
    }
}

