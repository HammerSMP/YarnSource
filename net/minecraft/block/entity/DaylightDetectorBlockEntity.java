/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Tickable;

public class DaylightDetectorBlockEntity
extends BlockEntity
implements Tickable {
    public DaylightDetectorBlockEntity() {
        super(BlockEntityType.DAYLIGHT_DETECTOR);
    }

    @Override
    public void tick() {
        BlockState lv;
        Block lv2;
        if (this.world != null && !this.world.isClient && this.world.getTime() % 20L == 0L && (lv2 = (lv = this.getCachedState()).getBlock()) instanceof DaylightDetectorBlock) {
            DaylightDetectorBlock.updateState(lv, this.world, this.pos);
        }
    }
}

